package com.voncken.lostandfound.Services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigService {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/lostItems", "/claimItem").hasRole("USER")
				.requestMatchers("/claimedItems").hasRole("ADMIN")
				.requestMatchers("/", "/upload", "/home", "/user", "/files/**").authenticated()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
			)
			.logout((logout) -> logout.permitAll());
			
			// so since I am running a bit low on time, usually I would've configured this
			// to be a bit more secure (allow certain callers), but for now I will just disable it for these endpoints
			// so I can focus on the main functionality
			http.csrf((csrf) -> csrf.ignoringRequestMatchers("/upload", "/files/**", "/claimedItems", "/lostItems", "claimItem" ));

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user =
			 User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("USER")
				.build();
				UserDetails user2 =
				User.withDefaultPasswordEncoder()
				.username("userTwo")
				.password("passwordTwo")
				.roles("USER")
				.build();
				UserDetails user3 =
				User.withDefaultPasswordEncoder()
				.username("userThree")
				.password("passwordThree")
				.roles("USER")
				.build();

				UserDetails admin =
			User.withDefaultPasswordEncoder()
				.username("admin")
				.password("admin")
				.roles("ADMIN")
				.build();

		return new InMemoryUserDetailsManager(user, user2, user3, admin);
	}
}