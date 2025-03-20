package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.GioHang;
import com.example.fsneaker.entity.GioHangChiTiet;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.service.GioHangChiTietService;
import com.example.fsneaker.service.GioHangService;
import com.example.fsneaker.service.KhachHangService;
import com.example.fsneaker.service.SanPhamChiTietService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class ThongTinKhachHangController {
    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private GioHangChiTietService gioHangChiTietService;
    @Autowired
    private GioHangService  gioHangService;
    @Autowired
    private KhachHangService khachHangService;
    @GetMapping("/thong-tin-ca-nhan")
    public String thongTinCaNhan(HttpServletRequest request,Model model){
        List<Object[]> tenSanPhamVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(1);
        model.addAttribute("tenSanPhamVoiSanPham", tenSanPhamVoiSanPham);
        List<Object[]> tenSanPhamPumaVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(3);
        model.addAttribute("tenSanPhamPumaVoiSanPham", tenSanPhamPumaVoiSanPham);
        List<Object[]> tenSanPhamAdidasVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(2);
        model.addAttribute("tenSanPhamAdidasVoiSanPham",tenSanPhamAdidasVoiSanPham);
        List<Object[]> tenSanPhamNewBalanceVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(4);
        model.addAttribute("tenSanPhamNewBalanceVoiSanPham",tenSanPhamNewBalanceVoiSanPham);
        List<Object[]> tenSanPhamAsicsVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(5);
        model.addAttribute("tenSanPhamAsicsVoiSanPham", tenSanPhamAsicsVoiSanPham);

        String sessionId;
        GioHang gioHang;
        // Kiểm tra nếu người dùng chưa đăng nhập
        if (request.getSession().getAttribute("userId") == null) {
            // Lấy hoặc tạo sessionId
            sessionId = (String) request.getSession().getAttribute("sessionId");
            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString(); // Tạo sessionId duy nhất
                request.getSession().setAttribute("sessionId", sessionId);
            }
            // Tìm giỏ hàng theo sessionId
            gioHang = gioHangService.getGioHangBySessionId(sessionId);
            if (gioHang == null) {
                // Nếu chưa có giỏ hàng, tạo giỏ hàng mới
                gioHang = new GioHang();
                gioHang.setMaGioHang(sessionId);
                gioHang.setNgayTao(LocalDate.now());
                gioHang.setTrangThai(0); // 0: Chưa thanh toán
                gioHangService.savaGioHang(gioHang);
            }
        } else {
            // Nếu người dùng đã đăng nhập, lấy giỏ hàng theo tài khoản
            int userId = (int) request.getSession().getAttribute("userId");
            gioHang = gioHangService.getGioHangByUserId(userId);
            KhachHang khachHang = khachHangService.getKhachHangById(userId);
            model.addAttribute("khachHang",khachHang);
            // Nếu giỏ hàng chưa có, tạo giỏ hàng mới và gắn với khách hàng
            if (gioHang == null) {
                gioHang = new GioHang();
                gioHang.setKhachHang(khachHang); // Gắn khách hàng vào giỏ hàng
                gioHang.setNgayTao(LocalDate.now());
                gioHang.setTrangThai(0); // 0: Chưa thanh toán
                gioHangService.savaGioHang(gioHang);
            }
            // Nếu có session giỏ hàng tạm thời, gộp vào giỏ hàng đã đăng nhập
            sessionId = (String) request.getSession().getAttribute("sessionId");
            if (sessionId != null) {
                GioHang tempGioHang = gioHangService.getGioHangBySessionId(sessionId);
                if (tempGioHang != null) {
                    gioHangChiTietService.mergeCart(tempGioHang, gioHang);
                    gioHangService.delete(tempGioHang); // Xóa giỏ hàng tạm thời
                    request.getSession().removeAttribute("sessionId"); // Xóa sessionId
                }
            }
        }
        // Tính toán dữ liệu hiển thị
        List<GioHangChiTiet> danhSachChiTiet = gioHangChiTietService.getByGioHangId(gioHang.getId());
        model.addAttribute("danhSachChiTiet", danhSachChiTiet);
        int demTongSoLuongTrongGioHang = gioHangChiTietService.getSoLuongTrongGioHang(gioHang.getId());
        model.addAttribute("demTongSoLuongTrongGioHang",demTongSoLuongTrongGioHang);
        BigDecimal tongTien = gioHangChiTietService.tinhTongTien(gioHang.getId());
        model.addAttribute("gioHang", gioHang);
        model.addAttribute("tongTien", tongTien);
        return "templatekhachhang/thong-tin-khach-hang";
    }
    @PostMapping("/doi-thong-tin")
    public String doiThongTin(@ModelAttribute("khachHang") KhachHang khachHang, Authentication authentication, HttpServletRequest request, RedirectAttributes redirectAttributes,Model model){
        Map<String, String> errors = new HashMap<>();
        //Kiểm tra thủ công từng trường
        if(khachHang.getTenKhachHang() == null || khachHang.getTenKhachHang().isBlank()){
            errors.put("tenKhachHang", "Tên khách hàng không được để trống!");
        }else if(khachHang.getTenKhachHang().length() < 5 || khachHang.getTenKhachHang().length() > 30){
            errors.put("tenKhachHang","Tên khách hàng phải từ 5 đến 30 ký tự!");
        }
        if(khachHang.getEmail() == null || khachHang.getEmail().isBlank()){
            errors.put("email","Email không được để trống!");
        }else if(!khachHang.getEmail().matches(".+@.+\\..+")){
            errors.put("email","Email không hợp lệ!");
        }
        if(khachHang.getDiaChi() == null || khachHang.getDiaChi().isBlank()){
            errors.put("diaChi","Địa chỉ không được để trống!");
        }else if(khachHang.getDiaChi().length() < 5 || khachHang.getDiaChi().length() > 100){
            errors.put("diaChi", "Địa chỉ phải từ 5 đến 100 ký tự!");
        }else if(khachHang.getDiaChi().trim().toLowerCase().contains("hà nội")){
            // Danh sách quận/ huyện tại Hà Nội
            List<String> quanHuyenHaNoi = List.of(
                    "ba đình", "hoàn kiếm", "đống đa", "thanh xuân",
                    "cầu giấy", "hai bà trưng", "hoàng mai", "long biên",
                    "hà đông", "bắc từ liêm", "nam từ liêm", "tây hồ",
                    "thanh trì", "gia lâm", "đông anh", "sóc sơn",
                    "mê linh", "chương mỹ", "thanh oai", "thường tín",
                    "phúc thọ", "đan phượng", "quốc oai", "hoài đức",
                    "ba vì", "mỹ đức", "ứng hòa", "thạch thất", "phú xuyên"
            );
            // Kiểm tra xem địa chỉ có chứa thông tin chi tiết (ngõ, phố, khu vực, số nhà)
            boolean coChiTiet = khachHang.getDiaChi().toLowerCase().matches(".*(ngõ|hẻm|số|phố|khu|tòa nhà|làng|thôn).*");

            // Kiểm tra xem địa chỉ có phường/xã
            boolean coPhuongXa = khachHang.getDiaChi().toLowerCase().matches(".*(phường|xã|thị trấn).*");

            //Kiểm tra xem địa chỉ có chứa một quận/huyện hợp lệ hay không
            boolean coQuanHuyen =quanHuyenHaNoi.stream().anyMatch(khachHang.getDiaChi().toLowerCase()::contains);
            if(!coQuanHuyen){
                errors.put("diaChi", "Địa chỉ Hà Nội phải bao gồm quận hoặc huyện cụ thể, ví dụ: 'Hà Đông, Hà Nội' hoặc 'Gia Lâm, Hà Nội'.");
            }
            // Nếu thiếu thông tin chi tiết, báo lỗi
            if (!coChiTiet) {
                errors.put("diaChi", "Địa chỉ phải bao gồm thông tin cụ thể như ngõ, phố, số nhà hoặc khu vực!");
            }
            // Nếu thiếu phường/xã, báo lỗi
            if (!coPhuongXa) {
                errors.put("diaChi", "Địa chỉ phải bao gồm phường hoặc xã!");
            }
        }
        if(khachHang.getSoDienThoai() == null || khachHang.getSoDienThoai().isBlank()){
            errors.put("soDienThoai", "Số điện thoại không được để trống!");
        }else if(!khachHang.getSoDienThoai().matches("^\\d+$")){
            errors.put("soDienThoai","Số điện thoại phải là số");
        }else if(khachHang.getSoDienThoai().length() != 10 && khachHang.getSoDienThoai().length() != 11){
            errors.put("soDienThoai", "Số điện thoại phải là 10 hoặc 11 số!");
        }
        if(!errors.isEmpty()){
            List<Object[]> tenSanPhamVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(1);
            model.addAttribute("tenSanPhamVoiSanPham", tenSanPhamVoiSanPham);
            List<Object[]> tenSanPhamPumaVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(3);
            model.addAttribute("tenSanPhamPumaVoiSanPham", tenSanPhamPumaVoiSanPham);
            List<Object[]> tenSanPhamAdidasVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(2);
            model.addAttribute("tenSanPhamAdidasVoiSanPham",tenSanPhamAdidasVoiSanPham);
            List<Object[]> tenSanPhamNewBalanceVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(4);
            model.addAttribute("tenSanPhamNewBalanceVoiSanPham",tenSanPhamNewBalanceVoiSanPham);
            List<Object[]> tenSanPhamAsicsVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(5);
            model.addAttribute("tenSanPhamAsicsVoiSanPham", tenSanPhamAsicsVoiSanPham);

            String sessionId;
            GioHang gioHang;
            // Kiểm tra nếu người dùng chưa đăng nhập
            if (request.getSession().getAttribute("userId") == null) {
                // Lấy hoặc tạo sessionId
                sessionId = (String) request.getSession().getAttribute("sessionId");
                if (sessionId == null) {
                    sessionId = UUID.randomUUID().toString(); // Tạo sessionId duy nhất
                    request.getSession().setAttribute("sessionId", sessionId);
                }
                // Tìm giỏ hàng theo sessionId
                gioHang = gioHangService.getGioHangBySessionId(sessionId);
                if (gioHang == null) {
                    // Nếu chưa có giỏ hàng, tạo giỏ hàng mới
                    gioHang = new GioHang();
                    gioHang.setMaGioHang(sessionId);
                    gioHang.setNgayTao(LocalDate.now());
                    gioHang.setTrangThai(0); // 0: Chưa thanh toán
                    gioHangService.savaGioHang(gioHang);
                }
            } else {
                // Nếu người dùng đã đăng nhập, lấy giỏ hàng theo tài khoản
                int userId = (int) request.getSession().getAttribute("userId");
                gioHang = gioHangService.getGioHangByUserId(userId);
                KhachHang khachHang1 = khachHangService.getKhachHangById(userId);
                model.addAttribute("khachHang",khachHang1);
                // Nếu giỏ hàng chưa có, tạo giỏ hàng mới và gắn với khách hàng
                if (gioHang == null) {
                    gioHang = new GioHang();
                    gioHang.setKhachHang(khachHang1); // Gắn khách hàng vào giỏ hàng
                    gioHang.setNgayTao(LocalDate.now());
                    gioHang.setTrangThai(0); // 0: Chưa thanh toán
                    gioHangService.savaGioHang(gioHang);
                }
                // Nếu có session giỏ hàng tạm thời, gộp vào giỏ hàng đã đăng nhập
                sessionId = (String) request.getSession().getAttribute("sessionId");
                if (sessionId != null) {
                    GioHang tempGioHang = gioHangService.getGioHangBySessionId(sessionId);
                    if (tempGioHang != null) {
                        gioHangChiTietService.mergeCart(tempGioHang, gioHang);
                        gioHangService.delete(tempGioHang); // Xóa giỏ hàng tạm thời
                        request.getSession().removeAttribute("sessionId"); // Xóa sessionId
                    }
                }
            }
            // Tính toán dữ liệu hiển thị
            List<GioHangChiTiet> danhSachChiTiet = gioHangChiTietService.getByGioHangId(gioHang.getId());
            model.addAttribute("danhSachChiTiet", danhSachChiTiet);
            int demTongSoLuongTrongGioHang = gioHangChiTietService.getSoLuongTrongGioHang(gioHang.getId());
            model.addAttribute("demTongSoLuongTrongGioHang",demTongSoLuongTrongGioHang);
            BigDecimal tongTien = gioHangChiTietService.tinhTongTien(gioHang.getId());
            model.addAttribute("gioHang", gioHang);
            model.addAttribute("tongTien", tongTien);
            model.addAttribute("errors",errors);
            return "templatekhachhang/thong-tin-khach-hang";
        }
            int userId = (int) request.getSession().getAttribute("userId");
            KhachHang khachHangCu = khachHangService.getKhachHangById(userId);
            khachHangCu.setTenKhachHang(khachHang.getTenKhachHang());
            khachHangCu.setEmail(khachHang.getEmail());
            khachHangCu.setSoDienThoai(khachHang.getSoDienThoai());
            khachHangCu.setNgaySinh(khachHang.getNgaySinh());
            khachHangCu.setDiaChi(khachHang.getDiaChi());
            khachHangCu.setGioiTinh(khachHang.getGioiTinh());
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String tenNguoiDung = userDetails.getDisplayName();
            khachHangService.themKH(khachHangCu,tenNguoiDung);
            redirectAttributes.addFlashAttribute("message", "Đổi thông tin thành công!");
        return "redirect:/thong-tin-ca-nhan";
    }

    @PostMapping("/doi-mat-khau")
    public String doiMatKhau(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("matKhauCu") String matKhauCu,
            @ModelAttribute("matKhauMoi") String matKhauMoi,
            @ModelAttribute("xacNhanMatKhauMoi") String xacNhanMatKhauMoi,
            Authentication authentication,
            Model model) {

        int userId = (int) request.getSession().getAttribute("userId");
        KhachHang khachHang = khachHangService.getKhachHangById(userId);


        // Kiểm tra mật khẩu cũ
        if (!khachHang.getMatKhau().equals(matKhauCu)) {
            System.out.println("Mật khẩu cũ không đúng!"); // Debug log
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu cũ không đúng!");
            return "redirect:/thong-tin-ca-nhan";  // Quay lại trang thông tin cá nhân và hiển thị lỗi
        }

        // Kiểm tra xác nhận mật khẩu mới
        if (!matKhauMoi.equals(xacNhanMatKhauMoi)) {
            System.out.println("Xác nhận mật khẩu mới không khớp!"); // Debug log
            redirectAttributes.addFlashAttribute("errorMessage", "Xác nhận mật khẩu mới không khớp!");
            return "redirect:/thong-tin-ca-nhan";  // Quay lại trang thông tin cá nhân và hiển thị lỗi
        }

        // Cập nhật mật khẩu mới
        khachHang.setMatKhau(matKhauMoi);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        khachHangService.themKH(khachHang,tenNguoiDung);

        System.out.println("Đổi mật khẩu thành công!"); // Debug log
        redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công!");
        return "redirect:/login";
    }

}
