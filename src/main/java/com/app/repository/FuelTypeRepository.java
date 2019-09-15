package com.app.repository;

import com.app.model.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelTypeRepository  extends JpaRepository<FuelType,Integer> {
}
