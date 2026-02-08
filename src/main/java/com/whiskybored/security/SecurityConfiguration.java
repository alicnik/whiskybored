package com.whiskybored.security;

import com.whiskybored.filters.CustomAuthenticationFilter;
import com.whiskybored.filters.CustomAuthorisationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${jwt.secret}")
    private String secret;

    // If you already have this as a @Bean elsewhere, remove this method.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Replaces configure(AuthenticationManagerBuilder ...).
     * Boot will build an AuthenticationManager automatically from your
     * UserDetailsService + PasswordEncoder beans.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Replaces configure(WebSecurity web).
     * NOTE: ignoring means these requests bypass the security filter chain entirely.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
//                new RegexRequestMatcher("/", "GET"),
                new RegexRequestMatcher("/images.*", "GET")
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager
    ) throws Exception {

        // Your custom login filter (POST /login) — same intent as before
        CustomAuthenticationFilter authenticationFilter =
                new CustomAuthenticationFilter(authenticationManager, secret);

        authenticationFilter.setRequiresAuthenticationRequestMatcher(
                new RegexRequestMatcher("^/login$", "POST")
        );

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/whiskies").permitAll()

                        // Your old ant matcher "/whiskies/{[0-9a-f]{32}}" isn’t an Ant pattern.
                        // If you truly want that regex, use a RegexRequestMatcher:
                        .requestMatchers(new RegexRequestMatcher("^/whiskies/[0-9a-f]{32}$", "GET")).permitAll()

                        .anyRequest().authenticated()
                )

                // Put your custom login filter at the UsernamePasswordAuthenticationFilter slot
                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // JWT/authorisation filter should run before UsernamePasswordAuthenticationFilter
                .addFilterBefore(new CustomAuthorisationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // If you ever set allowCredentials(true), you cannot use "*" for allowed origins.
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("authorization", "content-type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
