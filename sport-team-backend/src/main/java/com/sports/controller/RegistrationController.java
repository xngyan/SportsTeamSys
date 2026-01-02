package com.sports.controller;

import com.sports.dto.ActivityDTO;
import com.sports.dto.ApiResponse;
import com.sports.dto.RegistrationDTO;
import com.sports.dto.UserDTO;
import com.sports.entity.Activity;
import com.sports.entity.Registration;
import com.sports.entity.User;
import com.sports.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public ApiResponse<RegistrationDTO> submitRegistration(
        @RequestParam Integer activityId,
        @RequestAttribute Integer userId
    ) {
        try {
            Registration registration = registrationService.submitRegistration(userId, activityId);
            return ApiResponse.success("报名成功", convertToDTO(registration));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ApiResponse<String> cancelRegistration(
        @RequestParam Integer activityId,
        @RequestAttribute Integer userId
    ) {
        try {
            registrationService.cancelRegistrationByActivityId(userId, activityId);
            return ApiResponse.success("取消报名成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/my-registrations")
    public ApiResponse<List<RegistrationDTO>> getMyRegistrations(@RequestAttribute Integer userId) {
        try {
            List<Registration> registrations = registrationService.getUserRegistrations(userId);
            List<RegistrationDTO> dtos = registrations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/activity/{activityId}")
    public ApiResponse<List<RegistrationDTO>> getActivityRegistrations(
        @PathVariable Integer activityId,
        @RequestAttribute Integer userId
    ) {
        try {
            List<Registration> registrations = registrationService.getActivityRegistrations(activityId, userId);
            List<RegistrationDTO> dtos = registrations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<RegistrationDTO> approveRegistration(
        @PathVariable Integer id,
        @RequestAttribute Integer userId
    ) {
        try {
            Registration registration = registrationService.approveRegistration(id, userId);
            return ApiResponse.success("报名已通过", convertToDTO(registration));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<RegistrationDTO> rejectRegistration(
        @PathVariable Integer id,
        @RequestAttribute Integer userId
    ) {
        try {
            Registration registration = registrationService.rejectRegistration(id, userId);
            return ApiResponse.success("报名已驳回", convertToDTO(registration));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/approve-all")
    public ApiResponse<String> approveAllRegistrations(
        @RequestParam Integer activityId,
        @RequestAttribute Integer userId
    ) {
        try {
            registrationService.approveAllPendingRegistrations(activityId, userId);
            return ApiResponse.success("批量通过成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<String> cancelRegistrationById(
        @PathVariable Integer id,
        @RequestAttribute Integer userId
    ) {
        try {
            registrationService.cancelRegistration(id, userId);
            return ApiResponse.success("报名已取消");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 1: return "待审核";
            case 2: return "已通过";
            case 3: return "已驳回";
            case 4: return "已取消";
            default: return "未知";
        }
    }

    private RegistrationDTO convertToDTO(Registration registration) {
        Activity activity = registration.getActivity();
        ActivityDTO activityDTO = null;
        if (activity != null) {
            activityDTO = ActivityDTO.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .sportType(activity.getSportType())
                .startAt(activity.getStartAt())
                .spotAddress(activity.getSpot() != null ? activity.getSpot().getAddress() : "")
                .status(activity.getStatus())
                .build();
        }

        User user = registration.getUser();
        UserDTO userDTO = null;
        if (user != null) {
            userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phoneNum(user.getPhoneNum())
                .build();
        }

        return RegistrationDTO.builder()
            .id(registration.getId())
            .userId(registration.getUserId())
            .activityId(registration.getActivityId())
            .status(registration.getStatus())
            .statusText(getStatusText(registration.getStatus()))
            .registrationAt(registration.getRegistrationAt() != null ? registration.getRegistrationAt().toString() : "")
            .activityInfo(activityDTO)
            .userInfo(userDTO)
            .build();
    }
}
