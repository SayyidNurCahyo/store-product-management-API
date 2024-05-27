package com.example.store_product.service.impl;

import com.example.store_product.dto.request.AuthRequest;
import com.example.store_product.dto.request.RegisterRequest;
import com.example.store_product.dto.response.LoginResponse;
import com.example.store_product.dto.response.RegisterResponse;
import com.example.store_product.entity.UserAccount;
import com.example.store_product.repository.UserAccountRepository;
import com.example.store_product.service.AuthService;
import com.example.store_product.service.JwtService;
import com.example.store_product.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ValidationUtil validationUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${store.email.admin}")
    private String adminEmail;
    @Value("${store.password.admin}")
    private String adminPassword;
    @Value("${store.phone.admin}")
    private String adminPhone;
    @Value("${store.name.admin}")
    private String adminName;

    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initSuperAdmin() {
        UserAccount currentUser = userAccountRepository.findByEmail(adminEmail);
        if (currentUser != null) return;
        UserAccount account = UserAccount.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .phone(adminPhone)
                .name(adminName)
                .build();
        userAccountRepository.save(account);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse register(RegisterRequest request) throws DataIntegrityViolationException {
        validationUtil.validate(request);
        String hashPassword = passwordEncoder.encode(request.getPassword());
        UserAccount account = UserAccount.builder()
                .email(request.getEmail())
                .password(hashPassword)
                .phone(request.getPhone())
                .name(request.getName())
                .build();
        userAccountRepository.saveAndFlush(account);
        return RegisterResponse.builder()
                .email(account.getEmail())
                .phone(account.getPhone()).build();
    }

    @Transactional(readOnly = true)
    @Override
    public LoginResponse login(AuthRequest request) {
        UserAccount user = userAccountRepository.findByEmail(request.getIdentifier());
        if(user == null) user = userAccountRepository.findByPhone(request.getIdentifier());
        if(user == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Email or Phone Number");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                request.getPassword()
        );
        Authentication authenticate = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        UserAccount userAccount = (UserAccount) authenticate.getPrincipal();
        String token = jwtService.generateToken(userAccount);
        return LoginResponse.builder()
                .email(userAccount.getEmail())
                .token(token)
                .build();
    }

    public boolean validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userAccount = (UserAccount) this.userAccountRepository.findByEmail(authentication.getPrincipal().toString());
        return userAccount != null;
    }
}
