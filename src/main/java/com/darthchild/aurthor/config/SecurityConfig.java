package com.darthchild.aurthor.config;

import com.darthchild.aurthor.JWT.JwtFilter;
import com.darthchild.aurthor.model.Role;
import com.darthchild.aurthor.model.User;
import com.darthchild.aurthor.repo.RoleRepository;
import com.darthchild.aurthor.repo.UserRepository;
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
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;
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
     * and the defined password encoder. <br>
     * <br>
     * AuthenticationProvider is responsible for verifying user credentials during login attempts
     * uses the custom UserDetailsService to load user information from the database and the
     *  PasswordEncoder to check passwords.
     *
     * @return an AuthenticationProvider instance
     */

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }


    /**
     * Initializes demo users for testing purposes
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