package com.darthchild.aurthor.config;

import com.darthchild.aurthor.model.Role;
import com.darthchild.aurthor.model.User;
import com.darthchild.aurthor.repo.RoleRepository;
import com.darthchild.aurthor.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/users/register").permitAll()
                .requestMatchers("/users/admin").hasRole("ADMIN")
                .anyRequest()
                .authenticated());

        return http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    @Transactional
    public CommandLineRunner initUsers(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {

            // Create users
            User user1 = User.builder()
                    .username("user1")
                    .password(passwordEncoder().encode("lol123"))
                    .enabled(true)
                    .build();

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder().encode("lol123"))
                    .enabled(true)
                    .build();

            userRepository.saveAll(List.of(user1, admin));

            // Create Roles
            roleRepository.saveAll(List.of(
                    Role.builder().role("ROLE_USER").user(user1).build(),
                    Role.builder().role("ROLE_ADMIN").user(admin).build()
            ));
        };
    }
}