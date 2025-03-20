package com.example.fsneaker.controller;

import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.service.EmailService;
import com.example.fsneaker.service.KhachHangService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.UUID;

@Controller
public class PasswordController {
    @Autowired
    private KhachHangService  khachHangService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) throws MessagingException {
        KhachHang khachHang = khachHangService.findByEmail(email);
        if (khachHang == null) {
            model.addAttribute("error", "Không tìm thấy email.");
            return "quenmatkhau";
        }

        // Tạo token
        String token = UUID.randomUUID().toString();
        khachHang.setResetToken(token);
        khachHang.setTokenExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)); // 15 phút
        khachHangService.save(khachHang);

        // Gửi email
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendInvoiceEmail(email, "Đặt lại mật khẩu", "Nhấn vào liên kết sau để đặt lại mật khẩu: " + resetLink);

        model.addAttribute("message", "Đã gửi email đặt lại mật khẩu.");
        return "quenmatkhau";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        KhachHang khachHang = khachHangService.findByResetToken(token);
        if (khachHang == null || khachHang.getTokenExpiration().before(new Date())) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "reset-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, @RequestParam("password") String password, Model model) {
        KhachHang khachHang = khachHangService.findByResetToken(token);
        if (khachHang == null || khachHang.getTokenExpiration().before(new Date())) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "reset-password";
        }

        khachHang.setMatKhau(password); // Mã hóa mật khẩu
        khachHang.setResetToken(null);
        khachHang.setTokenExpiration(null);
        khachHangService.save(khachHang);

        model.addAttribute("message", "Đặt lại mật khẩu thành công!");
        return "login";
    }
}
