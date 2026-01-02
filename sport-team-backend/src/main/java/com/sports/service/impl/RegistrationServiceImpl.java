package com.sports.service.impl;

import com.sports.entity.Activity;
import com.sports.entity.Registration;
import com.sports.entity.User;
import com.sports.repository.ActivityRepository;
import com.sports.repository.RegistrationRepository;
import com.sports.repository.UserRepository;
import com.sports.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Registration submitRegistration(Integer userId, Integer activityId) throws Exception {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) {
            throw new Exception("活动不存在");
        }

        if (activity.getStatus() != 1) {
            throw new Exception("只能报名招募中的活动");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(activity.getRegistrationDdl())) {
            throw new Exception("报名已截止");
        }

        Optional<Registration> existing = registrationRepository.findByUserIdAndActivityId(userId, activityId);
        if (existing.isPresent()) {
            Registration reg = existing.get();
            if (reg.getStatus() == 1 || reg.getStatus() == 2) {
                throw new Exception("你已经报名过该活动");
            }
            // Re-activate registration
            reg.setStatus(1);
            reg.setRegistrationAt(LocalDateTime.now());
            reg.setCancelAt(null);
            return registrationRepository.save(reg);
        }

        // 检查参与者等级要求
        int userLevel = user.getLevel() != null ? user.getLevel() : 1;
        int minLevel = activity.getMinLevelRequired() != null ? activity.getMinLevelRequired() : 1;
        if (userLevel < minLevel) {
            throw new Exception("你的等级不符合该活动的要求");
        }

        // 检查是否已满人
        long approvedCount = registrationRepository.findByActivityIdAndStatus(activityId, 2).size();
        if (approvedCount >= activity.getMaxParticipants()) {
            throw new Exception("活动已满人");
        }

        Registration registration = Registration.builder()
            .userId(userId)
            .activityId(activityId)
            .status(1)  // 待审核
            .build();

        return registrationRepository.save(registration);
    }

    @Override
    public Registration approveRegistration(Integer registrationId, Integer userId) throws Exception {
        Registration registration = getRegistrationById(registrationId);
        if (registration == null) {
            throw new Exception("报名不存在");
        }

        Activity activity = activityRepository.findById(registration.getActivityId()).orElse(null);
        if (activity == null) {
            throw new Exception("活动不存在");
        }

        if (!activity.getCreatorId().equals(userId)) {
            throw new Exception("只有发布者才能审核报名");
        }

        if (registration.getStatus() != 1) {
            throw new Exception("只能审核待审核的报名");
        }

        // 检查是否已满人
        long approvedCount = registrationRepository.findByActivityIdAndStatus(activity.getId(), 2).size();
        if (approvedCount >= activity.getMaxParticipants()) {
            throw new Exception("活动已满人，无法再通过报名");
        }

        registration.setStatus(2);  // 已通过
        return registrationRepository.save(registration);
    }

    @Override
    public Registration rejectRegistration(Integer registrationId, Integer userId) throws Exception {
        Registration registration = getRegistrationById(registrationId);
        if (registration == null) {
            throw new Exception("报名不存在");
        }

        Activity activity = activityRepository.findById(registration.getActivityId()).orElse(null);
        if (activity == null) {
            throw new Exception("活动不存在");
        }

        if (!activity.getCreatorId().equals(userId)) {
            throw new Exception("只有发布者才能审核报名");
        }

        if (registration.getStatus() != 1) {
            throw new Exception("只能驳回待审核的报名");
        }

        registration.setStatus(3);  // 已驳回
        return registrationRepository.save(registration);
    }

    @Override
    public void approveAllPendingRegistrations(Integer activityId, Integer userId) throws Exception {
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) {
            throw new Exception("活动不存在");
        }

        if (!activity.getCreatorId().equals(userId)) {
            throw new Exception("只有发布者才能审核报名");
        }

        List<Registration> pendingRegistrations = registrationRepository.findByActivityIdAndStatus(activityId, 1);
        if (pendingRegistrations.isEmpty()) {
            return;
        }

        long approvedCount = registrationRepository.findByActivityIdAndStatus(activityId, 2).size();
        long remainingSlots = activity.getMaxParticipants() - approvedCount;

        if (remainingSlots <= 0) {
            throw new Exception("活动已满人，无法再通过报名");
        }

        for (Registration reg : pendingRegistrations) {
            if (remainingSlots > 0) {
                reg.setStatus(2);
                registrationRepository.save(reg);
                remainingSlots--;
            } else {
                break; // No more slots
            }
        }
    }

    @Override
    public void cancelRegistration(Integer registrationId, Integer userId) throws Exception {
        Registration registration = getRegistrationById(registrationId);
        if (registration == null) {
            throw new Exception("报名不存在");
        }

        if (!registration.getUserId().equals(userId)) {
            throw new Exception("只能取消自己的报名");
        }

        if (registration.getStatus() != 1) {
            throw new Exception("只能取消待审核的报名");
        }

        registration.setStatus(4);  // 已取消
        registration.setCancelAt(LocalDateTime.now());
        registrationRepository.save(registration);
    }

    @Override
    public void cancelRegistrationByActivityId(Integer userId, Integer activityId) throws Exception {
        Registration registration = registrationRepository.findByUserIdAndActivityId(userId, activityId)
                .orElseThrow(() -> new Exception("未找到该活动的报名信息"));

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new Exception("活动不存在"));

        if (activity.getStartAt().isBefore(LocalDateTime.now())) {
            throw new Exception("活动已开始，无法取消报名");
        }

        // Allow cancelling Pending (1) or Approved (2)
        if (registration.getStatus() != 1 && registration.getStatus() != 2) {
             throw new Exception("当前状态无法取消报名");
        }

        registration.setStatus(4); // Cancelled
        registration.setCancelAt(LocalDateTime.now());
        registrationRepository.save(registration);
    }

    @Override
    public List<Registration> getActivityRegistrations(Integer activityId, Integer userId) throws Exception {
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) {
            throw new Exception("活动不存在");
        }

        if (activity.getCreatorId().equals(userId)) {
            return registrationRepository.findByActivityId(activityId);
        } else {
            return registrationRepository.findByUserIdAndActivityId(userId, activityId)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        }
    }

    @Override
    public List<Registration> getUserRegistrations(Integer userId) {
        return registrationRepository.findByUserId(userId);
    }

    @Override
    public Registration getRegistrationById(Integer id) {
        return registrationRepository.findById(id).orElse(null);
    }
}
