package com.example.Lunara.user_service.controller;

import com.example.Lunara.user_service.DTO.LoginRequest;
import com.example.Lunara.user_service.helperclass.HtmlResetTemplates;
import com.example.Lunara.user_service.helperclass.CreateVerifyTemplate;
import com.example.Lunara.user_service.service.MailService;
import com.example.Lunara.user_service.security.JwtService;
import com.example.Lunara.user_service.service.ResetPasswordTokenService;
import com.example.Lunara.user_service.user.User;
import com.example.Lunara.user_service.respository.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final MailService mailService;
    private final ResetPasswordTokenService resetPasswordTokenService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService, MailService mailService, ResetPasswordTokenService resetPasswordTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.mailService = mailService;
        this.resetPasswordTokenService = resetPasswordTokenService;
    }
    // Password Kontrolü
    private boolean isPasswordValid(String password) {
        // Şifre kontrolü için regex deseni
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordRegex);
    }

    // Kullanıcı oluşturma
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        // Şifre kontrolü
        if (!isPasswordValid(user.getPassword())) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters, contain one uppercase letter, one number, and one special character.");
        }

        // Email kontrolü
        if (userService.getUserByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
        }

        // Kullanıcıyı pasif olarak kaydet
        user.setActive(false);
        userService.createUser(user);

        // Verify link oluştur
        String verifyUrl = "http://localhost:8081/api/users/verify-email?token=" + jwtService.generateToken(user.getUsername());

        // HTML email gönder
        String htmlContent = CreateVerifyTemplate.verifyEmailTemplate(verifyUrl);
        mailService.sendHtmlMail(user.getEmail(), "Verify Your Email", htmlContent);

        return ResponseEntity.ok("User created successfully. Please verify your email.");
    }


    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            // Token'dan username bilgisini çıkar
            String username = jwtService.validateToken(token);

            // Kullanıcıyı username'e göre bul
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
            // Kullanıcı zaten aktifse yeniden doğrulama göndermeye gerek yok
            if (user.isActive()) {
                return ResponseEntity.badRequest().body("User is already verified.");
            }
            // Kullanıcıyı aktif hale getirme
            user.setActive(true);
            userService.updateUser(user.getId(), user);
            return ResponseEntity.ok("Email verified successfully. You can now log in.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        // Token oluştur ve e-posta gönder
        String resetToken = resetPasswordTokenService.generateToken(email);
        String resetUrl = "http://localhost:8081/api/users/reset-password?resetToken=" + resetToken;

        mailService.sendPasswordResetEmail(email,user.getUsername(), resetUrl);
        return ResponseEntity.ok("Password reset email sent successfully.");
    }
    // Şifre sıfırlama formu
    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPasswordForm(@RequestParam String resetToken) {
        if (!resetPasswordTokenService.validateToken(resetToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("<h3>Invalid or Expired Token</h3>");
        }

        // HTML formu döndür
        String htmlForm = HtmlResetTemplates.resetPasswordForm(resetToken);
        return ResponseEntity.ok().header("Content-Type", "text/html").body(htmlForm);
    }
    // Şifre sıfırlama işlemi
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String resetToken,
                                                @RequestParam String newPassword,
                                                @RequestParam String confirmPassword) {
        if (!resetPasswordTokenService.validateToken(resetToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("<h3>Invalid or Expired Token</h3>");
        }
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("<h3>Passwords do not match. Please try again.</h3>");
        }
        if (!isPasswordValid(newPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("<h3>Password does not meet the complexity requirements.</h3>");
        }

        // Kullanıcıyı bul ve şifreyi güncelle
        String email = resetPasswordTokenService.getEmailByToken(resetToken);
        User user = userService.getUserByEmail(email);
        user.setPassword(newPassword); // Not: Şifreyi hash'lemeyi unutmayın!
        userService.updateUser(user.getId(), user);

        // Token'ı geçersiz kıl
        resetPasswordTokenService.invalidateToken(resetToken);

        return ResponseEntity.ok("<h3>Password successfully reset!</h3>");
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }

    // Tüm kullanıcıları listeleme (Admin rolleri için koruma eklenebilir)
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Kullanıcıyı username'e göre bulma
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    // Kullanıcıyı ID'ye göre bulma
    @GetMapping("/id/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Kullanıcı giriş (Login) endpoint
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        User user = userService.findByUsernameAndPassword(request.getUsername(), request.getPassword());
        if (user != null) {
            return jwtService.generateToken(user.getUsername());
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

    // Korunan bir endpoint örneği
    @GetMapping("/protected")
    public String protectedEndpoint(@RequestHeader("Authorization") String token) {
        if (!token.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token format. Expected 'Bearer <token>'");
        }
        String username = jwtService.validateToken(token.substring(7));
        return "Welcome, " + username;
    }
}