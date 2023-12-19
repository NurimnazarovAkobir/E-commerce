package com.ecommerce.controller;

import com.ecommerce.auth.JwtHelper;
import com.ecommerce.exception.UserException;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private UserRepository userRepository;
    private JwtHelper jwtHelper;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;
    public AuthController(UserRepository userRepository, UserServiceImpl userService, PasswordEncoder passwordEncoder, JwtHelper jwtHelper) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
    }

    @PostMapping("/signup")
    public ResponseEntity<JWTResponse> createUserHandler(@RequestBody User user) throws UserException {

        String email = user.getUsername();
        String password = user.getPassword();

        com.ecommerce.model.User isEmailExist = userRepository.findByEmail(email);
        if (isEmailExist != null) {
            throw new UserException("Email is already exist" + email);
        }
        com.ecommerce.model.User createdUser = new com.ecommerce.model.User();
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(password));

        com.ecommerce.model.User savedUser = userRepository.save(createdUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtHelper.generateToken((UserDetails) auth);

        JWTResponse jwtResponse = new JWTResponse();
        jwtResponse.setJwt(jwt);
        jwtResponse.setMessage("Signup success");
        return new ResponseEntity<JWTResponse>((JWTResponse) auth, HttpStatus.CREATED);
    }
    @PostMapping("/signin")
    public ResponseEntity<JWTResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtHelper.generateToken((UserDetails) authentication);

        JWTResponse jwtResponse = new JWTResponse(token, "Signin success");
        return null;
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = userService.loadUserByUsername(email);
        if (userDetails == null){
            throw new BadCredentialsException("Invalid username");
        }
        if (passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
