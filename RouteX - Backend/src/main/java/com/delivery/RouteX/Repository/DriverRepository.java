package com.delivery.RouteX.Repository;

import com.delivery.RouteX.model.Driver;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByUserId(Long userId);

    List<Driver> findByAvailabilityStatus(Driver.AvailabilityStatus status);

    @Query("SELECT d FROM Driver d WHERE d.availabilityStatus = 'ONLINE' " +
            "AND d.currentLatitude BETWEEN :minLat AND :maxLat " +
            "AND d.currentLongitude BETWEEN :minLon AND :maxLon")
    List<Driver> findAvailableDriversInArea(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon
    );

    @Query("SELECT d FROM Driver d WHERE d.availabilityStatus = 'ONLINE' " +
            "ORDER BY d.rating DESC, d.totalDeliveries DESC")
    List<Driver> findTopDrivers(Pageable pageable);

    @Query("SELECT AVG(d.rating) FROM Driver d WHERE d.totalDeliveries > 0")
    Double getAverageDriverRating();
}