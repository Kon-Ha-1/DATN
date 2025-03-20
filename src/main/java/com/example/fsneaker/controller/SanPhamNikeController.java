package com.example.fsneaker.controller;


import com.example.fsneaker.entity.*;
import com.example.fsneaker.repositories.SanPhamChiTietRepo;
import com.example.fsneaker.repositories.SanPhamRepo;
import com.example.fsneaker.service.*;
import jakarta.persistence.Column;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class SanPhamNikeController {
    @Autowired
    private SanPhamRepo sanPhamRepo;
    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;
    @Autowired
    private MauSacService mauSacService;
    @Autowired
    private KichThuocService kichThuocService;
    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private DonHangChiTietService donHangChiTietService;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private GioHangChiTietService gioHangChiTietService;
    @Autowired
    private KhachHangService khachHangService;
    @GetMapping("/san-pham-nike")
    public String hienThiSanPhamNike(@RequestParam(value = "page",defaultValue = "0")int page, @RequestParam(value ="tenSanPham",required = false) String tenSanPham, @RequestParam(value = "color", required = false)String color, @RequestParam(value = "kichThuoc",required = false)String kichThuoc, @RequestParam(value = "minGia",required = false)Integer minGia, @RequestParam(value = "maxGia",required = false)Integer maxGia, HttpServletRequest request,@RequestParam(value = "sortBy",required = false, defaultValue = "0") String sortBy, Model model){
        int pageSize = 12;
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
        List<Object[]> mauSacVoiSanPham = mauSacService.getMauSacWithSanPham(1);
        model.addAttribute("mauSacVoiSanPham",mauSacVoiSanPham);
        List<Object[]> kichThuocVoiSanPham = kichThuocService.getKichThuocVoiSanPham(1);
        model.addAttribute("kichThuocVoiSanPham",kichThuocVoiSanPham);
        if(color != null && !color.isEmpty()){
            Page<Object[]> sanPhamTheoMauSac = sanPhamChiTietService.getThuongHieuAndMauSac(1,color,page,pageSize);
            model.addAttribute("sanPhamTheoMauSac",sanPhamTheoMauSac);
            model.addAttribute("color", color);
        }else if(kichThuoc != null && !kichThuoc.isEmpty()){
            Page<Object[]> sanPhamTheoKichThuoc = sanPhamChiTietService.getThuongHieuAndKichThuoc(1,kichThuoc,page,pageSize);
            model.addAttribute("sanPhamTheoKichThuoc",sanPhamTheoKichThuoc);
            model.addAttribute("kichThuoc",kichThuoc);
        }else if(minGia != null && maxGia != null) {
            Page<Object[]> sanPhamTheoGia = sanPhamChiTietService.getSanPhamTheoThuongHieuVaGia(1,minGia, maxGia, page, pageSize);
            model.addAttribute("sanPhamTheoGia", sanPhamTheoGia);
            model.addAttribute("minGia", minGia);
            model.addAttribute("maxGia", maxGia);
        }else if(tenSanPham != null && !tenSanPham.isEmpty()){
            Page<Object[]> sanPhamTheoTenSanPham = sanPhamChiTietService.getNikeByTenSanPhamAndThuongHieu(1,tenSanPham, page,pageSize);
            model.addAttribute("sanPhamTheoTenSanPham", sanPhamTheoTenSanPham);
            model.addAttribute("tenSanPham", tenSanPham);
        }else{
            Page<Object[]> tatCaSanPhamNike;
            switch (sortBy){
                case "1" -> tatCaSanPhamNike = donHangChiTietService.getNikeByPopularity(1, page ,pageSize);
                case "2" -> tatCaSanPhamNike = sanPhamChiTietService.getNikeNyNewest(1, page,pageSize);
                case "3" -> tatCaSanPhamNike = sanPhamChiTietService.getNikeByPriceAsc(1, page, pageSize);
                case "4" -> tatCaSanPhamNike = sanPhamChiTietService.getNikeByPriceDesc(1, page, pageSize);
                case "5" -> tatCaSanPhamNike = sanPhamChiTietService.getNikeByName(1, page, pageSize);
                default -> tatCaSanPhamNike = sanPhamChiTietService.getThuongHieuTenThuongHieu(1, page, pageSize);
            };
//            Page<Object[]> tatCaSanPhamNike = sanPhamChiTietService.getThuongHieuTenThuongHieu(1, page, pageSize);
            model.addAttribute("tatCaSanPhamNike", tatCaSanPhamNike);
            model.addAttribute("sortBy",sortBy);
        }
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
        // Tính toán dữ liệu hiển thị
        List<GioHangChiTiet> danhSachChiTiet = gioHangChiTietService.getByGioHangId(gioHang.getId());
        model.addAttribute("danhSachChiTiet", danhSachChiTiet);
        int demTongSoLuongTrongGioHang = gioHangChiTietService.getSoLuongTrongGioHang(gioHang.getId());
        model.addAttribute("demTongSoLuongTrongGioHang",demTongSoLuongTrongGioHang);
        BigDecimal tongTien = gioHangChiTietService.tinhTongTien(gioHang.getId());
        model.addAttribute("gioHang", gioHang);
        model.addAttribute("tongTien", tongTien);
        return "templatekhachhang/san-pham-nike";
    }
    //    @GetMapping("/san-pham-sap-xep")
//    public String hienThiSanPhamNikeSapXep(HttpServletRequest request,@RequestParam(value = "page",defaultValue = "0") int page,@RequestParam(value = "sortBy",required = false, defaultValue = "0") String sortBy, Model model){
//        int pageSize = 12;
////        System.out.println("Received sortBy: " + sortBy);
////        if (sortBy != null && sortBy == 0) {
////            sortBy = null; // hoặc gán lại về giá trị mặc định nào đó nếu cần
////        }
//        List<Object[]> tenSanPhamVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(1);
//        model.addAttribute("tenSanPhamVoiSanPham", tenSanPhamVoiSanPham);
//        List<Object[]> tenSanPhamPumaVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(3);
//        model.addAttribute("tenSanPhamPumaVoiSanPham", tenSanPhamPumaVoiSanPham);
//        List<Object[]> tenSanPhamAdidasVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(2);
//        model.addAttribute("tenSanPhamAdidasVoiSanPham",tenSanPhamAdidasVoiSanPham);
//        List<Object[]> tenSanPhamNewBalanceVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(4);
//        model.addAttribute("tenSanPhamNewBalanceVoiSanPham",tenSanPhamNewBalanceVoiSanPham);
//        List<Object[]> tenSanPhamAsicsVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(5);
//        model.addAttribute("tenSanPhamAsicsVoiSanPham", tenSanPhamAsicsVoiSanPham);
//        List<Object[]> mauSacVoiSanPham = mauSacService.getMauSacWithSanPham(1);
//        model.addAttribute("mauSacVoiSanPham",mauSacVoiSanPham);
//        List<Object[]> kichThuocVoiSanPham = kichThuocService.getKichThuocVoiSanPham(1);
//        model.addAttribute("kichThuocVoiSanPham",kichThuocVoiSanPham);
//
//        String sessionId;
//        GioHang gioHang;
//
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
//        // Tính toán dữ liệu hiển thị
//        List<GioHangChiTiet> danhSachChiTiet = gioHangChiTietService.getByGioHangId(gioHang.getId());
//        model.addAttribute("danhSachChiTiet", danhSachChiTiet);
//        int demTongSoLuongTrongGioHang = gioHangChiTietService.getSoLuongTrongGioHang(gioHang.getId());
//        model.addAttribute("demTongSoLuongTrongGioHang",demTongSoLuongTrongGioHang);
//        BigDecimal tongTien = gioHangChiTietService.tinhTongTien(gioHang.getId());
//        model.addAttribute("gioHang", gioHang);
//        model.addAttribute("tatCaSanPhamNike",tatCaSanPhamNike);
//        model.addAttribute("sortBy",sortBy);
//        model.addAttribute("tongTien",tongTien);
//        return "templatekhachhang/san-pham-nike";
//    }
    @GetMapping("/xem-chi-tiet/{id}")
    public String xemChiTiet(@PathVariable("id") Integer id,HttpServletRequest request, Model model){
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
        List<Object[]> mauSacVoiSanPham = mauSacService.getMauSacWithSanPham(1);
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.getSanPhamChiTietById(id);
        model.addAttribute("sanPhamChiTiet", sanPhamChiTiet);
        List<SanPhamChiTiet> sanPhamChiTiets = sanPhamChiTietService.getSanPhamChiTietBySanPhamIdAndMauSacId(sanPhamChiTiet.getSanPham().getId(), sanPhamChiTiet.getMauSac().getId());
        model.addAttribute("sanPhamChiTiets", sanPhamChiTiets);
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

        // Tính toán dữ liệu hiển thị
        List<GioHangChiTiet> danhSachChiTiet = gioHangChiTietService.getByGioHangId(gioHang.getId());
        model.addAttribute("danhSachChiTiet", danhSachChiTiet);
        int demTongSoLuongTrongGioHang = gioHangChiTietService.getSoLuongTrongGioHang(gioHang.getId());
        model.addAttribute("demTongSoLuongTrongGioHang",demTongSoLuongTrongGioHang);
        BigDecimal tongTien = gioHangChiTietService.tinhTongTien(gioHang.getId());
        model.addAttribute("gioHang", gioHang);
        model.addAttribute("tongTien", tongTien);
        return "templatekhachhang/chi-tiet-san-pham";
    }
}
