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
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/api/books/admin").hasRole("ADMIN")
                .requestMatchers("/users/register").permitAll()
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

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        return provider;
    }

    @Bean
    @Transactional
    public CommandLineRunner initUsers(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {

            // Create users
            User user1 = User.builder()
                    .username("user1")
                    .password("lol123")
                    .enabled(true)
                    .build();

            User admin = User.builder()
                    .username("admin")
                    .password("lol123")
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