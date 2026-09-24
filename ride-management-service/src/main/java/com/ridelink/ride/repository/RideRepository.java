package com.ridelink.ride.repository;

import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByPassengerIdOrderByRequestedAtDesc(Long passengerId);
    List<Ride> findByDriverIdOrderByRequestedAtDesc(Long driverId);
    List<Ride> findByStatus(RideStatus status);
}
