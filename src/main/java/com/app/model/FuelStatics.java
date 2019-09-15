package com.app.model;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FuelStatics {
    private String fuelType;
    private Double totalVolume;
    private Double totalPrice;
    private Double avgPrice;
}
