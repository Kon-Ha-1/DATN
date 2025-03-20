package com.example.fsneaker.controller;

import com.example.fsneaker.entity.GioHang;
import com.example.fsneaker.entity.GioHangChiTiet;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.entity.SanPhamChiTiet;
import com.example.fsneaker.repositories.DonHangChiTietRepo;
import com.example.fsneaker.repositories.SanPhamChiTietRepo;
import com.example.fsneaker.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class  SanPhamAsicsController {
    @Autowired
    private MauSacService mauSacService;
    @Autowired
    private KichThuocService kichThuocService;
    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;
    @Autowired
    DonHangChiTietRepo donHangChiTietRepo;
    @Autowired
    private DonHangChiTietService donHangChiTietService;
    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private GioHangChiTietService gioHangChiTietService;
    @Autowired
    private KhachHangService khachHangService;
    @GetMapping("/san-pham-asics")
    public String hienThiAsicsTimKiem(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "12") int pageSize,
            @RequestParam(value = "tenMauSac", required = false) String tenMauSac,
            @RequestParam(value = "tenKichThuoc",required = false)String tenKichThuoc,
            @RequestParam(value="fromGiaBan",required = false)Integer fromGiaBan,
            @RequestParam(value="toGiaBan",required = false) Integer toGiaBan,
            @RequestParam(value="tenSanPham",required = false)String tenSanPham,
            @RequestParam(value = "sortBy", required = false, defaultValue = "0") String sortBy,
            HttpServletRequest request,
            Model model
    ) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);

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
        List<Object[]> mauSacVoiSanPham = mauSacService.getMauSacWithSanPham(5);
        model.addAttribute("mauSacVoiSanPham", mauSacVoiSanPham);
        List<Object[]> kichThuocVoiSanPham = kichThuocService.getKichThuocVoiSanPham(5);
        model.addAttribute("kichThuocVoiSanPham", kichThuocVoiSanPham);
        if(tenMauSac != null && !tenMauSac.isEmpty()){
            Page<Object[]> sanPhamTheoMauSac = sanPhamChiTietService.getThuongHieuAndMauSac(5,tenMauSac,page,pageSize);
            model.addAttribute("sanPhamTheoMauSac",sanPhamTheoMauSac);
            model.addAttribute("tenMauSac", tenMauSac);
        }else if(tenKichThuoc != null && !tenKichThuoc.isEmpty()){
            Page<Object[]> sanPhamTheoKichThuoc = sanPhamChiTietService.getThuongHieuAndKichThuoc(5,tenKichThuoc,page,pageSize);
            model.addAttribute("sanPhamTheoKichThuoc",sanPhamTheoKichThuoc);
            model.addAttribute("tenKichThuoc",tenKichThuoc);
        }else if(fromGiaBan != null && toGiaBan != null) {
            Page<Object[]> sanPhamTheoGia = sanPhamChiTietService.getSanPhamTheoThuongHieuVaGia(5,fromGiaBan, toGiaBan, page, pageSize);
            model.addAttribute("sanPhamTheoGia", sanPhamTheoGia);
            model.addAttribute("fromGiaBan", fromGiaBan);
            model.addAttribute("toGiaBan", toGiaBan);
        }else if(tenSanPham != null && !tenSanPham.isEmpty()){
            Page<Object[]> sanPhamTheoTenSanPham = sanPhamChiTietService.getNikeByTenSanPhamAndThuongHieu(5,tenSanPham, page,pageSize);
            model.addAttribute("sanPhamTheoTenSanPham", sanPhamTheoTenSanPham);
            model.addAttribute("tenSanPham", tenSanPham);
        }else{
            Page<Object[]> tatCaSanPhamAsics;
            switch (sortBy){
                case "1" -> tatCaSanPhamAsics = donHangChiTietService.getNikeByPopularity(5, page ,pageSize);
                case "2" -> tatCaSanPhamAsics = sanPhamChiTietService.getNikeNyNewest(5, page,pageSize);
                case "3" -> tatCaSanPhamAsics = sanPhamChiTietService.getNikeByPriceAsc(5, page, pageSize);
                case "4" -> tatCaSanPhamAsics = sanPhamChiTietService.getNikeByPriceDesc(5, page, pageSize);
                case "5" -> tatCaSanPhamAsics = sanPhamChiTietService.getNikeByName(5, page, pageSize);
                default -> tatCaSanPhamAsics = sanPhamChiTietService.getThuongHieuTenThuongHieu(5, page, pageSize);
            };
//            Page<Object[]> tatCaSanPhamNike = sanPhamChiTietService.getThuongHieuTenThuongHieu(1, page, pageSize);
            model.addAttribute("tatCaSanPhamAsics", tatCaSanPhamAsics);
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
        List<GioHangChiTiet> danhSachChiTiet = gioHangChiTietService.getByGioHangId(gioHang.getId());
        model.addAttribute("danhSachChiTiet", danhSachChiTiet);
        int tongSoLuongTrongGioHang = gioHangChiTietService.getSoLuongTrongGioHang(gioHang.getId());
        model.addAttribute("tongSoLuongTrongGioHang",tongSoLuongTrongGioHang);
        BigDecimal tongTien = gioHang.getGioHangChiTietList().stream()
                .map(item -> item.getGia().multiply(BigDecimal.valueOf(item.getSoLuong()))) // Phép nhân giá và số lượng
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Cộng dồn tổng
        model.addAttribute("gioHang",gioHang);
        model.addAttribute("tongTien",tongTien);
        return "templatekhachhang/san-pham-asics";
    }

}
