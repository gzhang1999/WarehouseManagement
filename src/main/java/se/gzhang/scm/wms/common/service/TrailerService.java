/**
 * Copyright 2018
 *
 * @author gzhang
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.gzhang.scm.wms.common.service;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.gzhang.scm.wms.common.model.Carrier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.common.model.TrailerState;
import se.gzhang.scm.wms.common.model.TrailerType;
import se.gzhang.scm.wms.common.repository.TrailerRepository;
import se.gzhang.scm.wms.exception.GenericException;
import se.gzhang.scm.wms.exception.Inbound.ReceiptException;
import se.gzhang.scm.wms.exception.StandProductException;
import se.gzhang.scm.wms.exception.common.TrailerException;
import se.gzhang.scm.wms.inbound.model.Receipt;
import se.gzhang.scm.wms.layout.model.AreaType;
import se.gzhang.scm.wms.layout.model.Location;
import se.gzhang.scm.wms.reporting.model.ReportArchive;
import se.gzhang.scm.wms.reporting.service.TrailerReportService;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Service
public class TrailerService {

    @Autowired
    TrailerRepository trailerRepository;

    @Autowired
    TrailerReportService trailerReportService;

    private Map<TrailerState, List<TrailerState>> validTrailerStateTransfer;

    // all the valid area that the trailer can park
    private List<AreaType> validTrailerParkingAreaType;

    public TrailerService() {
        validTrailerStateTransfer = loadValidTrailerStateTransfer();
        validTrailerParkingAreaType = loadValidTrailerParkingAreaType();
    }

    private List<AreaType> loadValidTrailerParkingAreaType() {

        List<AreaType> validTrailerParkingAreaType = new ArrayList<>();
        validTrailerParkingAreaType.add(AreaType.DOCKDOOR);
        validTrailerParkingAreaType.add(AreaType.YARD);
        return validTrailerParkingAreaType;
    }

    private Map<TrailerState, List<TrailerState>> loadValidTrailerStateTransfer() {
        // from              to
        // EXPECTED        CHECKED_IN
        // EXPECTED        VOID
        // CHECKED_IN      PROCESSING
        // CHECKED_IN      DISPATCHED
        // PROCESSING      CLOSED
        // PROCESSING      VOID          // Only if there's no receiving / shipping activity yet
        // CLOSED          DISPATCH
        // CLOSED          VOID          // Only if there's no receiving / shipping activity yet
        Map<TrailerState, List<TrailerState>> validTrailerStateTransfer = new HashMap<>();
        TrailerState[] destinationTrailerState = new TrailerState[]{TrailerState.CHECKED_IN, TrailerState.VOID};
        validTrailerStateTransfer.put(TrailerState.EXPECTED, Arrays.asList(destinationTrailerState));

        destinationTrailerState = new TrailerState[]{TrailerState.PROCESSING, TrailerState.DISPATCHED};
        validTrailerStateTransfer.put(TrailerState.CHECKED_IN, Arrays.asList(destinationTrailerState));

        destinationTrailerState = new TrailerState[]{TrailerState.CLOSED, TrailerState.VOID};
        validTrailerStateTransfer.put(TrailerState.PROCESSING, Arrays.asList(destinationTrailerState));

        destinationTrailerState = new TrailerState[]{TrailerState.DISPATCHED, TrailerState.VOID};
        validTrailerStateTransfer.put(TrailerState.CLOSED, Arrays.asList(destinationTrailerState));

        return validTrailerStateTransfer;
    }

    public List<Trailer> findAll(){

        return trailerRepository.findAll();
    }

    public Trailer findByTrailerId(int id){
        return trailerRepository.findById(id);
    }

    public List<Trailer> findByTrailerNumber(String number) {
        return trailerRepository.findByTrailerNumber(number);
    }

    @Transactional
    public Trailer save(Trailer trailer) {
        // If the trailer doesn't have state yet, let's always
        // assume it is in 'Expected' status
        if (trailer.getTrailerState() == null) {
            // Trailer always start with expected status.
            trailer.setTrailerState(TrailerState.EXPECTED);
        }
        return trailerRepository.save(trailer);
    }


    public List<Trailer> findTrailers(Map<String, String> criteriaList) {
        return trailerRepository.findAll(new Specification<Trailer>() {
            @Override
            public Predicate toPredicate(Root<Trailer> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if(criteriaList.containsKey("id") && !criteriaList.get("id").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), criteriaList.get("id")));
                }

                // Query receiving trailer by receipt
                if ((criteriaList.containsKey("receiptNumber") && !criteriaList.get("receiptNumber").isEmpty()) ||
                        (criteriaList.containsKey("receiptExternalID") && !criteriaList.get("receiptExternalID").isEmpty()) ||
                        (criteriaList.containsKey("receiptID") && !criteriaList.get("receiptID").isEmpty())) {
                    Join<Trailer, Receipt> joinReceipt = root.join("receipt",JoinType.INNER);

                    if(criteriaList.containsKey("receiptNumber") && !criteriaList.get("receiptNumber").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReceipt.get("receiptNumber"), criteriaList.get("receiptNumber")));
                    }
                    if(criteriaList.containsKey("receiptExternalID") && !criteriaList.get("receiptExternalID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReceipt.get("externalID"), criteriaList.get("receiptExternalID")));
                    }
                    if(criteriaList.containsKey("receiptID") && !criteriaList.get("receiptID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinReceipt.get("id"), criteriaList.get("receiptID")));
                    }
                }

                if(criteriaList.containsKey("trailerType") && !criteriaList.get("trailerType").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("trailerType"), TrailerType.valueOf(criteriaList.get("trailerType"))));
                }
                if(criteriaList.containsKey("driver") && !criteriaList.get("driver").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("driver"), TrailerType.valueOf(criteriaList.get("driver"))));
                }
                if(criteriaList.containsKey("licensePlate") && !criteriaList.get("licensePlate").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("licensePlate"), TrailerType.valueOf(criteriaList.get("licensePlate"))));
                }

                // Query by carrier
                if ((criteriaList.containsKey("carrierID") && !criteriaList.get("carrierID").isEmpty()) ||
                        (criteriaList.containsKey("carrierName") && !criteriaList.get("carrierName").isEmpty())) {
                    Join<Trailer, Carrier> joinCarrier = root.join("carrier",JoinType.INNER);

                    if(criteriaList.containsKey("carrierID") && !criteriaList.get("carrierID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinCarrier.get("id"), criteriaList.get("carrierID")));
                    }
                    if(criteriaList.containsKey("carrierName") && !criteriaList.get("carrierName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinCarrier.get("name"), criteriaList.get("carrierName")));
                    }
                }

                if(criteriaList.containsKey("trailerState") && !criteriaList.get("trailerState").isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("trailerState"), TrailerState.valueOf(criteriaList.get("trailerState"))));
                }

                // Query by yard location
                if ((criteriaList.containsKey("locationID") && !criteriaList.get("locationID").isEmpty()) ||
                        (criteriaList.containsKey("locationName") && !criteriaList.get("locationName").isEmpty())) {
                    Join<Trailer, Location> joinLocation = root.join("location",JoinType.INNER);

                    if(criteriaList.containsKey("locationID") && !criteriaList.get("locationID").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinLocation.get("id"), criteriaList.get("locationID")));
                    }
                    if(criteriaList.containsKey("locationName") && !criteriaList.get("locationName").isEmpty()) {
                        predicates.add(criteriaBuilder.equal(joinLocation.get("name"), criteriaList.get("locationName")));
                    }
                }

                Predicate[] p = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(p));

            }
        });
    }

    @Transactional
    public void deleteByTrailerID(int trailerID) {
        Trailer trailer = findByTrailerId(trailerID);
        if (trailer.getTrailerState() != TrailerState.EXPECTED) {
            throw ReceiptException.TRAILER_NOT_VALID_STATE;
        }
        trailerRepository.deleteById(trailerID);
    }

    @Transactional
    public Trailer voidByTrailerID(int trailerID) {
        Trailer trailer = findByTrailerId(trailerID);
        if (trailer.getTrailerState() != TrailerState.EXPECTED) {
            throw ReceiptException.TRAILER_NOT_VALID_STATE;
        }
        return setTrailerState(trailer, TrailerState.VOID);
    }

    @Transactional
    public Trailer checkInTrailer(int trailerID) {
        return checkInTrailer(findByTrailerId(trailerID));
    }
    @Transactional
    public Trailer closeTrailer(int trailerID) {
        return closeTrailer(findByTrailerId(trailerID));
    }
    @Transactional
    public Trailer dispatchTrailer(int trailerID) {
        return dispatchTrailer(findByTrailerId(trailerID));
    }

    @Transactional
    public Trailer checkInTrailer(Trailer trailer) {
        trailer.setCheckedInDate(new Date());
        return setTrailerState(trailer, TrailerState.CHECKED_IN);
    }
    @Transactional
    public Trailer closeTrailer(Trailer trailer) {
        trailer.setClosedDate(new Date());
        return setTrailerState(trailer, TrailerState.CLOSED);
    }
    @Transactional
    public Trailer dispatchTrailer(Trailer trailer) {
        trailer.setDispatchedDate(new Date());
        return setTrailerState(trailer, TrailerState.DISPATCHED);
    }

    // The trailer is only allowed to be moved to
    // 1. Yard for parking
    // 2. Dock for processing
    @Transactional
    public Trailer moveTrailer(Trailer trailer, Location location) {
        if (!isLocationValidForParking(location)) {
            throw TrailerException.INVALID_LOCATION_FOR_TRAILER;
        }
        trailer.setLocation(location);
        return save(trailer);
    }

    private boolean isLocationValidForParking(Location location) {
        if (validTrailerParkingAreaType.contains(location.getArea().getAreaType())) {
            return true;
        }
        else  {
            return false;
        }

    }


    public Trailer setTrailerState(Trailer trailer, TrailerState trailerState) {

        if (!isDestinationTrailerStateValid(trailer, trailerState)) {
            throw new StandProductException("ReceiptException.NotValidDestinationState", "Cannot transfer current trailer to the state: " + trailerState.name());
        }
        trailer.setTrailerState(trailerState);
        return save(trailer);
    }

    public boolean isDestinationTrailerStateValid(Trailer trailer, TrailerState trailerState) {
        if (validTrailerStateTransfer.containsKey(trailer.getTrailerState()) &&
            validTrailerStateTransfer.get(trailer.getTrailerState()).contains(trailerState)) {
            return true;
        }
        return false;
    }

    public Trailer createTrailer(String trailerType, String trailerNumber,String driver, String driverTelephone,
                                 String licensePlate, Carrier carrier) {
        Trailer trailer = new Trailer();
        trailer.setTrailerType(TrailerType.valueOf(trailerType));
        trailer.setTrailerNumber(trailerNumber);
        trailer.setDriver(driver);
        trailer.setDriverTelephone(driverTelephone);
        trailer.setLicensePlate(licensePlate);
        trailer.setCarrier(carrier);

        // Trailer always start with expected status.
        trailer.setTrailerState(TrailerState.EXPECTED);
        return save(trailer);
    }

    public Trailer createTrailer(String trailerType, String trailerNumber,String driver, String driverTelephone,
                                 String licensePlate) {
        return createTrailer(trailerType, trailerNumber, driver, driverTelephone, licensePlate, null);
    }


    public Trailer changeTrailer(Trailer trailer, String trailerType, String driver, String driverTelephone,
                                 String licensePlate, Carrier carrier) {

        trailer.setTrailerType(TrailerType.valueOf(trailerType));
        trailer.setDriver(driver);
        trailer.setDriverTelephone(driverTelephone);
        trailer.setLicensePlate(licensePlate);
        trailer.setCarrier(carrier);
        return save(trailer);
    }

    public Trailer changeTrailer(Trailer trailer, String trailerType, String driver, String driverTelephone,
                                 String licensePlate) {
        return changeTrailer(trailer, trailerType, driver, driverTelephone, licensePlate, null);
    }

    public List<ReportArchive> printReport(Trailer trailer)
            throws IOException, SQLException, JRException {
        switch (trailer.getTrailerType()){
            case RECEIVING_TRAILER:
                return trailerReportService.printReceivingDocument(trailer);

            case SHIPPING_TRAILER:
                break;
            case STORAGE_TRAILER:
                break;
            default:
                break;
        }
        return new ArrayList<>();
    }

}
