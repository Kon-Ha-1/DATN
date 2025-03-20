package com.example.fsneaker.controller;

import com.example.fsneaker.entity.GioHang;
import com.example.fsneaker.entity.GioHangChiTiet;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.entity.SanPhamChiTiet;
import com.example.fsneaker.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
public class SanPhamNewBalanceController {
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
    @GetMapping("/san-pham-newbalance")
    public String hienThiNewBalance(@RequestParam(value = "page",defaultValue = "0")int page,
                                    Model model,
                                    @RequestParam(value = "tenSanPham", required = false) String tenSanPham,
                                    @RequestParam(value = "color", required = false) String color,
                                    @RequestParam(value = "kichThuoc", required = false) String kichThuoc,
                                    @RequestParam(value = "minGia", required = false) Integer minGia,
                                    @RequestParam(value = "maxGia", required = false) Integer maxGia,
                                    @RequestParam(value = "sortBy", required = false, defaultValue = "0") String sortBy,
                                    HttpServletRequest  request

    ){
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
        List<Object[]> mauSacVoiSanPham = mauSacService.getMauSacWithSanPham(4);
        model.addAttribute("mauSacVoiSanPham",mauSacVoiSanPham);
        List<Object[]> kichThuocVoiSanPham = kichThuocService.getKichThuocVoiSanPham(4);
        model.addAttribute("kichThuocVoiSanPham",kichThuocVoiSanPham);


        if(color != null && !color.isEmpty()){
            Page<Object[]> sanPhamTheoMauSac = sanPhamChiTietService.getThuongHieuAndMauSac(4,color,page,pageSize);
            model.addAttribute("sanPhamTheoMauSac",sanPhamTheoMauSac);
            model.addAttribute("color", color);
        }
        else if(kichThuoc != null && !kichThuoc.isEmpty()){
            Page<Object[]> sanPhamTheoKichThuoc = sanPhamChiTietService.getThuongHieuAndKichThuoc(4,kichThuoc,page,pageSize);
            model.addAttribute("sanPhamTheoKichThuoc",sanPhamTheoKichThuoc);
            model.addAttribute("kichThuoc", kichThuoc);
        }
        else if(minGia != null && maxGia != null){
            Page<Object[]> sanPhamTheoGia = sanPhamChiTietService.getSanPhamTheoThuongHieuVaGia(4,minGia, maxGia, page, pageSize);
            model.addAttribute("sanPhamTheoGia", sanPhamTheoGia);
            model.addAttribute("minGia", minGia);
            model.addAttribute("maxGia", maxGia);
        } else if (tenSanPham !=null && !tenSanPham.isEmpty()) {
            Page<Object[]> sanPhamTheoTenSanPham = sanPhamChiTietService.getNikeByTenSanPhamAndThuongHieu(4,tenSanPham, page,pageSize);
            model.addAttribute("sanPhamTheoTenSanPham", sanPhamTheoTenSanPham);
            model.addAttribute("tenSanPham", tenSanPham);
        }
        else {
            Page<Object[]> tatCaSanPhamNewBlance;
            switch (sortBy){
                case "1" -> tatCaSanPhamNewBlance = donHangChiTietService.getNikeByPopularity(4, page ,pageSize);
                case "2" -> tatCaSanPhamNewBlance = sanPhamChiTietService.getNikeNyNewest(4, page,pageSize);
                case "3" -> tatCaSanPhamNewBlance = sanPhamChiTietService.getNikeByPriceAsc(4, page, pageSize);
                case "4" -> tatCaSanPhamNewBlance = sanPhamChiTietService.getNikeByPriceDesc(4, page, pageSize);
                case "5" -> tatCaSanPhamNewBlance = sanPhamChiTietService.getNikeByName(4, page, pageSize);
                default -> tatCaSanPhamNewBlance = sanPhamChiTietService.getThuongHieuTenThuongHieu(4, page, pageSize);
            };
            model.addAttribute("tatCaSanPhamNewBalance",tatCaSanPhamNewBlance);
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
        return "templatekhachhang/san-pham-newbalance";
    }

    @GetMapping("/xem-chi-tiet-newbalance/{id}")
    public String xemChiTiet(@PathVariable("id") Integer id,HttpServletRequest request,Model model){
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
        List<Object[]> mauSacVoiSanPham = mauSacService.getMauSacWithSanPham(4);
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.getSanPhamChiTietById(id);
        model.addAttribute("sanPhamChiTiet", sanPhamChiTiet);
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
        List<SanPhamChiTiet> sanPhamChiTiets = sanPhamChiTietService.getSanPhamChiTietBySanPhamIdAndMauSacId(sanPhamChiTiet.getSanPham().getId(), sanPhamChiTiet.getMauSac().getId());
        model.addAttribute("sanPhamChiTiets", sanPhamChiTiets);
        return "templatekhachhang/chi-tiet-san-pham";
    }
}