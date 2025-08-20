package org.example.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncoder {
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public Boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
