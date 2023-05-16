package io.github.joshy56.portfoliobackend.security.controller;

import io.github.joshy56.portfoliobackend.dto.Message;
import io.github.joshy56.portfoliobackend.security.dto.JwtDto;
import io.github.joshy56.portfoliobackend.security.dto.UserDto;
import io.github.joshy56.portfoliobackend.security.entity.SimpleUser;
import io.github.joshy56.portfoliobackend.security.jwt.JwtProvider;
import io.github.joshy56.portfoliobackend.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author joshy56
 * @since 15/5/2023
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtProvider provider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto dto, BindingResult result) {
        if (result.hasErrors())
            return new ResponseEntity<>(new Message("Malformed fields"), HttpStatus.BAD_REQUEST);
        if (userRepository.existsByUsername(dto.getUsername()))
            return new ResponseEntity<>(new Message("User with its name already exists"), HttpStatus.CONFLICT);
        SimpleUser user = new SimpleUser(dto.getUsername(), passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>(new Message("Successfully registered!"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@RequestBody UserDto dto, BindingResult result) {
        if (result.hasErrors())
            return new ResponseEntity(new Message("Malformed fields"), HttpStatus.BAD_REQUEST);
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = provider.generateToken(auth);
        JwtDto jwtDto = new JwtDto(token);
        return new ResponseEntity<>(jwtDto, HttpStatus.OK);
    }
}
