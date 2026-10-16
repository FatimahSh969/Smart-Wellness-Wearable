package com._wellness.wellness.repository;

import com._wellness.wellness.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<SensorReading, Long> {
}