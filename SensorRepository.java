
package com._wellness.wellness.repository;

import com._wellness.wellness.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface SensorRepository extends JpaRepository<SensorReading, Long> {

    @Modifying
    @Query("DELETE FROM SensorReading s WHERE s.recordedAt < :cutoff")
    int deleteOldReadings(@Param("cutoff") Instant cutoff);

    @Modifying
    @Query("DELETE FROM SensorReading s WHERE s.userId = :userId")
    int deleteByUserIdValue(@Param("userId") String userId);
}
