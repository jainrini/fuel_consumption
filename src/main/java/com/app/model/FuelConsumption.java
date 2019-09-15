package com.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name="FuelConsumption")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Valid
public class FuelConsumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fid;

    @Column(name="driverid")
    @NotNull
    private Integer driverid;

    @Column(name="fueltype")
    @NotNull
    @NotBlank
    @Length(min=1)
    private String fueltype;

    @Column(name = "price")
    @NotNull
    private Double price;

    @Column(name = "volume")
    @NotNull
    private Double volume;

    @Column(name="date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate date;

    @Transient
    private Double moneySpent;

    @Transient
    @JsonIgnore
    private String month;


    @Override
    public String toString() {
        return this.fueltype.toString()+","+this.moneySpent.toString();
    }
}
