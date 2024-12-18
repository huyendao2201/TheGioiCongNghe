package com.example.thegioicongnghe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // Kích hoạt @PreAuthorize và @PostAuthorize
public class SecurityConfig {

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Autowired
	@Lazy
	private AuthFailureHandlerImpl authenticationFailureHandler;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.disable())
				.authorizeHttpRequests(req -> req
						.anyRequest().permitAll() // Cho phép tất cả request không cần kiểm tra
				)
				.formLogin(form -> form
						.loginPage("/signin") // Đường dẫn tới trang đăng nhập (nếu cần)
						.loginProcessingUrl("/login") // Xử lý logic đăng nhập
						.failureHandler(authenticationFailureHandler)
						.successHandler(authenticationSuccessHandler)
				)
				.logout(logout -> logout.permitAll()); // Cho phép logout không hạn chế
		return http.build();
	}

	/*
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http.csrf(csrf->csrf.disable()).cors(cors->cors.disable())
				.authorizeHttpRequests(req->req
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/**").permitAll())
				.formLogin(form->form.loginPage("/signin")
						.loginProcessingUrl("/login")
//						.defaultSuccessUrl("/")
						.failureHandler(authenticationFailureHandler)
						.successHandler(authenticationSuccessHandler))
				.logout(logout->logout.permitAll());
		
		return http.build();
	}
	 */

}
