package com.sports.service;

import com.sports.dto.UserDTO;
import com.sports.entity.User;

public interface UserService {
    User findById(Integer id);
    User findByUsername(String username);
    UserDTO register(String username, String password, String phoneNum, String stuId, Integer level) throws Exception;
    User login(String username, String password) throws Exception;
    void updateProfile(Integer userId, String nickname, String stuId, String phoneNum, String avatar, String gender, Integer level) throws Exception;
    UserDTO getUserInfo(Integer userId);
}
