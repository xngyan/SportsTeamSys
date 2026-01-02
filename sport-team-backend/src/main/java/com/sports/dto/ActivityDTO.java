package com.sports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {
    private Integer id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "运动类型不能为空")
    private String sportType;

    private String description;

    @NotNull(message = "最大参与人数不能为空")
    private Integer maxParticipants;

    private Integer minLevelRequired;

    @NotNull(message = "报名截止时间不能为空")
    private LocalDateTime registrationDdl;

    @NotNull(message = "活动开始时间不能为空")
    private LocalDateTime startAt;

    @NotNull(message = "活动结束时间不能为空")
    private LocalDateTime endAt;

    @NotNull(message = "赛点不能为空")
    private Integer spotId;

    private Integer status;
    private String spotAddress;
    private Integer creatorId;
    private String creatorName;
    private Integer registrationCount;
    private Integer remainingPlaces;
    private Boolean isRegistered;
    private Integer pendingRegistrationCount;
}
