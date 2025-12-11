package com.expensetracker;

import com.expensetracker.entity.User;
import com.expensetracker.model.UserRequestDTO;
import com.expensetracker.model.UserResponseDTO;

public class UserMapper {

    public static User toUser(UserRequestDTO userDTO){
        User user = new User();

        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        return user;
    }

    public static UserResponseDTO toUserResponseDTO(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }
}
