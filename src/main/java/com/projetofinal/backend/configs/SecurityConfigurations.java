package com.projetofinal.backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/employees/**", "/products/**", "/sales/**").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers(HttpMethod.POST, "/sales/**").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers(HttpMethod.POST).hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.PUT).hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE).hasAuthority("ADMIN")
						.anyRequest().authenticated())
				.headers(headers -> headers.frameOptions(frame -> frame.disable()))
				.httpBasic(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
