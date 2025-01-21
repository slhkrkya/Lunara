package com.example.Lunara.user_service.controller;

import com.example.Lunara.user_service.DTO.LoginRequest;
import com.example.Lunara.user_service.helperclass.HtmlResetTemplates;
import com.example.Lunara.user_service.service.MailService;
import com.example.Lunara.user_service.security.JwtService;
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

    @Autowired
    public UserController(UserService userService, JwtService jwtService, MailService mailService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.mailService = mailService;
    }

    // Kullanıcı oluşturma
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Şifre sıfırlama tokeni oluştur
        String resetToken = jwtService.generateToken(user.getUsername());
        String resetUrl = "http://localhost:8081/api/users/reset-password?token=" + resetToken;

        // E-posta gönder
        mailService.sendPasswordResetEmail(email, user.getUsername(), resetUrl);

        return ResponseEntity.ok("Password reset email sent to: " + email);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPasswordForm(@RequestParam String token) {
        try {
            // Token doğrulama
            String username = jwtService.validateToken(token);

            // Şablon oluştur
            String htmlForm = HtmlResetTemplates.resetPasswordForm(token);
            return ResponseEntity.ok().header("Content-Type", "text/html").body(htmlForm);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("<h3>Invalid or Expired Token</h3>");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("<h3>Passwords do not match. Please try again.</h3>");
        }

        try {
            // Token doğrulama
            String username = jwtService.validateToken(token);

            // Kullanıcıyı bul ve şifreyi güncelle
            User user = userService.getUserByUsername(username);
            user.setPassword(newPassword); // Not: Şifreyi hash'lemeyi unutmayın!
            userService.updateUser(user.getId(), user);

            return ResponseEntity.ok("<h3>Password successfully reset!</h3>");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("<h3>Invalid or Expired Token</h3>");
        }
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