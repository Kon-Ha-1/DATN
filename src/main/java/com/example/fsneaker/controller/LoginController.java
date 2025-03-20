package com.example.fsneaker.controller;

import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.service.GioHangService;
import com.example.fsneaker.service.KhachHangService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private GioHangService gioHangService;
    @GetMapping("/login")
    public String trangChu(){
        return "login";
    }
    @GetMapping("/redirectByRole")
    public String redirectByRole(Authentication authentication){
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if("ROLE_ADMIN".equals(role)){
            return "redirect:/qlnhanvien";
        }else if("ROLE_STAFF".equals(role)){
            return "redirect:/don-hang/hien-thi";
        }else if("ROLE_MANAGER".equals(role)){
            return "redirect:/trangchuadmin";
        }else if("ROLE_CUSTOMER".equals(role)){
            return "redirect:/trang-chu";
        }
        return "redirect:/login?error";
    }@PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request){
        //Kiểm tra đăng nhập
        KhachHang khachHang = khachHangService.authenticateUser(username,password);
        if(khachHang != null){
            //Lưu userId vào session
            request.getSession().setAttribute("userId",khachHang.getId());
            return "redirect:/trang-chu";
        }else{
            return "login";
        }
    }

    @GetMapping("/dang-ky")
    public String dangKy(Model model){
        KhachHang khachHang = new KhachHang();
        model.addAttribute("khachHang", khachHang);

        return "dangky";
    }

    @GetMapping("/quen-mat-khau")
    public String quenMatKhau(){
        return "quenmatkhau";
    }
}
