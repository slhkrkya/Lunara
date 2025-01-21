package com.example.Lunara.user_service.helperclass;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    /**
     * Şifre sıfırlama e-postası için HTML şablonu oluşturur.
     *
     * @param username Kullanıcının adı
     * @param resetUrl Şifre sıfırlama bağlantısı
     * @return HTML içeriği
     */
    public String generateResetEmail(String username, String resetUrl) {
        return """
            <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 5px; background-color: #f9f9f9;">
                        <h2 style="text-align: center; color: #4CAF50;">Password Reset Request</h2>
                        <p>Hi <b>%s</b>,</p>
                        <p>We received a request to reset your password. Click the button below to reset your password:</p>
                        <div style="text-align: center; margin: 20px 0;">
                            <a href="%s" style="text-decoration: none; color: white; background-color: #4CAF50; padding: 10px 20px; border-radius: 5px; display: inline-block;">Reset Password</a>
                        </div>
                        <p>If you did not request this, please ignore this email.</p>
                        <p style="color: #666;">Best regards,<br>The Lunara Team</p>
                    </div>
                </body>
            </html>
        """.formatted(username, resetUrl);
    }
}