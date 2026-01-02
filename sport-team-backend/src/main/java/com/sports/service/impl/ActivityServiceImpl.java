package com.sports.service.impl;

import com.sports.dto.ActivityDTO;
import com.sports.entity.Activity;
import com.sports.entity.Registration;
import com.sports.repository.ActivityRepository;
import com.sports.repository.RegistrationRepository;
import com.sports.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Override
    public Activity createActivity(ActivityDTO dto, Integer creatorId) throws Exception {
        if (dto.getMaxParticipants() <= 0) {
            throw new Exception("最大参与人数必须大于0");
        }
        if (dto.getRegistrationDdl().isAfter(dto.getStartAt())) {
            throw new Exception("报名截止时间必须早于活动开始时间");
        }
        if (dto.getStartAt().isAfter(dto.getEndAt())) {
            throw new Exception("活动开始时间必须早于结束时间");
        }

        Activity activity = Activity.builder()
            .creatorId(creatorId)
            .spotId(dto.getSpotId())
            .title(dto.getTitle())
            .sportType(dto.getSportType())
            .description(dto.getDescription())
            .maxParticipants(dto.getMaxParticipants())
            .minLevelRequired(dto.getMinLevelRequired() != null ? dto.getMinLevelRequired() : 1)
            .registrationDdl(dto.getRegistrationDdl())
            .startAt(dto.getStartAt())
            .endAt(dto.getEndAt())
            .status(1)  // 招募中
            .build();

        return activityRepository.save(activity);
    }

    @Override
    public Activity getActivityById(Integer id) {
        Activity activity = activityRepository.findById(id).orElse(null);
        if (activity != null) {
            checkAndUpdateStatus(activity);
        }
        return activity;
    }

    @Override
    public Page<Activity> searchActivities(String keyword, String sportType, String location, Integer status, Pageable pageable) {
        Specification<Activity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 搜索标题或描述
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                    cb.like(root.get("title"), "%" + keyword + "%"),
                    cb.like(root.get("description"), "%" + keyword + "%")
                ));
            }

            // 按运动类型过滤
            if (sportType != null && !sportType.isEmpty()) {
                predicates.add(cb.equal(root.get("sportType"), sportType));
            }

            // 按状态过滤
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            } else {
                // 默认只显示招募中的活动
                // predicates.add(cb.equal(root.get("status"), 1));
                // 修改：不强制过滤状态，或者在前端处理。
                // 如果必须过滤，那么过期的活动在这里会被过滤掉（因为数据库里还是1），
                // 但我们希望在返回前更新状态。
                // 如果这里过滤了status=1，那么过期的活动会被查出来，然后我们更新它为3。
                // 这样用户会看到状态为3的活动。
                // 如果用户只想看招募中，那看到“已截止”可能觉得奇怪，但也是合理的（刚截止）。
                
                // 为了简单起见，这里先保留过滤逻辑，但要注意：
                // 如果数据库里是1，但实际已过期。查出来后，我们更新为3。
                // 那么返回给前端的就是3。
                predicates.add(cb.equal(root.get("status"), 1));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Activity> page = activityRepository.findAll(spec, pageable);
        page.getContent().forEach(this::checkAndUpdateStatus);
        return page;
    }

    @Override
    public List<Activity> getUserActivities(Integer userId) {
        List<Activity> activities = activityRepository.findByCreatorId(userId);
        activities.forEach(this::checkAndUpdateStatus);
        return activities;
    }

    private void checkAndUpdateStatus(Activity activity) {
        boolean changed = false;
        LocalDateTime now = LocalDateTime.now();

        // 检查是否超过报名截止时间
        if (now.isAfter(activity.getRegistrationDdl()) && activity.getStatus() == 1) {
            activity.setStatus(3);  // 已截止
            changed = true;
        }

        // 检查是否已满人
        if (activity.getStatus() == 1) {
            long registeredCount = registrationRepository.findByActivityIdAndStatus(activity.getId(), 2).size();
            if (registeredCount >= activity.getMaxParticipants()) {
                activity.setStatus(2);  // 已满人
                changed = true;
            }
        }

        // 检查活动是否已结束
        if (now.isAfter(activity.getEndAt()) && activity.getStatus() != 4 && activity.getStatus() != 0) {
            activity.setStatus(4);  // 已完成
            changed = true;
        }

        if (changed) {
            activityRepository.save(activity);
        }
    }

    @Override
    public Activity updateActivity(Integer id, ActivityDTO dto, Integer userId) throws Exception {
        Activity activity = getActivityById(id);
        if (activity == null) {
            throw new Exception("活动不存在");
        }
        if (!activity.getCreatorId().equals(userId)) {
            throw new Exception("只能修改自己发布的活动");
        }
        if (activity.getStatus() != 1) {
            throw new Exception("只能修改招募中的活动");
        }

        activity.setTitle(dto.getTitle());
        activity.setSportType(dto.getSportType());
        activity.setDescription(dto.getDescription());
        activity.setMaxParticipants(dto.getMaxParticipants());
        activity.setRegistrationDdl(dto.getRegistrationDdl());
        activity.setStartAt(dto.getStartAt());
        activity.setEndAt(dto.getEndAt());

        return activityRepository.save(activity);
    }

    @Override
    public void deleteActivity(Integer id, Integer userId) throws Exception {
        Activity activity = getActivityById(id);
        if (activity == null) {
            throw new Exception("活动不存在");
        }
        if (!activity.getCreatorId().equals(userId)) {
            throw new Exception("只能删除自己发布的活动");
        }
        if (activity.getStatus() != 1) {
            throw new Exception("只能删除招募中的活动");
        }

        activityRepository.deleteById(id);
    }

    @Override
    public void updateActivityStatus(Integer id) throws Exception {
        Activity activity = getActivityById(id);
        if (activity == null) {
            throw new Exception("活动不存在");
        }

        LocalDateTime now = LocalDateTime.now();

        // 检查是否超过报名截止时间
        if (now.isAfter(activity.getRegistrationDdl()) && activity.getStatus() == 1) {
            activity.setStatus(3);  // 已截止
        }

        // 检查是否已满人
        long registeredCount = registrationRepository.findByActivityIdAndStatus(id, 2).size();
        if (registeredCount >= activity.getMaxParticipants() && activity.getStatus() == 1) {
            activity.setStatus(2);  // 已满人
        }

        // 检查活动是否已结束
        if (now.isAfter(activity.getEndAt())) {
            activity.setStatus(4);  // 已完成
        }

        activityRepository.save(activity);
    }
}
