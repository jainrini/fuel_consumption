package com.app.model;

import lombok.*;

import javax.persistence.*;

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
    private Double price;

    @Column(name="typename")
    private String typename;

}
