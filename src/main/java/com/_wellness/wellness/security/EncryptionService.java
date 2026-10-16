package com._wellness.wellness.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EncryptionService {

    private static final int IV_LENGTH = 12;
    private static final int TAG_BITS = 128;

    @Value("${WELLNESS_AES_KEY:}")
    private String base64Key;

    private SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    @PostConstruct
    void init() {
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException("WELLNESS_AES_KEY is not set");
        }
        byte[] k = Base64.getDecoder().decode(base64Key);
        if (k.length != 32) {
            throw new IllegalStateException("AES key must be 256 bits");
        }
        key = new SecretKeySpec(k, "AES");
    }

    public String encrypt(String plainText) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] ct = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] out = ByteBuffer.allocate(iv.length + ct.length).put(iv).put(ct).array();
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed");
        }
    }

    public String decrypt(String encoded) {
        try {
            byte[] all = Base64.getDecoder().decode(encoded);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, all, 0, IV_LENGTH));
            byte[] pt = cipher.doFinal(all, IV_LENGTH, all.length - IV_LENGTH);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed");
        }
    }
}