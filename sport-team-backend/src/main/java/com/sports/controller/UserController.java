package com.sports.controller;

import com.sports.dto.ApiResponse;
import com.sports.dto.UserDTO;
import com.sports.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUser(@PathVariable Integer id) {
        try {
            UserDTO userDTO = userService.getUserInfo(id);
            if (userDTO == null) {
                return ApiResponse.error("用户不存在");
            }
            return ApiResponse.success(userDTO);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ApiResponse<String> updateProfile(
        @RequestAttribute Integer userId,
        @RequestParam(required = false) String nickname,
        @RequestParam(required = false) String phoneNum,
        @RequestParam(required = false) String avatar,
        @RequestParam(required = false) String gender,
        @RequestParam(required = false) Integer level
    ) {
        try {
            userService.updateProfile(userId, nickname, phoneNum, avatar, gender, level);
            return ApiResponse.success("个人信息更新成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ApiResponse<UserDTO> getProfile(@RequestAttribute Integer userId) {
        try {
            UserDTO userDTO = userService.getUserInfo(userId);
            if (userDTO == null) {
                return ApiResponse.error("用户不存在");
            }
            return ApiResponse.success(userDTO);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
