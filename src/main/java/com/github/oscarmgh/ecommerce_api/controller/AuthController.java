package com.github.oscarmgh.ecommerce_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.github.oscarmgh.ecommerce_api.entity.User;
import com.github.oscarmgh.ecommerce_api.payload.LoginRequest;
import com.github.oscarmgh.ecommerce_api.payload.LoginResponse;
import com.github.oscarmgh.ecommerce_api.repository.UserRepository;
import com.github.oscarmgh.ecommerce_api.security.JwtUtil;
import com.github.oscarmgh.ecommerce_api.service.UserService;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtUtil jwtUtil;
	private final UserService userService;

	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
			JwtUtil jwtUtil, UserService userService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user) {
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			return new ResponseEntity<>("Username already exists.", HttpStatus.BAD_REQUEST);
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			return new ResponseEntity<>("Email already exists.", HttpStatus.BAD_REQUEST);
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRoles(Collections.singleton("ROLE_USER"));
		userRepository.save(user);

		return new ResponseEntity<>("User registered successfully.", HttpStatus.CREATED);
	}

	@PostMapping("/register/admin")
	public ResponseEntity<String> registerAdminUser(@RequestBody User user) {
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			return new ResponseEntity<>("Username already exists.", HttpStatus.BAD_REQUEST);
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			return new ResponseEntity<>("Email already exists.", HttpStatus.BAD_REQUEST);
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRoles(Collections.singleton("ROLE_ADMIN"));
		userRepository.save(user);

		return new ResponseEntity<>("Admin registered successfully.", HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
		String token = jwtUtil.generateToken(userDetails);

		return ResponseEntity.ok(new LoginResponse(token));
	}

	@PostMapping("/promote/{username}")
	public ResponseEntity<String> promoteToAdmin(@PathVariable String username) {
		boolean success = userService.promoteToAdmin(username);
		if (success) {
			return ResponseEntity.ok("User " + username + " promoted to admin.");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/demote/{username}")
	public ResponseEntity<String> demoteToUser(@PathVariable String username) {
		boolean success = userService.demoteToUser(username);
		if (success) {
			return ResponseEntity.ok("User " + username + " demoted to regular user.");
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}