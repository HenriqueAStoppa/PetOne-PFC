package com.petone.petone.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário simples para criptografar e verificar senhas usando BCrypt.
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Criptografa uma senha (raw).
     * @param rawPassword A senha pura.
     * @return O hash BCrypt.
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * Compara uma senha (raw) com um hash (encoded).
     * @param rawPassword A senha pura digitada pelo usuário.
     * @param encodedPassword O hash salvo no banco.
     * @return true se as senhas baterem, false caso contrário.
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}