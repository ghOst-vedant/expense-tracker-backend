package com.expensetracker;

import com.expensetracker.entity.User;
import com.expensetracker.model.UserRequestDTO;
import com.expensetracker.model.UserResponseDTO;

import java.time.LocalDateTime;

public class UserMapper {

    public static User toUser(UserRequestDTO userDTO){
        User user = new User();

        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public static UserResponseDTO toUserResposeDTO(User user){
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId(user.getId());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setUsername(user.getUsername());

        return userResponseDTO;
    }
}
