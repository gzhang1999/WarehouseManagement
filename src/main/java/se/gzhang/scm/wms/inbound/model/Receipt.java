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

package se.gzhang.scm.wms.inbound.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import se.gzhang.scm.wms.common.model.Carrier;
import se.gzhang.scm.wms.common.model.Supplier;
import se.gzhang.scm.wms.common.model.Trailer;
import se.gzhang.scm.wms.common.model.TrailerSerializer;
import se.gzhang.scm.wms.layout.model.Warehouse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "receipt")
@JsonSerialize(using = ReceiptSerializer.class)
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "receipt_id")
    private Integer id;

    @Column(name = "external_id",unique=true)
    private String externalID;

    @Column(name = "number",unique=true)
    private String number;

    @Column(name = "purchase_order_number")
    private String purchaseOrderNumber;

    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name="supplier_id")
    private Supplier supplier;



    @OneToMany(
            mappedBy = "receipt",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("line_number asc")
    private List<ReceiptLine> receiptLineList = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trailer_id")
    private Trailer trailer;


}
