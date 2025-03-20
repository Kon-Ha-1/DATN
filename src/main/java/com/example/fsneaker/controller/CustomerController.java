package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomerDto;
import com.example.fsneaker.dto.KhachHangDTO;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.entity.KhuyenMai;
import com.example.fsneaker.repositories.KhachHangRepo;

import com.example.fsneaker.service.KhachHangService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomerController {



@Autowired
private KhachHangRepo khachHangRepo;

    @Autowired
    private KhachHangService khachHangService;



    // Xử lý đăng ký khách hàng
    @PostMapping("dangky")
    @Transactional
    public String register(


             @Valid @ModelAttribute KhachHangDTO khachHangdto,
            BindingResult validate,
             @RequestParam("matKhauXacNhan") String matKhauXacNhan,

            Model model) {
        System.out.println("Thông tin khách hàng: " + khachHangdto);

        System.out.println("Đã vào phương thức đăng ký!");



        if (validate.hasErrors()) {
            System.out.println("Có lỗi validate: ");
            for (FieldError error : validate.getFieldErrors()) {
                System.out.println("Lỗi: " + error.getField() + " - " + error.getDefaultMessage());
            }
            model.addAttribute("khachHang", khachHangdto);
            Map<String, String> errors = new HashMap<>();
            for (FieldError e : validate.getFieldErrors()) {
                errors.put(e.getField(), e.getDefaultMessage());
            }
            model.addAttribute("errors", errors);
            return "dangky";  // Trả về form với thông báo lỗi
        }





        // Kiểm tra sự khớp giữa mật khẩu và mật khẩu xác nhận
        if (!khachHangdto.getMatKhau().equals(matKhauXacNhan)) {
            model.addAttribute("matKhauXacNhanError", "Mật khẩu xác nhận không khớp");
            model.addAttribute("khachHang", khachHangdto); // Thêm lại thông tin khách hàng vào model
            return "dangky";  // Trả về trang đăng ký nếu mật khẩu không khớp
        }

        // Log thông tin khách hàng để kiểm tra
        System.out.println("Thông tin khách hàng: " + khachHangdto.toString());
        // Log lại lần nữa trước khi lưu vào DB
        System.out.println("Thông tin khách hàng trước khi lưu: " + khachHangdto);


//         Thiết lập các giá trị khác cho đối tượng khachHang

        // Kiểm tra và thiết lập các trường còn thiếu
        if (khachHangdto.getGioiTinh() == null) {
            khachHangdto.setGioiTinh(true);  // Mặc định là Nam
        }

        if (khachHangdto.getTrangThai() == null) {
            khachHangdto.setTrangThai(true);  // Mặc định là "Đang hoạt động"
        }

        // Kiểm tra và thiết lập mã khách hàng nếu đối tượng là mới (mã khách hàng chưa có)
        if (khachHangdto.getMaKhachHang() == null || khachHangdto.getMaKhachHang().isEmpty()) {
            khachHangdto.setMaKhachHang(khachHangService.taoMaKhachHang());  // Tạo mã khách hàng tự động
        }

        // Log thông tin khách hàng để kiểm tra
        System.out.println("Thông tin khách hàng: " + khachHangdto.toString());






// Log xem thông tin khách hàng có đầy đủ không
        System.out.println("Thông tin khách hàng: " + khachHangdto.toString());

        // Lưu đối tượng khachHang vào cơ sở dữ liệu
        // Chuyển DTO thành entity KhachHang
        KhachHang khachHang = new KhachHang();
        khachHang.setEmail(khachHangdto.getEmail());
        khachHang.setTenKhachHang(khachHangdto.getTenKhachHang());
        khachHang.setSoDienThoai(khachHangdto.getSoDienThoai());
        khachHang.setDiaChi(khachHangdto.getDiaChi());
        khachHang.setMatKhau(khachHangdto.getMatKhau());
        khachHang.setGioiTinh(khachHangdto.getGioiTinh());
        khachHang.setTrangThai(khachHangdto.getTrangThai());
        khachHang.setMaKhachHang(khachHangService.taoMaKhachHang());

        // Lưu khách hàng vào database
        try {
            khachHangRepo.save(khachHang);  // Lưu khách hàng nếu không có lỗi
            System.out.println("Thông tin khách hàng đã được lưu thành công!");
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu vào database: " + e.getMessage());
            model.addAttribute("error", "Lỗi lưu thông tin khách hàng. Vui lòng thử lại.");
            return "dangky"; // Trả về form với thông báo lỗi
        }

        return "redirect:/login";
    }

    }


