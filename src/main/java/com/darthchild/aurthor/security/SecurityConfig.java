package com.darthchild.aurthor.security;

import com.darthchild.aurthor.security.JWT.JwtFilter;
import com.darthchild.aurthor.security.model.Role;
import com.darthchild.aurthor.security.model.User;
import com.darthchild.aurthor.security.repo.RoleRepository;
import com.darthchild.aurthor.security.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Defines core security rules and provides authentication beans
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    /**
     * Configures HTTP security, endpoint access rules, integrates JWT filter
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/users/register").permitAll()
                .requestMatchers("/users/login").permitAll()
                .requestMatchers("/users/admin").hasRole("ADMIN")
                .anyRequest()
                .authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configures the authentication provider to use the custom UserDetailsService
     * and the defined password encoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the {@link AuthenticationManager} bean from Spring Security's
     * {@link AuthenticationConfiguration}.
     * <p>
     * This allows the application to inject and use the <b>AuthenticationManager</b>
     * (e.g., in a login service) to perform authentication.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Initializes demo users in the database for testing purposes
     */
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