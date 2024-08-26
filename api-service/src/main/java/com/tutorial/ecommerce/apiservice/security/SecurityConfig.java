package com.tutorial.ecommerce.apiservice.security;

import com.tutorial.ecommerce.model.Role;
import com.tutorial.ecommerce.security.JwtAuthenticationFilter;
import com.tutorial.ecommerce.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final HandlerExceptionResolver handlerExceptionResolver;


    public SecurityConfig(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider, HandlerExceptionResolver handlerExceptionResolver) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> {
                    try {
                        csrf.disable()
                                .authorizeHttpRequests(authorize -> authorize
                                        .requestMatchers("/users/auth/login").permitAll()
                                        .requestMatchers("/users").authenticated()
                                        .requestMatchers("/products/**").hasAnyAuthority(Role.ADMIN.getRoleName(), Role.AGENT.getRoleName(), Role.USER.getRoleName())
                                        .requestMatchers("/orders/**").hasAnyAuthority(Role.ADMIN.getRoleName(), Role.AGENT.getRoleName(), Role.USER.getRoleName())
                                        .requestMatchers("/inventory/**").hasAuthority(Role.ADMIN.getRoleName())
                                        .anyRequest().authenticated()
                                )
                                .addFilterBefore(jwtAuthenticationFilter(http), BasicAuthenticationFilter.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        return http.build();

    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**");
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(HttpSecurity http) throws Exception {
        return new JwtAuthenticationFilter(authenticationManager(http), jwtTokenProvider, userDetailsService, handlerExceptionResolver);
    }
}