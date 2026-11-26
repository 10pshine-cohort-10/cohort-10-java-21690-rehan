package com.rehan.contacts.service;

import com.rehan.contacts.dto.AuthResponse;
import com.rehan.contacts.dto.LoginRequest;
import com.rehan.contacts.dto.RegisterRequest;
import com.rehan.contacts.dto.ResendRequest;
import com.rehan.contacts.dto.VerifyRequest;
import com.rehan.contacts.entity.User;
import com.rehan.contacts.entity.VerificationCode;
import com.rehan.contacts.exception.ResendWaitException;
import com.rehan.contacts.repository.UserRepository;
import com.rehan.contacts.repository.VerificationCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final SecureRandom random = new SecureRandom();

    private static final int CODE_MINUTES = 5;
    private static final int RESEND_WAIT_SECONDS = 60;
    private static final int MAX_CODE_ATTEMPTS = 5;
    private static final int MAX_PASSWORD_BYTES = 72;

    private final UserRepository userRepository;
    private final VerificationCodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       VerificationCodeRepository codeRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(RegisterRequest req) {
        String email = blankToNull(req.email());
        String phone = blankToNull(req.phone());

        if (email == null && phone == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter an email or a phone number");
        }
        if (req.password().getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too long, use a shorter one");
        }
        if (identifierTaken(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        if (email != null && identifierTaken(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        if (phone != null && identifierTaken(phone)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already registered");
        }

        User user = new User();
        user.setUsername(req.username());
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        userRepository.save(user);

        createCode(user);
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void verify(VerifyRequest req) {
        User user = findByIdentifier(req.identifier())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        VerificationCode saved = codeRepository.findTopByUserOrderByIdDesc(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No code found. Ask for a new code"));

        if (saved.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code expired. Ask for a new code");
        }
        if (saved.getAttempts() >= MAX_CODE_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many wrong tries. Ask for a new code");
        }
        if (!passwordEncoder.matches(req.code(), saved.getCode())) {
            saved.setAttempts(saved.getAttempts() + 1);
            codeRepository.save(saved);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong code");
        }

        user.setVerified(true);
        userRepository.save(user);
        codeRepository.deleteByUser(user);
    }

    @Transactional
    public void resendCode(ResendRequest req) {
        User user = findByIdentifier(req.identifier())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is already verified");
        }

        Optional<VerificationCode> last = codeRepository.findTopByUserOrderByIdDesc(user);
        if (last.isPresent()) {
            LocalDateTime sentAt = last.get().getExpiresAt().minusMinutes(CODE_MINUTES);
            LocalDateTime allowedAt = sentAt.plusSeconds(RESEND_WAIT_SECONDS);
            LocalDateTime now = LocalDateTime.now();
            if (allowedAt.isAfter(now)) {
                long millis = Duration.between(now, allowedAt).toMillis();
                throw new ResendWaitException((int) Math.ceil(millis / 1000.0));
            }
        }

        codeRepository.deleteByUser(user);
        createCode(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = findByIdentifier(req.identifier())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        if (!user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Verify your account first");
        }

        return new AuthResponse(jwtService.generateToken(user.getUsername()));
    }

    private void createCode(User user) {
        String code = String.format("%05d", random.nextInt(100000));

        VerificationCode vc = new VerificationCode();
        vc.setCode(passwordEncoder.encode(code));
        vc.setUser(user);
        vc.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_MINUTES));
        codeRepository.save(vc);

        log.info("Verification code for {}: {}", user.getUsername(), code);
    }

    private boolean identifierTaken(String value) {
        return userRepository.existsByUsername(value)
                || userRepository.existsByEmail(value)
                || userRepository.existsByPhone(value);
    }

    private Optional<User> findByIdentifier(String identifier) {
        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .or(() -> userRepository.findByPhone(identifier));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
