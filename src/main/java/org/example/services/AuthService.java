package org.example.services;

import org.example.utils.PasswordEncoder;
import org.example.repositories.AuthRepository;


public class AuthService {
    private final PasswordEncoder passwordUtils;
    private final AuthRepository authRepository;

    public AuthService(PasswordEncoder passwordUtils, AuthRepository authRepository) {
        this.passwordUtils = passwordUtils;
        this.authRepository = authRepository;
    }

    public Boolean registerUser(String username, String password, String publicKey) {
        String hashedPassword = passwordUtils.hashPassword(password);
        return authRepository.saveUser(username, hashedPassword, publicKey);
    }

    public Boolean loginUser(String username, String password) {
        String hashedPassword = authRepository.findPasswordHashedByUser(username);
        if (hashedPassword != null) {
            return passwordUtils.checkPassword(password, hashedPassword);
        }
        return false;
    }
}
