package com.taskmanager.service.impl;

import com.taskmanager.dto.*;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.model.User;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.security.JwtUtil;
import com.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO requestDTO) {
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(jwtUtil.getExpiration())
                .build();
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),
                        requestDTO.getPassword()
                )
        );

        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(jwtUtil.getExpiration())
                .build();
    }

    @Override
    public void logout(String token) {
        // Store token in Redis blacklist until it expires
        redisTemplate.opsForValue().set(
                "blacklist:" + token,
                "true",
                jwtUtil.getExpiration(),
                TimeUnit.MILLISECONDS
        );
    }
}