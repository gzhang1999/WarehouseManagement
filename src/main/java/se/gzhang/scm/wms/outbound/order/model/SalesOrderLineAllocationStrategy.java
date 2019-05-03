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

package se.gzhang.scm.wms.outbound.order.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sales_order_line_allocation_strategy")
public class SalesOrderLineAllocationStrategy implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_order_line_allocation_strategy_id")
    private Integer id;

    @JoinColumn(name = "sequence")
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_line_id")
    private SalesOrderLine salesOrderLine;

    @JoinColumn(name = "allocation_strategy")
    private AllocationStrategyType allocationStrategyType;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SalesOrderLine getSalesOrderLine() {
        return salesOrderLine;
    }

    public void setSalesOrderLine(SalesOrderLine salesOrderLine) {
        this.salesOrderLine = salesOrderLine;
    }

    public AllocationStrategyType getAllocationStrategyType() {
        return allocationStrategyType;
    }

    public void setAllocationStrategyType(AllocationStrategyType allocationStrategyType) {
        this.allocationStrategyType = allocationStrategyType;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
