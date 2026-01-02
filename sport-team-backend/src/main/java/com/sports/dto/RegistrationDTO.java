package com.sports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationDTO {
    private Integer id;

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotNull(message = "活动ID不能为空")
    private Integer activityId;

    private Integer status;
    private String statusText;
    private String registrationAt;
    private UserDTO userInfo;
    private ActivityDTO activityInfo;
}
