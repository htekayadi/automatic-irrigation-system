package com.automatic.irrigation.repository;

import com.automatic.irrigation.model.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeslotRepository extends JpaRepository<Timeslot, String>, JpaSpecificationExecutor<Timeslot> {

}
