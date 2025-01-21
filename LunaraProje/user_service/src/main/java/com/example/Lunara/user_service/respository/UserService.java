package com.example.Lunara.user_service.respository;

import com.example.Lunara.user_service.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRespository userRespository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRespository userRespository) {
        this.userRespository = userRespository;
    }

    // Kullanıcıyı kaydetme
    public User createUser(User user) {
        return userRespository.save(user);
    }

    // Tüm kullanıcıları listeleme
    public List<User> getAllUsers() {
        return userRespository.findAll();
    }

    // Kullanıcıyı username'e göre bulma
    public User getUserByUsername(String username) {
        return userRespository.findByUsername(username);
    }

    // Kullanıcıyı email'e göre bulma
    public User getUserByEmail(String email) {
        return userRespository.findByEmail(email);
    }

    // Kullanıcıyı ID'ye göre bulma
    public Optional<User> getUserById(Long id) {
        return userRespository.findById(id);
    }

    // Kullanıcıyı güncelleme
    public User updateUser(Long id, User user) {
        if (userRespository.existsById(id)) {
            user.setId(id);
            return userRespository.save(user);
        } else {
            throw new RuntimeException("Kullanıcı bulunamadı: ID = " + id);
        }
    }

    // Kullanıcı şifre değişikliği
    public User updatePassword(Long userId, String newPassword) {
        User user = userRespository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: ID = " + userId));
        user.setPassword(passwordEncoder.encode(newPassword)); // Şifre hash'leniyor
        return userRespository.save(user);
    }

    // Kullanıcıyı silme
    public void deleteUser(Long id) {
        userRespository.deleteById(id);
    }

    // Kullanıcıyı username ve password ile bulma (login için)
    public User findByUsernameAndPassword(String username, String password) {
        User user = userRespository.findByUsernameAndPassword(username, password);
        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }
        return user;
    }
}
