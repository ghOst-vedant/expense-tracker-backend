package com.expensetracker.service;

import com.expensetracker.model.UserRequestDTO;
import com.expensetracker.model.UserResponseDTO;

public interface IUserService {

    UserResponseDTO signUp(UserRequestDTO userDTO);
}
