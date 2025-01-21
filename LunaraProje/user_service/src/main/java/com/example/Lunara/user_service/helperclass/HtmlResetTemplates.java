package com.example.Lunara.user_service.helperclass;

public class HtmlResetTemplates {
    public static String resetPasswordForm(String token) {
        return """
            <html>
                <body>
                    <h3>Reset Your Password</h3>
                    <form action="/api/users/reset-password" method="POST">
                        <input type="hidden" name="token" value="%s" />
                        <label for="newPassword">New Password:</label><br>
                        <input type="password" id="newPassword" name="newPassword" required><br><br>
                        <label for="confirmPassword">Confirm Password:</label><br>
                        <input type="password" id="confirmPassword" name="confirmPassword" required><br><br>
                        <button type="submit">Reset Password</button>
                    </form>
                </body>
            </html>
        """.formatted(token);
    }
}
