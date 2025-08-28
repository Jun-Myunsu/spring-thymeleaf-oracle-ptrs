package com.posco.ptrs.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PasswordUtil {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    public static String encode(String password) {
        try {
            var salt = generateSalt();
            var md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            var hashedPassword = md.digest(password.getBytes());
            
            var saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("비밀번호 암호화 실패", e);
        }
    }
    
    public static boolean matches(String password, String encodedPassword) {
        try {
            var saltAndHash = Base64.getDecoder().decode(encodedPassword);
            var salt = Arrays.copyOf(saltAndHash, SALT_LENGTH);
            
            var md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            var hashedPassword = md.digest(password.getBytes());
            
            var storedHash = Arrays.copyOfRange(saltAndHash, SALT_LENGTH, saltAndHash.length);
            return Arrays.equals(hashedPassword, storedHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    private static byte[] generateSalt() {
        var salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }
}