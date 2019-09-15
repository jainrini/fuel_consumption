package com.app.model;


import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FuelConsumed {

    private Integer driverId;
    private Double totalAmountSpent;
    private List<FuelConsumption> fuelConsumptionList;
    private List<FuelStatics> statics;
}
