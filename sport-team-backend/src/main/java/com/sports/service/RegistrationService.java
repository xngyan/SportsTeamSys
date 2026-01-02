package com.sports.service;

import com.sports.entity.Registration;

import java.util.List;

public interface RegistrationService {
    Registration submitRegistration(Integer userId, Integer activityId) throws Exception;
    Registration approveRegistration(Integer registrationId, Integer userId) throws Exception;
    Registration rejectRegistration(Integer registrationId, Integer userId) throws Exception;
    void cancelRegistration(Integer registrationId, Integer userId) throws Exception;
    void cancelRegistrationByActivityId(Integer userId, Integer activityId) throws Exception;
    void approveAllPendingRegistrations(Integer activityId, Integer userId) throws Exception;
    List<Registration> getActivityRegistrations(Integer activityId, Integer userId) throws Exception;
    List<Registration> getUserRegistrations(Integer userId);
    Registration getRegistrationById(Integer id);
}
