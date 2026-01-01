package com.example.employee_management_system.config;

import com.example.employee_management_system.Utils.AuthEntryPointJwt;
import com.example.employee_management_system.Utils.AuthTokenFilter;
import com.example.employee_management_system.Services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .authorizeHttpRequests(auth -> auth
                        // ===== PUBLIC ENDPOINTS =====
                        .requestMatchers("/", "/login", "/register", "/logout").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()  // This allows /api/auth/signup
                        .requestMatchers("/h2-console/**").permitAll()

                        // ===== ADMIN ONLY ROUTES =====
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/departments/**").hasAnyRole("ADMIN", "HR", "MANAGER")
                        .requestMatchers("/employees/**").hasAnyRole("ADMIN", "HR", "MANAGER")
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // ===== HR ROUTES =====
                        .requestMatchers("/hr/**").hasAnyRole("HR", "ADMIN")
                        .requestMatchers("/leave-requests/approve/**").hasAnyRole("HR", "MANAGER", "ADMIN")
                        .requestMatchers("/leave-requests/all").hasAnyRole("HR", "MANAGER", "ADMIN")

                        // ===== EMPLOYEE ROUTES =====
                        .requestMatchers("/employee/**").hasAnyRole("EMPLOYEE", "MANAGER", "HR", "ADMIN")
                        .requestMatchers("/my-leaves/**").hasAnyRole("EMPLOYEE", "MANAGER", "HR", "ADMIN")
                        .requestMatchers("/my-profile/**").hasAnyRole("EMPLOYEE", "MANAGER", "HR", "ADMIN")

                        // ===== MANAGER ROUTES =====
                        .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")

                        // ===== DASHBOARD ACCESS =====
                        .requestMatchers("/dashboard").authenticated()

                        // ===== API ENDPOINTS =====
                        .requestMatchers("/api/employees/**").hasAnyRole("ADMIN", "HR", "MANAGER")
                        .requestMatchers("/api/departments/**").hasAnyRole("ADMIN", "HR", "MANAGER")
                        .requestMatchers("/api/leaves/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}