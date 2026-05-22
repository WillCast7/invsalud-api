package com.aurealab.service;

import com.aurealab.model.aurea.entity.UserEntity;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${frontend.route}")
    private String frontendRoute;

    public void sendInvoiceEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo: " + e.getMessage());
        }
    }

    public void sendAccountRecoveryEmail(UserEntity user, String token) {
        try {
            String to = user.getEmail();
            String subject = "Recuperación de contraseña";
            String resetLink = frontendRoute + "/reset-password?token=" + token;
            
            String htmlContent = String.format(
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                "<h2 style=\"color: #333;\">Recuperación de Contraseña</h2>" +
                "<p>Hola %s,</p>" +
                "<p>Hemos recibido una solicitud para recuperar tu contraseña.</p>" +
                "<p>Haz clic en el siguiente enlace para establecer una nueva contraseña (válido por 15 minutos):</p>" +
                "<p style=\"margin: 30px 0;\">" +
                "<a href=\"%s\" style=\"background-color: #4CAF50; color: white; padding: 14px 28px; text-decoration: none; border-radius: 4px; display: inline-block;\">Restablecer Contraseña</a>" +
                "</p>" +
                "<p>O copia y pega este enlace en tu navegador:</p>" +
                "<p style=\"word-break: break-all; color: #666; font-size: 14px;\">%s</p>" +
                "<p>Si no realizaste esta solicitud, puedes ignorar este correo.</p>" +
                "<p>Saludos,<br/>Equipo de soporte</p>" +
                "</body>" +
                "</html>",
                user.getPerson() != null ? user.getPerson().getNames() : user.getUserName(),
                resetLink,
                resetLink
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo de recuperación: " + e.getMessage());
        }
    }
}
