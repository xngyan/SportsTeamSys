package com.sports.service;

import com.sports.dto.ActivityDTO;
import com.sports.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityService {
    Activity createActivity(ActivityDTO dto, Integer creatorId) throws Exception;
    Activity getActivityById(Integer id);
    Page<Activity> searchActivities(String keyword, String sportType, String location, Integer status, Pageable pageable);
    List<Activity> getUserActivities(Integer userId);
    Activity updateActivity(Integer id, ActivityDTO dto, Integer userId) throws Exception;
    void deleteActivity(Integer id, Integer userId) throws Exception;
    void updateActivityStatus(Integer id) throws Exception;
}
