package com.suleyman.jobportal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suleyman.jobportal.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    private static final String[] PUBLIC_API_URLS = {
            "/",
            "/api/v1/auth/register",
            "/api/v1/auth/user-types",
            "/api/v1/jobs/public-search/**",
            "/login",
            "/photos/**"

    };


    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests(auth ->{
            auth.requestMatchers(PUBLIC_API_URLS).permitAll();
            auth.anyRequest().authenticated();
        });

        httpSecurity.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "Full authentication is required to access this resource");
            response.getWriter().write(objectMapper.writeValueAsString(error));
        }));

        httpSecurity.formLogin(form -> {
            form.loginProcessingUrl("/login");
            form.successHandler(customAuthenticationSuccessHandler);

            form.failureHandler((request, response, exception) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Authentication Failed");
                error.put("message", exception.getMessage());
                response.getWriter().write(objectMapper.writeValueAsString(error));
            });
        });

        httpSecurity.logout(logout->{
            logout.logoutUrl("/logout");
            logout.logoutSuccessHandler((request, response, authentication) -> {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                Map<String, String> message = new HashMap<>();
                message.put("message", "Logout successful");
                response.getWriter().write(objectMapper.writeValueAsString(message));
            });
        });
        httpSecurity.cors(Customizer.withDefaults())
                .csrf(csrf->csrf.disable());


        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();

    }
}