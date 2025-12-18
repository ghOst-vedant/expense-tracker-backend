package com.expensetracker.service.impl;

import com.expensetracker.UserMapper;

import com.expensetracker.entity.User;
import com.expensetracker.exceptionHandler.EmailAlreadyExistsException;
import com.expensetracker.model.UserRequestDTO;
import com.expensetracker.model.UserResponseDTO;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements IUserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO signUp(UserRequestDTO userDTO) {

    if (userRepository.existsByEmail(userDTO.getEmail())){
        throw new EmailAlreadyExistsException("User with email:"+userDTO.getEmail()+" already exists.");
    }
    log.info("Creating user: {}.",userDTO.getEmail());

    User user = UserMapper.toUser(userDTO);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    User savedUser = userRepository.save(user);
    log.info("User: {} created.",savedUser.getEmail());
    return UserMapper.toUserResponseDTO(savedUser);

    }
}
