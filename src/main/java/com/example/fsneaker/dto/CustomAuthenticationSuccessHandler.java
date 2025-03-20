package com.example.fsneaker.dto;

import com.example.fsneaker.entity.GioHang;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.repositories.GioHangRepo;
import com.example.fsneaker.service.GioHangChiTietService;
import com.example.fsneaker.service.GioHangService;
import com.example.fsneaker.service.KhachHangService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private GioHangRepo gioHangRepo;
    @Autowired
    private GioHangChiTietService gioHangChiTietService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Lấy thông tin khách hàng từ Authentication
        String username = authentication.getName();
        KhachHang khachHang = khachHangService.getKhachHangByUserName(username);
        //Kiểm tra nếu tài khoản là admin hoặc nhân viên (không phải là khách hàng)
        if(khachHang == null){
            //Điều hướng về trang chính theo vai trò
            response.sendRedirect("/redirectByRole");
            return; //Kết thúc xử lý tại đảy cho tài khoản không phải là khách hàng
        }
            // Lưu userId vào session để sử dụng sau này
            request.getSession().setAttribute("userId", khachHang.getId());

            // Lấy sessionId hiện tại từ request
            String sessionId = (String) request.getSession().getAttribute("sessionId");

            // Lấy giỏ hàng khách hàng (nếu có)
            GioHang gioHangKhachHang = gioHangService.layGioHangTheoKhachHang(khachHang.getId());

            // Kiểm tra nếu có giỏ hàng tạm thời
            if (sessionId != null) {
                GioHang gioHangTamThoi = gioHangService.getGioHangBySessionId(sessionId);

                if (gioHangTamThoi != null) {
                    // Trường hợp khách hàng đã có giỏ hàng
                    if (gioHangKhachHang != null) {
                        // Gộp giỏ hàng tạm thời vào giỏ hàng khách hàng
                        gioHangChiTietService.mergeCart(gioHangTamThoi, gioHangKhachHang);
                        // Xóa giỏ hàng tạm thời sau khi gộp
                        gioHangService.delete(gioHangTamThoi);
                    } else {
                        // Nếu khách hàng chưa có giỏ hàng, gắn giỏ hàng tạm thời cho khách hàng
                        gioHangTamThoi.setKhachHang(khachHang);
                        gioHangService.savaGioHang(gioHangTamThoi);
                    }
                    // Xóa sessionId sau khi xử lý giỏ hàng tạm thời
                    request.getSession().removeAttribute("sessionId");
                }
            } else if (gioHangKhachHang == null) {
                // Trường hợp không có giỏ hàng nào, tạo mới giỏ hàng cho khách hàng
                GioHang gioHangMoi = new GioHang();
                gioHangMoi.setKhachHang(khachHang);
                gioHangMoi.setNgayTao(LocalDate.now());
                gioHangMoi.setTrangThai(0); // 0: Chưa thanh toán
                gioHangService.savaGioHang(gioHangMoi);
            }
        //Điều hướng về trang chính
        response.sendRedirect("/redirectByRole");
    }
}
