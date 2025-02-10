package com.digitalmoneyhouse.service;

import com.digitalmoneyhouse.model.User;
import com.digitalmoneyhouse.repository.UserRepository;
import com.digitalmoneyhouse.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String authenticate(String email, String password) throws UserNotFoundException, IncorrectPasswordException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException();
        }
        return jwtUtil.generateToken(user);
    }

    public void logout(String token) {
        // Implementar lógica de revocación o invalidación del token
    }

    public static class UserNotFoundException extends Exception { }
    public static class IncorrectPasswordException extends Exception { }
}

