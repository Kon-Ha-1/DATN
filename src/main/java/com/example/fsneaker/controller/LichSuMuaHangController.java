
package com.example.fsneaker.controller;

import com.example.fsneaker.entity.DonHang;
import com.example.fsneaker.entity.DonHangChiTiet;
import com.example.fsneaker.entity.GioHang;
import com.example.fsneaker.entity.GioHangChiTiet;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.entity.LichSuDonHang;
import com.example.fsneaker.entity.SanPhamChiTiet;
import com.example.fsneaker.entity.Voucher;
import com.example.fsneaker.repositories.DonHangChiTietRepo;
import com.example.fsneaker.repositories.DonHangRepo;
import com.example.fsneaker.repositories.LichSuDonHangRepo;
import com.example.fsneaker.repositories.SanPhamChiTietRepo;
import com.example.fsneaker.repositories.VoucherRepo;
import com.example.fsneaker.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class LichSuMuaHangController {
    @Autowired
    DonHangRepo donHangRepo;

    @Autowired
    DonHangChiTietRepo donHangChiTietRepo;

    @Autowired
    GioHangService gioHangService;

    @Autowired
    KhachHangService khachHangService;

    @Autowired
    GioHangChiTietService gioHangChiTietService;

    @Autowired
    SanPhamChiTietService sanPhamChiTietService;

    @Autowired
    LichSuDonHangRepo lichSuDonHangRepo;

    @Autowired
    VoucherRepo voucherRepo;

    @Autowired
    SanPhamChiTietRepo sanPhamChiTietRepo;
    @Autowired
    DonHangService donHangService;

    @GetMapping("/lich-su-mua-hang")
    public String viewOrder(
            HttpServletRequest request, // Lấy thông tin session từ request
            Model model
    ) {

        List<Object[]> tenSanPhamVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(1);
        model.addAttribute("tenSanPhamVoiSanPham", tenSanPhamVoiSanPham);
        List<Object[]> tenSanPhamPumaVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(3);
        model.addAttribute("tenSanPhamPumaVoiSanPham", tenSanPhamPumaVoiSanPham);
        List<Object[]> tenSanPhamAdidasVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(2);
        model.addAttribute("tenSanPhamAdidasVoiSanPham", tenSanPhamAdidasVoiSanPham);
        List<Object[]> tenSanPhamNewBalanceVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(4);
        model.addAttribute("tenSanPhamNewBalanceVoiSanPham", tenSanPhamNewBalanceVoiSanPham);
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
            model.addAttribute("khachHang", khachHang);
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
        model.addAttribute("demTongSoLuongTrongGioHang", demTongSoLuongTrongGioHang);
        BigDecimal tongTien = gioHangChiTietService.tinhTongTien(gioHang.getId());
        model.addAttribute("gioHang", gioHang);
        model.addAttribute("tongTien", tongTien);


        // Lấy idKhachHang từ session
        Integer idKhachHang = (Integer) request.getSession().getAttribute("userId");
        if (idKhachHang == null) {
            // Chuyển hướng nếu chưa đăng nhập
            return "redirect:/login";
        }

        List<DonHang> listCXN = this.donHangRepo.findAllWithDetailsByTrangThai("Chờ xác nhận", idKhachHang);
        List<DonHang> listCG = this.donHangRepo.findAllWithDetailsByTrangThai("Chờ giao", idKhachHang);
        List<DonHang> listDG = this.donHangRepo.findAllWithDetailsByTrangThai("Đang giao", idKhachHang);
        List<DonHang> listDH = this.donHangRepo.findAllWithDetailsByTrangThai("Đã hủy", idKhachHang);
        List<String> statuses = Arrays.asList("Đã thanh toán", "Hoàn thành");
        List<DonHang> listHT = this.donHangRepo.findAllDetailsByTrangThai(statuses, idKhachHang);

        model.addAttribute("choXacNhan", listCXN);
        model.addAttribute("choGiao", listCG);
        model.addAttribute("dangGiao", listDG);
        model.addAttribute("hoanThanh", listHT);
        model.addAttribute("daHuy", listDH);

        return "/templatekhachhang/lich-su-mua-hang";
    }

    @GetMapping("/lich-su-mua-hang-detail/{id}")
    public String detail(
            Model model,
            @PathVariable("id") Integer idDonHang,
            HttpServletRequest request
    ) {
        List<DonHangChiTiet> listDHCT = this.donHangChiTietRepo.donHangDetail(idDonHang);


        List<Object[]> tenSanPhamVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(1);
        model.addAttribute("tenSanPhamVoiSanPham", tenSanPhamVoiSanPham);
        List<Object[]> tenSanPhamPumaVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(3);
        model.addAttribute("tenSanPhamPumaVoiSanPham", tenSanPhamPumaVoiSanPham);
        List<Object[]> tenSanPhamAdidasVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(2);
        model.addAttribute("tenSanPhamAdidasVoiSanPham", tenSanPhamAdidasVoiSanPham);
        List<Object[]> tenSanPhamNewBalanceVoiSanPham = sanPhamChiTietService.getNiekByTenSanPham(4);
        model.addAttribute("tenSanPhamNewBalanceVoiSanPham", tenSanPhamNewBalanceVoiSanPham);
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
            model.addAttribute("khachHang", khachHang);
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
        model.addAttribute("demTongSoLuongTrongGioHang", demTongSoLuongTrongGioHang);
        BigDecimal tongTien = gioHangChiTietService.tinhTongTien(gioHang.getId());
        model.addAttribute("gioHang", gioHang);
        model.addAttribute("tongTien", tongTien);
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        BigDecimal phiShip = donHangService.tinhPhiShip(donHang.getDiaChiGiaoHang(), donHang.getDonHangChiTiets());
        model.addAttribute("phiShip",phiShip);

        List<LichSuDonHang> listLSDH = this.lichSuDonHangRepo.lichSuDonHangCT(idDonHang);

        model.addAttribute("dataLSDH", listLSDH);
        model.addAttribute("dataDHCT", listDHCT);
        return "templatekhachhang/lich-su-mua-hang-chitiet";
    }

     //Thanh toán đơn hàng
    @PostMapping("/lich-su-mua-hang-detail/{orderId}/thanhtoan")
    public String payOrder(@PathVariable Integer orderId, RedirectAttributes redirectAttributes) {
        DonHang order = donHangRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if ("Đang giao".equals(order.getTrangThai())) {
            saveOrderStatusHistory(order, order.getTrangThai(), "Đã thanh toán");
            order.setTrangThai("Đã thanh toán");
            donHangRepo.save(order);
            redirectAttributes.addFlashAttribute("message", "Thanh toán đơn hàng thành công.");
        }
        return "redirect:/lich-su-mua-hang-detail/" + orderId;
    }

    // huỷ don hang
    @PostMapping("/lich-su-mua-hang-detail/{orderId}/huy")
    public String huyOrder(@PathVariable Integer orderId, RedirectAttributes redirectAttributes) {
        // Lấy thông tin đơn hàng
        DonHang order = donHangRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        // Lấy trạng thái hiện tại của đơn hàng
        String currentStatus = order.getTrangThai();

        if ("Đang giao".equals(currentStatus) || "Chờ giao".equals(currentStatus)
                || "Đang chờ".equals(currentStatus) || "Đang xử lý".equals(currentStatus)
                || "Chờ xác nhận".equals(currentStatus)) {

            // Lưu lịch sử thay đổi trạng thái
            saveOrderStatusHistory(order, currentStatus, "Đã hủy");

            // Xử lý hoàn lại số lượng nếu không phải trạng thái "Chờ xác nhận"
            if (!"Chờ xác nhận".equals(currentStatus)) {
                // Lấy danh sách chi tiết đơn hàng
                List<DonHangChiTiet> chiTietList = order.getDonHangChiTiets();

                for (DonHangChiTiet chiTiet : chiTietList) {
                    SanPhamChiTiet product = chiTiet.getSanPhamChiTiet();
                    if (product != null) {
                        // Cộng lại số lượng sản phẩm
                        product.setSoLuong(product.getSoLuong() + chiTiet.getSoLuong());
                        sanPhamChiTietRepo.save(product);
                    }
                }
            }
            // Cập nhật trạng thái đơn hàng
            order.setTrangThai("Đã hủy");
            donHangRepo.save(order);
            redirectAttributes.addFlashAttribute("message", "Huỷ đơn hàng thành công.");
        }

        return "redirect:/lich-su-mua-hang-detail/" + orderId;
    }

    private void saveOrderStatusHistory(DonHang order, String oldStatus, String newStatus) {
        LichSuDonHang history = new LichSuDonHang();
        history.setDonHang(order);
        history.setTrangThaiCu(oldStatus);
        history.setTrangThaiMoi(newStatus);
        lichSuDonHangRepo.save(history);
    }
}