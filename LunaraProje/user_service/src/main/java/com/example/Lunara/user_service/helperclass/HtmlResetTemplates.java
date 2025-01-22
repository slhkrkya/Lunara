package com.example.Lunara.user_service.helperclass;

public class HtmlResetTemplates {
    public static String resetPasswordForm(String resetToken) {
        return """
            <html>
                <head>
                    <title>Reset Your Password</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f9;
                            margin: 0;
                            padding: 0;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                        }
                        .reset-container {
                            background: #ffffff;
                            padding: 20px;
                            border-radius: 8px;
                            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                            width: 100%%; /* Buradaki %% önemli */
                            max-width: 400px;
                            text-align: center;
                        }
                        h3 {
                            color: #333;
                            margin-bottom: 20px;
                        }
                        label {
                            display: block;
                            text-align: left;
                            margin-bottom: 5px;
                            font-weight: bold;
                            color: #555;
                        }
                        input[type="password"] {
                            width: 100%%; /* Buradaki %% önemli */
                            padding: 10px;
                            margin-bottom: 15px;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                            font-size: 14px;
                        }
                        button {
                            background-color: #007BFF;
                            color: #fff;
                            padding: 10px 20px;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                            font-size: 16px;
                        }
                        button:hover {
                            background-color: #0056b3;
                        }
                        .footer {
                            margin-top: 20px;
                            font-size: 12px;
                            color: #777;
                        }
                    </style>
                </head>
                <body>
                    <div class="reset-container">
                        <h3>Reset Your Password</h3>
                        <form action="/api/users/reset-password" method="POST">
                            <input type="hidden" name="resetToken" value="%s" />
                            <label for="newPassword">New Password:</label>
                            <input type="password" id="newPassword" name="newPassword" required />
                            <label for="confirmPassword">Confirm Password:</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" required />
                            <button type="submit">Reset Password</button>
                        </form>
                        <div class="footer">
                            If you didn't request this, please ignore this email.
                        </div>
                    </div>
                </body>
            </html>
        """.formatted(resetToken);
    }
}
