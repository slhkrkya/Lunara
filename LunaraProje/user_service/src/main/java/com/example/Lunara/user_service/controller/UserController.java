package com.example.Lunara.user_service.controller;

import com.example.Lunara.user_service.DTO.LoginRequest;
import com.example.Lunara.user_service.security.JwtService;
import com.example.Lunara.user_service.user.User;
import com.example.Lunara.user_service.respository.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Kullanıcı oluşturma
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
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
