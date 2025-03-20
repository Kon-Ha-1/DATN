package com.example.fsneaker.controller;

import com.example.fsneaker.entity.*;
import com.example.fsneaker.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class GioHangController {
    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private GioHangChiTietService gioHangChiTietService;
    @Autowired
    private DonHangService donHangService;
    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private DonHangChiTietService  donHangChiTietService;
    @GetMapping("/gio-hang")
    public String hienThiGioHang(HttpServletRequest request, Model model){
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
            Long sessionCreatedTime = (Long) request.getSession().getAttribute("sessionCreatedTime");
            if (sessionId == null || sessionCreatedTime == null || System.currentTimeMillis() - sessionCreatedTime > 7*24*60*60*1000) {//Hơn 1 tuần
                //Nếu hết hạn, xóa giỏ hàng tạm thời cũ
                if(sessionId != null){
                    GioHang tempGioHang = gioHangService.getGioHangBySessionId(sessionId);
                    if(tempGioHang != null){
                        gioHangService.delete(tempGioHang);
                    }
                    request.getSession().removeAttribute("sessionId");
                    request.getSession().removeAttribute("sessionCreatedTime");
                }
                sessionId = UUID.randomUUID().toString(); // Tạo sessionId duy nhất
                request.getSession().setAttribute("sessionId", sessionId);
                request.getSession().setAttribute("sessionCreatedTime", System.currentTimeMillis());
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
        return "templatekhachhang/gio-hang";
    }
    //    @PostMapping("/them-vao-gio-hang")
//    @ResponseBody
//    public ResponseEntity<?> themVaoGioHang(
//            @RequestParam(value = "idSanPham", required = false) Integer idSanPham,
//            @RequestParam(value = "idKichThuoc", required = false) Integer idKichThuoc,
//            @RequestParam(value = "idMauSac", required = false) Integer idMauSac,
//            @RequestParam(value = "soLuong", required = false) Integer soLuong,
//            HttpServletRequest request) {
//
//
//        String sessionId;
//        GioHang gioHang;
//        // Kiểm tra nếu người dùng chưa đăng nhập
//        if (request.getSession().getAttribute("userId") == null) {
//            // Lấy hoặc tạo sessionId
//            sessionId = (String) request.getSession().getAttribute("sessionId");
//            if (sessionId == null) {
//                sessionId = UUID.randomUUID().toString(); // Tạo sessionId duy nhất
//                request.getSession().setAttribute("sessionId", sessionId);
//            }
//            // Tìm giỏ hàng theo sessionId
//            gioHang = gioHangService.getGioHangBySessionId(sessionId);
//            if (gioHang == null) {
//                // Nếu chưa có giỏ hàng, tạo giỏ hàng mới
//                gioHang = new GioHang();
//                gioHang.setMaGioHang(sessionId);
//                gioHang.setNgayTao(LocalDate.now());
//                gioHang.setTrangThai(0); // 0: Chưa thanh toán
//                gioHangService.savaGioHang(gioHang);
//            }
//        } else {
//            // Nếu người dùng đã đăng nhập, lấy giỏ hàng theo tài khoản
//            int userId = (int) request.getSession().getAttribute("userId");
//            gioHang = gioHangService.getGioHangByUserId(userId);
//            KhachHang khachHang = khachHangService.getKhachHangById(userId);
//            // Nếu giỏ hàng chưa có, tạo giỏ hàng mới và gắn với khách hàng
//            if (gioHang == null) {
//                gioHang = new GioHang();
//                gioHang.setKhachHang(khachHang); // Gắn khách hàng vào giỏ hàng
//                gioHang.setNgayTao(LocalDate.now());
//                gioHang.setTrangThai(0); // 0: Chưa thanh toán
//                gioHangService.savaGioHang(gioHang);
//            }
//            // Nếu có session giỏ hàng tạm thời, gộp vào giỏ hàng đã đăng nhập
//            sessionId = (String) request.getSession().getAttribute("sessionId");
//            if (sessionId != null) {
//                GioHang tempGioHang = gioHangService.getGioHangBySessionId(sessionId);
//                if (tempGioHang != null) {
//                    gioHangChiTietService.mergeCart(tempGioHang, gioHang);
//                    gioHangService.delete(tempGioHang); // Xóa giỏ hàng tạm thời
//                    request.getSession().removeAttribute("sessionId"); // Xóa sessionId
//                }
//            }
//        }
//
//        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.getBySanPhamIdAndKichThuocIdAndMauSacId(idSanPham,idKichThuoc,idMauSac);
//        if(sanPhamChiTiet == null){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sản phẩm không tồn tại!");
//        }
//        if(sanPhamChiTiet.getSoLuong() < soLuong){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sản phẩm chỉ còn "+sanPhamChiTiet.getSoLuong());
//        }
//
//            //Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
//            GioHangChiTiet gioHangChiTiet = gioHangChiTietService.getByGioHangIdAndSanPhamIdAndKichThuocIdAndMauSacId(gioHang.getId(),idSanPham,idKichThuoc,idMauSac);
//            if(gioHangChiTiet != null){
//                //Sản phẩm đã tồn tại, cập nhật số lượng
//                gioHangChiTiet.setSoLuong(gioHangChiTiet.getSoLuong() + soLuong);
//                gioHangChiTietService.saveGioHangChiTiet(gioHangChiTiet);
//            }else {
//                String part2 = String.format("%05d", new Random().nextInt(100000));
//                //Sản phẩm chưa tồn tại, thêm mới
//                GioHangChiTiet newgioHangChiTiet = new GioHangChiTiet();
//                newgioHangChiTiet.setMaGioHangChiTiet("GHCT" + part2);
//                newgioHangChiTiet.setGioHang(gioHang);
//                newgioHangChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
//                newgioHangChiTiet.setSoLuong(soLuong);
//                newgioHangChiTiet.setGia(sanPhamChiTiet.getGiaBan());
//                gioHangChiTietService.saveGioHangChiTiet(newgioHangChiTiet);
//            }
//        return ResponseEntity.ok("Thêm vào giỏ hàng thành công");
//    }
    @PostMapping("/them-vao-gio-hang")
    public String themVaoGIoHang(@RequestParam(value = "idSanPham",required = false) Integer idSanPham,@RequestParam(value = "idKichThuoc",required = false)Integer idKichThuoc, @RequestParam(value = "idMauSac",required = false)Integer idMauSac,@RequestParam(value = "soLuong",required = false) Integer soLuong,HttpServletRequest request, RedirectAttributes redirectAttributesl, Model model){
        String sessionId;
        GioHang gioHang;
        // Kiểm tra nếu người dùng chưa đăng nhập
        if (request.getSession().getAttribute("userId") == null) {
            // Lấy hoặc tạo sessionId
            sessionId = (String) request.getSession().getAttribute("sessionId");
            Long sessionCreatedTime = (Long) request.getSession().getAttribute("sessionCreatedTime");
            if (sessionId == null || sessionCreatedTime == null || System.currentTimeMillis() - sessionCreatedTime > 7*24*60*60*1000) {//Hơn 1 tuần
                //Nếu hết hạn, xóa giỏ hàng tạm thời cũ
                if(sessionId != null){
                    GioHang tempGioHang = gioHangService.getGioHangBySessionId(sessionId);
                    if(tempGioHang != null){
                        gioHangService.delete(tempGioHang);
                    }
                    request.getSession().removeAttribute("sessionId");
                    request.getSession().removeAttribute("sessionCreatedTime");
                }
                sessionId = UUID.randomUUID().toString(); // Tạo sessionId duy nhất
                request.getSession().setAttribute("sessionId", sessionId);
                request.getSession().setAttribute("sessionCreatedTime", System.currentTimeMillis());
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

        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.getBySanPhamIdAndKichThuocIdAndMauSacId(idSanPham,idKichThuoc,idMauSac);
        if(sanPhamChiTiet == null){
            redirectAttributesl.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại!");
            return "redirect:/gio-hang";
        }
        if(sanPhamChiTiet.getSoLuong() < soLuong){
            redirectAttributesl.addFlashAttribute("errorMessage","Sản phẩm chỉ còn "+sanPhamChiTiet.getSoLuong());
            return "redirect:/xem-chi-tiet/"+sanPhamChiTiet.getId();
        }

        //Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        GioHangChiTiet gioHangChiTiet = gioHangChiTietService.getByGioHangIdAndSanPhamIdAndKichThuocIdAndMauSacId(gioHang.getId(),idSanPham,idKichThuoc,idMauSac);
        if(gioHangChiTiet != null){
            //Sản phẩm đã tồn tại, cập nhật số lượng
            if(gioHangChiTiet.getSoLuong() + soLuong > sanPhamChiTiet.getSoLuong()){
                redirectAttributesl.addFlashAttribute("errorMessage", "Không thể thêm quá số lượng trong kho!");
                return "redirect:/xem-chi-tiet/"+sanPhamChiTiet.getId();
            }
            gioHangChiTiet.setSoLuong(gioHangChiTiet.getSoLuong() + soLuong);
            gioHangChiTietService.saveGioHangChiTiet(gioHangChiTiet);
        }else {
            String part2 = String.format("%05d", new Random().nextInt(100000));
            //Sản phẩm chưa tồn tại, thêm mới
            GioHangChiTiet newgioHangChiTiet = new GioHangChiTiet();
            newgioHangChiTiet.setMaGioHangChiTiet("GHCT" + part2);
            newgioHangChiTiet.setGioHang(gioHang);
            newgioHangChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
            newgioHangChiTiet.setSoLuong(soLuong);
            newgioHangChiTiet.setGia(sanPhamChiTiet.getSanPham().getKhuyenMai() != null ? sanPhamChiTiet.getGiaBanGiamGia() : sanPhamChiTiet.getGiaBan());
            gioHangChiTietService.saveGioHangChiTiet(newgioHangChiTiet);
        }
        redirectAttributesl.addFlashAttribute("message","Thêm sản phẩm vào giỏ hàng thành công!");
        return "redirect:/xem-chi-tiet/"+sanPhamChiTiet.getId();
    }
    @PostMapping("/xoa-san-pham")
    public String xoaSanPhamTrongGioHang(@RequestParam(value = "idSanPham",required = false)Integer idSanPham,@RequestParam(value = "idKichThuoc",required = false)Integer idKichThuoc,@RequestParam(value ="idMauSac",required = false)Integer idMauSac, HttpServletRequest request,RedirectAttributes redirectAttributes){
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

        //Lấy sản phẩm chi tiết từ giỏ hàng
        GioHangChiTiet gioHangChiTiet = gioHangChiTietService.getByGioHangIdAndSanPhamIdAndKichThuocIdAndMauSacId(gioHang.getId(),idSanPham,idKichThuoc,idMauSac);
        if(gioHangChiTiet != null){
//                //Hoàn lại số lượng tồn kho
//                SanPhamChiTiet sanPhamChiTiet = gioHangChiTiet.getSanPhamChiTiet();
//                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() + gioHangChiTiet.getSoLuong());
//                sanPhamChiTietService.capNhatSanPhamChiTiet(sanPhamChiTiet);

            //Xóa sản phẩm chi tiết khỏi giở hàng
            gioHangChiTietService.deleteGioHangChiTiet(gioHangChiTiet);
        }
        redirectAttributes.addFlashAttribute("message","Xóa sản phẩm thành công!");

        return "redirect:/gio-hang";
    }
    @PostMapping("/cap-nhat-gio-hang")
    public String  capNhatGioHang(@RequestParam(value ="idSanPham",required = false)Integer idSanPham, @RequestParam(value = "idKichThuoc",required = false)Integer idKichThuoc, @RequestParam(value="idMauSac",required = false)Integer idMauSac, @RequestParam(value="soLuong",required = false)Integer soLuong,HttpServletRequest request ,RedirectAttributes redirectAttributes){
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
        //Tìm sản phẩm trong giỏ hàng
        GioHangChiTiet gioHangChiTiet = gioHangChiTietService.getByGioHangIdAndSanPhamIdAndKichThuocIdAndMauSacId(gioHang.getId(),idSanPham,idKichThuoc,idMauSac);
        if(gioHangChiTiet != null){

            if(soLuong == null){
                redirectAttributes.addFlashAttribute("message","Số lượng không được để trống!");
                return "redirect:/gio-hang";
            }
            int oldQuantity = gioHangChiTiet.getSoLuong();
            int defference = soLuong - oldQuantity;
            SanPhamChiTiet sanPhamChiTiet = gioHangChiTiet.getSanPhamChiTiet();
            if(gioHangChiTiet.getSoLuong() + soLuong > sanPhamChiTiet.getSoLuong()){
                redirectAttributes.addFlashAttribute("errorMessage", "Số lượng trong kho không đủ!");
                return "redirect:/gio-hang";
            }
            if(sanPhamChiTiet.getSoLuong() < defference){
                redirectAttributes.addFlashAttribute("errorMessage", "Số lượng trong kho không đủ!");
                return "redirect:/gio-hang";
            }
            gioHangChiTiet.setSoLuong(soLuong);
            gioHangChiTietService.saveGioHangChiTiet(gioHangChiTiet);

//                //Điều chỉnh số lượng tồn kho
//                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - defference);
//                sanPhamChiTietService.capNhatSanPhamChiTiet(sanPhamChiTiet);
        }
        redirectAttributes.addFlashAttribute("message","Cập nhật sản phẩm thành công!");
        return "redirect:/gio-hang";
    }
}