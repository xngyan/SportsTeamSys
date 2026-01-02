package com.sports.repository;

import com.sports.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer>, JpaSpecificationExecutor<Activity> {
    List<Activity> findByCreatorId(Integer creatorId);
    List<Activity> findBySportType(String sportType);
    List<Activity> findByStatus(Integer status);
    List<Activity> findByStartAtBetween(LocalDateTime start, LocalDateTime end);
}
