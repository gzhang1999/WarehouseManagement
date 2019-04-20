/**
 * Copyright 2019
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

package se.gzhang.scm.wms.work.model;

import se.gzhang.scm.wms.inventory.model.Inventory;
import se.gzhang.scm.wms.layout.model.Location;

import javax.persistence.*;

@Entity
@Table(name = "work_instruction")
public class WorkInstruction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "work_instruction_id")
    private Integer id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_location_id", referencedColumnName="location_id")
    private Location sourceLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_location_id", referencedColumnName="location_id")
    private Location destinationLocation;


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="inventory_id")
    private Inventory inventory;

    @Column(name = "work_instruction_type")
    private WorkInstructionType workInstructionType;

    @Column(name = "work_instruction_status")
    private WorkInstructionStatus workInstructionStatus;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public WorkInstructionType getWorkInstructionType() {
        return workInstructionType;
    }

    public void setWorkInstructionType(WorkInstructionType workInstructionType) {
        this.workInstructionType = workInstructionType;
    }

    public WorkInstructionStatus getWorkInstructionStatus() {
        return workInstructionStatus;
    }

    public void setWorkInstructionStatus(WorkInstructionStatus workInstructionStatus) {
        this.workInstructionStatus = workInstructionStatus;
    }
}
