package com.example.fsneaker.controller;

import com.example.fsneaker.entity.GioHang;
import com.example.fsneaker.entity.GioHangChiTiet;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.entity.SanPhamChiTiet;
import com.example.fsneaker.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class TrangChuController {
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
    @GetMapping("/trang-chu")
    public String hienThiTrangChu( HttpServletRequest request, Model model){
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
        List<Object[]> sanPhamBanChayNike = donHangChiTietService.getTopSellingProductsByBrand(1, 10); // Lấy 10 sản phẩm bán chạy nhất
        model.addAttribute("sanPhamBanChayNike",sanPhamBanChayNike);
        List<Object[]> sanPhamBanChayAdidas = donHangChiTietService.getTopSellingProductsByBrand(2,10);
        model.addAttribute("sanPhamBanChayAdidas",sanPhamBanChayAdidas);
        List<Object[]> sanPhamBanChayAsics = donHangChiTietService.getTopSellingProductsByBrand(5, 10); // Lấy 10 sản phẩm bán chạy nhất
        model.addAttribute("sanPhamBanChayAsics",sanPhamBanChayAsics);
        List<Object[]> sanPhamBanChayPuma = donHangChiTietService.getTopSellingProductsByBrand( 3,10); // Lấy 10 sản phẩm bán chạy nhất
        model.addAttribute("sanPhamBanChayPuma",sanPhamBanChayPuma);
        List<Object[]> sanPhamBanChayNewBalance= donHangChiTietService.getTopSellingProductsByBrand(4,10); // Lấy 10 sản phẩm bán chạy nhất
        model.addAttribute("sanPhamBanChayNewBalance",sanPhamBanChayNewBalance);
        List<Object[]> sanPhamKhuyenMai = sanPhamChiTietService.getSanPhamChiTietKhuyenMai();
        model.addAttribute("sanPhamKhuyenMai", sanPhamKhuyenMai);
        List<Object[]> sanPhamMoi = sanPhamChiTietService.getSanPhamNewest();
        model.addAttribute("sanPhamMoi", sanPhamMoi);
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

        return "trangchu";
    }
    @GetMapping("/searchProduct")
    public String searchProduct(@RequestParam(value="keyword",required = false,defaultValue = "")String keyword,Model model){
        List<Object[]> products = sanPhamChiTietService.searchProducts(keyword);
        model.addAttribute("products",products);
        if (keyword.length() > 50) {
            keyword = keyword.substring(0, 50); // Giới hạn độ dài
        }
        keyword = keyword.replaceAll("[^a-zA-Z0-9 ]", ""); // Loại bỏ ký tự đặc biệt
        return "templatekhachhang/searchResults :: resultsList";
    }
}
