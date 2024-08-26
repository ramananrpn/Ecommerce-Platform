package com.tutorial.ecommerce.security;

import com.tutorial.ecommerce.exception.InvalidCredentialsException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String BASIC_PREFIX = "Basic ";

    private JwtTokenProvider jwtTokenProvider;

    private UserDetailsService userDetailsService;
    private HandlerExceptionResolver resolver;



    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   JwtTokenProvider jwtTokenProvider,
                                   UserDetailsService userDetailsService,
                                   HandlerExceptionResolver resolver) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            final String authorizationHeader = request.getHeader(AUTHORIZATION);

            if ("/users/auth/login".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
                chain.doFilter(request, response);
                return;
            }
            else if ("/users".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
                handleClientCredentialsAuthentication(authorizationHeader, response);
            } else {
                handleJwtAuthentication(authorizationHeader, request, response);
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            resolver.resolveException(request, response, null, e);
        }
    }

    private void handleClientCredentialsAuthentication(String header, HttpServletResponse response) throws IOException {
        if (header != null && header.startsWith(BASIC_PREFIX)) {
            String[] credentials = extractCredentials(header);
            if (jwtTokenProvider.validateCredentials(credentials)) {
                Authentication authentication = jwtTokenProvider.getClientAuthentication();
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new InvalidCredentialsException("Invalid client credentials");
            }
        } else {
            throw new InvalidCredentialsException("Missing or invalid Authorization header");
        }
    }

    private void handleJwtAuthentication(String authorizationHeader, HttpServletRequest request, HttpServletResponse response) {
        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            jwt = authorizationHeader.substring(7);
            email = jwtTokenProvider.extractEmailFromToken(jwt);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            if (jwtTokenProvider.validateToken(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
    }

    private String[] extractCredentials(String header) {
        String credentials = header.substring(BASIC_PREFIX.length()).trim();
        return credentials.split(":", 2);
    }
}