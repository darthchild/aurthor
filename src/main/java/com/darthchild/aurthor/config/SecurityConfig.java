package com.darthchild.aurthor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    DataSource dataSource;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                // permits this url w/o any Auth
                .requestMatchers("/h2-console/**").permitAll()
                // all the rest require Auth
                .anyRequest()
                .authenticated());

        // To not store any session info like auth credentials
        http.sessionManagement(session
                -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Enables Basic authentication
        http.httpBasic(Customizer.withDefaults());

        // Enables display of frames in H2 console
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        // Disables csrf tokens
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initializeUsers(UserDetailsService userDetailsService) {
        return args -> {
            JdbcUserDetailsManager udm = (JdbcUserDetailsManager) userDetailsService;

            if (!udm.userExists("user1")) {
                udm.createUser(
                        User.withUsername("user1")
                                .password(passwordEncoder().encode("lol123"))
                                .roles("USER")
                                .build()
                );
            }

            if (!udm.userExists("admin")) {
                udm.createUser(
                        User.withUsername("admin")
                                .password(passwordEncoder().encode("lol123"))
                                .roles("ADMIN")
                                .build()
                );
            }
        };
    }
}