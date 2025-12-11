package org.example.lab1.config;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class JwtKeyProvider {

    private static final String PRIVATE_KEY_PATH = "/keys/ec-private-pkcs8.pem";
    private static final String PUBLIC_KEY_PATH = "/keys/ec-public.pem";

    public PrivateKey loadPrivateKey() {
        try (InputStream is = getClass().getResourceAsStream(PRIVATE_KEY_PATH)) {
            if (is == null) throw new IllegalStateException("Private key not found: " + PRIVATE_KEY_PATH);
            String pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            String base64 = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(base64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("EC").generatePrivate(spec);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load EC private key", ex);
        }
    }

    public PublicKey loadPublicKey() {
        try (InputStream is = getClass().getResourceAsStream(PUBLIC_KEY_PATH)) {
            if (is == null) throw new IllegalStateException("Public key not found: " + PUBLIC_KEY_PATH);
            String pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            String base64 = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(base64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("EC").generatePublic(spec);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load EC public key", ex);
        }
    }
}
