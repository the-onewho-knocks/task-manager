package com.taskmanager.service;

import com.taskmanager.dto.*;

public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO requestDTO);

    AuthResponseDTO login(LoginRequestDTO requestDTO);

    void logout(String token);
}