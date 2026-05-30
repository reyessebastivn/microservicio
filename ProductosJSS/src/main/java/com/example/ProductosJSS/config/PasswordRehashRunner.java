package com.example.ProductosJSS.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.ProductosJSS.repositories.UsuarioRepository;

@Configuration
@ConditionalOnProperty(value = "app.migrate.passwords", havingValue = "true")
public class PasswordRehashRunner {

    @Bean
    CommandLineRunner rehashPlaintextPasswords(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            int migrated = 0;
            for (var u : repo.findAll()) {
                String pwd = u.getPassword() == null ? "" : u.getPassword();
                boolean isBcrypt = pwd.startsWith("$2a$") || pwd.startsWith("$2b$");
                if (!isBcrypt && !pwd.isBlank()) {
                    u.setPassword(encoder.encode(pwd));
                    repo.save(u);
                    migrated++;
                }
            }
            if (migrated > 0) {
                System.out.println("✔ Re-encriptadas " + migrated + " contraseñas en texto plano.");
            } else {
                System.out.println("ℹ No había contraseñas en texto plano que migrar.");
            }
        };
    }
}
