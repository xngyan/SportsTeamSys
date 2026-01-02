package com.sports.controller;

import com.sports.dto.ApiResponse;
import com.sports.dto.LoginRequest;
import com.sports.dto.LoginResponse;
import com.sports.dto.RegisterRequest;
import com.sports.dto.UserDTO;
import com.sports.entity.User;
import com.sports.service.UserService;
import com.sports.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        try {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ApiResponse.error("两次输入的密码不一致");
            }
            UserDTO userDTO = userService.register(
                request.getUsername(),
                request.getPassword(),
                request.getPhoneNum(),
                request.getStuId(),
                request.getLevel()
            );
            return ApiResponse.success("注册成功", userDTO);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .stuId(user.getStuId())
                .phoneNum(user.getPhoneNum())
                .level(user.getLevel())
                .gender(user.getGender() != null ? user.getGender().name() : "OTHER")
                .build();
            
            LoginResponse response = LoginResponse.builder()
                .token(token)
                .user(userDTO)
                .build();
            
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/user-info")
    public ApiResponse<UserDTO> getUserInfo(@RequestAttribute Integer userId) {
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
