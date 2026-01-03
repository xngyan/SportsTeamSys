package com.sports.service.impl;

import com.sports.dto.UserDTO;
import com.sports.entity.User;
import com.sports.repository.UserRepository;
import com.sports.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserDTO register(String username, String password, String phoneNum, String stuId, Integer level) throws Exception {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("用户名已存在");
        }
        if (stuId != null && userRepository.findByStuId(stuId).isPresent()) {
            throw new Exception("学号已被注册");
        }
        if (phoneNum != null && userRepository.findByPhoneNum(phoneNum).isPresent()) {
            throw new Exception("电话号码已被注册");
        }

        User user = User.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .phoneNum(phoneNum)
            .stuId(stuId)
            .level(level != null ? level : 1)
            .gender(User.Gender.OTHER)
            .build();

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public User login(String username, String password) throws Exception {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new Exception("用户名或密码错误");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("用户名或密码错误");
        }

        return user;
    }

    @Override
    public void updateProfile(Integer userId, String nickname, String stuId, String phoneNum, String avatar, String gender, Integer level) throws Exception {
        User user = findById(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        if (nickname != null && !nickname.isEmpty()) {
            user.setUsername(nickname);
        }
        if (stuId != null && !stuId.isEmpty()) {
            if (!stuId.equals(user.getStuId()) && userRepository.findByStuId(stuId).isPresent()) {
                throw new Exception("学号已被使用");
            }
            user.setStuId(stuId);
        }
        if (phoneNum != null && !phoneNum.isEmpty()) {
            if (!phoneNum.equals(user.getPhoneNum()) && userRepository.findByPhoneNum(phoneNum).isPresent()) {
                throw new Exception("电话号码已被使用");
            }
            user.setPhoneNum(phoneNum);
        }
        if (avatar != null && !avatar.isEmpty()) {
            user.setAvatar(avatar);
        }
        if (gender != null && !gender.isEmpty()) {
            user.setGender(User.Gender.valueOf(gender.toUpperCase()));
        }
        if (level != null) {
            user.setLevel(level);
        }

        userRepository.save(user);
    }

    @Override
    public UserDTO getUserInfo(Integer userId) {
        User user = findById(userId);
        if (user == null) {
            return null;
        }
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .avatar(user.getAvatar())
            .stuId(user.getStuId())
            .phoneNum(user.getPhoneNum())
            .level(user.getLevel())
            .gender(user.getGender() != null ? user.getGender().name() : "OTHER")
            .build();
    }
}
