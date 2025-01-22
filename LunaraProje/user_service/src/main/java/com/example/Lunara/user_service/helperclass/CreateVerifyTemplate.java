package com.example.Lunara.user_service.helperclass;

public class CreateVerifyTemplate {
    public static String verifyEmailTemplate(String verifyUrl) {
        return """
            <html>
                <body>
                    <h2>Welcome to Lunara!</h2>
                    <p>Thank you for registering. Please click the button below to verify your email address:</p>
                    <a href="%s" style="display:inline-block;padding:10px 20px;font-size:16px;color:#fff;background-color:#28a745;text-decoration:none;border-radius:5px;">Verify Email</a>
                    <p>If you did not create this account, you can ignore this email.</p>
                </body>
            </html>
        """.formatted(verifyUrl);
    }
}
