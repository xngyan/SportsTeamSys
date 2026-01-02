package com.sports.controller;

import com.sports.dto.ActivityDTO;
import com.sports.dto.ApiResponse;
import com.sports.entity.Activity;
import com.sports.entity.Registration;
import com.sports.repository.RegistrationRepository;
import com.sports.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private RegistrationRepository registrationRepository;

    @PostMapping
    public ApiResponse<ActivityDTO> createActivity(
        @Valid @RequestBody ActivityDTO dto,
        @RequestAttribute Integer userId
    ) {
        try {
            Activity activity = activityService.createActivity(dto, userId);
            return ApiResponse.success("组队发布成功", convertToDTO(activity, userId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ActivityDTO> getActivity(
        @PathVariable Integer id,
        @RequestAttribute(required = false) Integer userId
    ) {
        try {
            Activity activity = activityService.getActivityById(id);
            if (activity == null) {
                return ApiResponse.error("活动不存在");
            }
            return ApiResponse.success(convertToDTO(activity, userId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<Page<ActivityDTO>> searchActivities(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String sportType,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) Integer status,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestAttribute(required = false) Integer userId
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Activity> result = activityService.searchActivities(keyword, sportType, location, status, pageable);
            Page<ActivityDTO> dtoPage = result.map(activity -> convertToDTO(activity, userId));
            return ApiResponse.success(dtoPage);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/my-activities")
    public ApiResponse<List<ActivityDTO>> getMyActivities(@RequestAttribute Integer userId) {
        try {
            List<Activity> activities = activityService.getUserActivities(userId);
            List<ActivityDTO> dtos = activities.stream()
                .map(activity -> convertToDTO(activity, userId))
                .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<ActivityDTO> updateActivity(
        @PathVariable Integer id,
        @Valid @RequestBody ActivityDTO dto,
        @RequestAttribute Integer userId
    ) {
        try {
            Activity activity = activityService.updateActivity(id, dto, userId);
            return ApiResponse.success("活动更新成功", convertToDTO(activity, userId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteActivity(
        @PathVariable Integer id,
        @RequestAttribute Integer userId
    ) {
        try {
            activityService.deleteActivity(id, userId);
            return ApiResponse.success("活动删除成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    private ActivityDTO convertToDTO(Activity activity, Integer userId) {
        boolean isRegistered = false;
        if (userId != null) {
            Optional<Registration> regOpt = registrationRepository.findByUserIdAndActivityId(userId, activity.getId());
            if (regOpt.isPresent()) {
                Integer status = regOpt.get().getStatus();
                isRegistered = (status == 1 || status == 2);
            }
        }
        return ActivityDTO.builder()
            .id(activity.getId())
            .title(activity.getTitle())
            .sportType(activity.getSportType())
            .description(activity.getDescription())
            .maxParticipants(activity.getMaxParticipants())
            .minLevelRequired(activity.getMinLevelRequired())
            .registrationDdl(activity.getRegistrationDdl())
            .startAt(activity.getStartAt())
            .endAt(activity.getEndAt())
            .spotId(activity.getSpotId())
            .status(activity.getStatus())
            .creatorId(activity.getCreatorId())
            .creatorName(activity.getCreator() != null ? activity.getCreator().getUsername() : "")
            .spotAddress(activity.getSpot() != null ? activity.getSpot().getAddress() : "")
            .registrationCount(activity.getRegistrations() != null ? 
                (int) activity.getRegistrations().stream().filter(r -> r.getStatus() == 2).count() : 0)
            .pendingRegistrationCount(activity.getRegistrations() != null ? 
                (int) activity.getRegistrations().stream().filter(r -> r.getStatus() == 1).count() : 0)
            .isRegistered(isRegistered)
            .build();
    }
}
