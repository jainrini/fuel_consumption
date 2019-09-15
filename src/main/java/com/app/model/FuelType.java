package com.app.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="FuelType")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer typeid;

    @Column(name="price")
    @NotNull
    private Double price;

    @Column(name="typename")
    @NotNull
    @NotBlank
    private String typename;

}
