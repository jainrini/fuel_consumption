package com.app.repository;

import com.app.model.FuelConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelConsumptionRepository extends JpaRepository<FuelConsumption,Integer> {
}
