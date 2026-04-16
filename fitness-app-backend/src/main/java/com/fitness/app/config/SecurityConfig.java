package com.fitness.app.config;

import com.fitness.app.filter.JwtAuthenticationFilter;
import com.fitness.app.filter.JwtAuthorizationFilter;
import com.fitness.app.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private JwtUtils jwtUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/wechat", "/auth/admin", "/auth/logout").permitAll()
                        .requestMatchers("/templates/list", "/categories/list", "/exercises/list", "/movements/**").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}