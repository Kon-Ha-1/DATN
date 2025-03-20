package com.example.fsneaker.controller;

import com.example.fsneaker.entity.DonHang;
import com.example.fsneaker.entity.DonHangChiTiet;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.entity.KichThuoc;
import com.example.fsneaker.entity.LichSuDonHang;
import com.example.fsneaker.entity.MauSac;
import com.example.fsneaker.entity.SanPhamChiTiet;
import com.example.fsneaker.entity.Voucher;
import com.example.fsneaker.repositories.DonHangChiTietRepo;
import com.example.fsneaker.repositories.DonHangRepo;
import com.example.fsneaker.repositories.KhachHangRepo;
import com.example.fsneaker.repositories.KichThuocRepo;
import com.example.fsneaker.repositories.LichSuDonHangRepo;
import com.example.fsneaker.repositories.MauSacRepo;
import com.example.fsneaker.repositories.SanPhamChiTietRepo;
import com.example.fsneaker.repositories.VoucherRepo;
import com.example.fsneaker.service.DonHangService;
import com.example.fsneaker.service.PhieuGiaoHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class HoaDonController {
    @Autowired
    DonHangRepo donHangRepo;

    @Autowired
    DonHangChiTietRepo donHangChiTietRepo;

    @Autowired
    LichSuDonHangRepo lichSuDonHangRepo;

    @Autowired
    KhachHangRepo khachHangRepo;

    @Autowired
    SanPhamChiTietRepo sanPhamChiTietRepo;

    @Autowired
    VoucherRepo voucherRepo;

    @Autowired
    MauSacRepo mauSacRepo;

    @Autowired
    KichThuocRepo kichThuocRepo;

    @Autowired
    PhieuGiaoHangService phieuGiaoHangService;
    @Autowired
    DonHangService donHangService;

    @GetMapping("/donhangadmin")
    public String donHang(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam Optional<LocalDateTime> startDate,
            @RequestParam Optional<LocalDateTime> endDate,
//            @RequestParam Optional<String> trangThai,
            @RequestParam(required = false, defaultValue = "tatca") String tab
    ) {
        String s = "%" + keyword + "%";
        PageRequest pageRequest = PageRequest.of(page,limit);
        Page<DonHang> list = this.donHangRepo.searchPageHoaDon(
                s,
                startDate.orElse(null),
                endDate.orElse(null),
                pageRequest
        );
        // Tạo một bản đồ lưu phí ship cho từng đơn hàng
        Map<Integer, BigDecimal> shippingFees = new HashMap<>();

        // Tính phí ship cho từng đơn hàng
        for (DonHang donHang : list.getContent()) {
            List<DonHangChiTiet> listDHCT = this.donHangChiTietRepo.donHangDetail(donHang.getId());
            BigDecimal phiShip = donHangService.tinhPhiShip(donHang.getDiaChiGiaoHang(), listDHCT);
            shippingFees.put(donHang.getId(), phiShip);
        }

        model.addAttribute("shippingFees", shippingFees);

        Page<DonHang> listCXN = this.donHangRepo.searchPageHoaDonfindStatus(
                s,
                startDate.orElse(null),
                endDate.orElse(null),
                "Chờ xác nhận",
                pageRequest
        );

        Page<DonHang> listCG = this.donHangRepo.searchPageHoaDonfindStatus(
                s,
                startDate.orElse(null),
                endDate.orElse(null),
                "Chờ giao",
                pageRequest
        );
        Page<DonHang> listDG = this.donHangRepo.searchPageHoaDonfindStatus(
                s,
                startDate.orElse(null),
                endDate.orElse(null),
                "Đang giao",
                pageRequest
        );
        List<String> statuses = Arrays.asList("Đã thanh toán", "Hoàn thành");
        Page<DonHang> listHT = this.donHangRepo.searchPageHoaDonfindListStatus(
                s,
                startDate.orElse(null),
                endDate.orElse(null),
                statuses,
                pageRequest
        );

        Page<DonHang> listDH = this.donHangRepo.searchPageHoaDonfindStatus(
                s,
                startDate.orElse(null),
                endDate.orElse(null),
                "Đã hủy",
                pageRequest
        );

        if (tab == null || tab.trim().isEmpty()) {
            tab = "tatca"; // Giá trị mặc định
        }
        model.addAttribute("data", list);
        model.addAttribute("choXacNhan", listCXN);
        model.addAttribute("choGiao", listCG);
        model.addAttribute("dangGiao", listDG);
        model.addAttribute("hoanThanh", listHT);
        model.addAttribute("daHuy", listDH);
        model.addAttribute("tab", tab);
        return "templateadmin/donhangadmin";
    }

    @GetMapping("/donhangadmin-detail/{id}")
    public String detail(
            Model model,
            @PathVariable("id") Integer idDonHang
    ){
        List<DonHangChiTiet> listDHCT = this.donHangChiTietRepo.donHangDetail(idDonHang);
        List<LichSuDonHang> listLSDH = this.lichSuDonHangRepo.lichSuDonHangCT(idDonHang);
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        BigDecimal phiShip = donHangService.tinhPhiShip(donHang.getDiaChiGiaoHang(), listDHCT);

        BigDecimal tongTien = donHang.getTongTien(); // Tổng tiền gốc
        BigDecimal soTienGiam = BigDecimal.ZERO;    // Mặc định là 0
        if (donHang.getGiamGia() != null) {
            BigDecimal giaTriGiam = donHang.getGiamGia().getGiaTri();
            if ("Giảm giá %".equals(donHang.getGiamGia().getLoaiVoucher())) {
                soTienGiam = tongTien.multiply(giaTriGiam).divide(BigDecimal.valueOf(100));
                model.addAttribute("soTienGiam", soTienGiam);
            }
        }

        model.addAttribute("phiShip",phiShip);
        model.addAttribute("dataLSDH",listLSDH);
        model.addAttribute("dataDHCT", listDHCT);
        return "templateadmin/donhang-detail";
    }


    // Xác nhận đơn hàng
    @PostMapping("/donhangadmin-detail/{orderId}/confirm")
    public String confirmOrder(@PathVariable Integer orderId,  RedirectAttributes redirectAttributes) {
        // Lấy thông tin đơn hàng
        DonHang order = donHangRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        // Kiểm tra trạng thái hợp lệ để chuyển đổi
        if ("Đang chờ".equals(order.getTrangThai()) || "Đang xử lý".equals(order.getTrangThai()) || "Chờ xác nhận".equals(order.getTrangThai())) {
            // Lưu lịch sử trạng thái
            saveOrderStatusHistory(order, order.getTrangThai(), "Chờ giao");

            // Cập nhật trạng thái đơn hàng
            order.setTrangThai("Chờ giao");

            // Lấy danh sách chi tiết đơn hàng liên quan
            List<DonHangChiTiet> chiTietList = order.getDonHangChiTiets();

            // Trừ số lượng sản phẩm trong kho
            for (DonHangChiTiet chiTiet : chiTietList) {
                SanPhamChiTiet product = chiTiet.getSanPhamChiTiet();
                // Kiểm tra xem sản phẩm còn đủ hàng hay không
                if (product.getSoLuong() < chiTiet.getSoLuong()) {
                    throw new RuntimeException("Số lượng sản phẩm không đủ để thực hiện đơn hàng.");
                }
                product.setSoLuong(product.getSoLuong() - chiTiet.getSoLuong());
                sanPhamChiTietRepo.save(product);
            }
            donHangRepo.save(order);
            redirectAttributes.addFlashAttribute("message", "Xác nhận đơn hàng thành công.");
        }
        return "redirect:/donhangadmin-detail/" + orderId;
    }


    // Vận chuyển đơn hàng
    @PostMapping("/donhangadmin-detail/{orderId}/ship")
    public String shipOrder(@PathVariable Integer orderId,  RedirectAttributes redirectAttributes) {
        DonHang order = donHangRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if ("Chờ giao".equals(order.getTrangThai())) {
            saveOrderStatusHistory(order, order.getTrangThai(), "Đang giao");
            order.setTrangThai("Đang giao");
            donHangRepo.save(order);
            redirectAttributes.addFlashAttribute("message", "Chuyển trạng thái đơn hàng thành công.");
        }
        return "redirect:/donhangadmin-detail/" + orderId;
    }

    // Thanh toán đơn hàng
    @PostMapping("/donhangadmin-detail/{orderId}/pay")
    public String payOrder(@PathVariable Integer orderId,  RedirectAttributes redirectAttributes) {
        DonHang order = donHangRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if ("Đang giao".equals(order.getTrangThai())) {
            saveOrderStatusHistory(order, order.getTrangThai(), "Đã thanh toán");
            order.setTrangThai("Đã thanh toán");
            donHangRepo.save(order);
            redirectAttributes.addFlashAttribute("message", "Chuyển trạng thái đơn hàng thành công.");
        }
        return "redirect:/donhangadmin-detail/" + orderId;
    }

    // Hoàn thành đơn hàng
    @PostMapping("/donhangadmin-detail/{orderId}/hoanThanh")
    public String hoanThanhOrder(@PathVariable Integer orderId,  RedirectAttributes redirectAttributes) {
        DonHang order = donHangRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if ("Đã thanh toán".equals(order.getTrangThai())) {
            saveOrderStatusHistory(order, order.getTrangThai(), "Hoàn thành");
            order.setTrangThai("Hoàn thành");
            donHangRepo.save(order);
            redirectAttributes.addFlashAttribute("message", "Chuyển trạng thái đơn hàng thành công.");
        }
        return "redirect:/donhangadmin-detail/" + orderId;
    }

    // huỷ don hang
    @PostMapping("/donhangadmin-detail/{orderId}/huy")
    public String huyOrder(@PathVariable Integer orderId,  RedirectAttributes redirectAttributes) {
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
            // Hoàn lại số lượng voucher nếu đã được áp dụng
//            Voucher appliedVoucher = order.getGiamGia(); // Giả định mỗi đơn hàng chỉ dùng 1 voucher
//            if (appliedVoucher != null) {
//                appliedVoucher.setSoLuong(appliedVoucher.getSoLuong() + 1); // Cộng lại 1 voucher đã được áp dụng
//                voucherRepo.save(appliedVoucher);
//            }
            // Cập nhật trạng thái đơn hàng
            order.setTrangThai("Đã hủy");
            donHangRepo.save(order);
            redirectAttributes.addFlashAttribute("message", "Huỷ đơn hàng thành công.");
        }

        return "redirect:/donhangadmin-detail/" + orderId;
    }

    // Lưu lịch sử trạng thái
    private void saveOrderStatusHistory(DonHang order, String oldStatus, String newStatus) {
        LichSuDonHang history = new LichSuDonHang();
        history.setDonHang(order);
        history.setTrangThaiCu(oldStatus);
        history.setTrangThaiMoi(newStatus);
        lichSuDonHangRepo.save(history);
    }

    //cập nhật khách hàng
    @PostMapping("/update-customer/{idDonHang}")
    public String updateCustomer(
            @PathVariable("idDonHang") Integer idDonHang,
            @RequestParam("tenKhachHang") String tenKhachHang,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("email") String email,
            @RequestParam("diaChi") String diaChi,
            RedirectAttributes redirectAttributes) {
//        KhachHang existingCustomer = khachHangRepo.findById(idKhachHang)
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + idKhachHang));
//        // Cập nhật thông tin khách hàng
//        existingCustomer.setTenKhachHang(tenKhachHang);
//        existingCustomer.setSoDienThoai(soDienThoai);
//        existingCustomer.setEmail(email);
//        existingCustomer.setDiaChi(diaChi);
//        khachHangRepo.save(existingCustomer);

        // Tìm đơn hàng hiện tại trong cơ sở dữ liệu
        DonHang donHang = donHangRepo.findById(idDonHang)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + idDonHang));

        // Cập nhật thông tin khách hàng trong bảng đơn hàng
        donHang.setTenNguoiNhan(tenKhachHang);
        donHang.setSoDienThoaiGiaoHang(soDienThoai);
        donHang.setEmailGiaoHang(email);
        donHang.setDiaChiGiaoHang(diaChi);
        donHangRepo.save(donHang);
        redirectAttributes.addFlashAttribute("message", "Thay đổi thông tin người nhận thành công.");
        return "redirect:/donhangadmin-detail/" + idDonHang;
    }

    @GetMapping("/edit-sanpham-donhang/{id}")
    public String editDonHang(
            Model model,
            @PathVariable("id") Integer idDonHang,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam Optional<String> tenMauSac,
            @RequestParam Optional<String> tenKichThuoc
    ) {
        String s = "%" + keyword + "%";
        PageRequest pageRequest = PageRequest.of(page,limit);
        Page<SanPhamChiTiet> listSPCT = this.sanPhamChiTietRepo.findAddOrder(s, tenMauSac.orElse(null), tenKichThuoc.orElse(null), pageRequest);
        List<MauSac> listMS = this.mauSacRepo.findAll();
        List<KichThuoc> listKT = this.kichThuocRepo.findAll();

        model.addAttribute("dataMS", listMS);
        model.addAttribute("dataKT", listKT);
        model.addAttribute("dataSPCT", listSPCT);
        model.addAttribute("orderId", idDonHang);
        return "templateadmin/sua-sanpham-donhang";
    }

    //thêm san pham vào don hang
    @PostMapping("/donhangadmin-detail/{orderId}/add-product")
    public String addProductToOrder(@PathVariable Integer orderId,
                                    @RequestParam Integer productId,
                                    @RequestParam Integer soLuong,
                                    RedirectAttributes redirectAttributes) {
        // Lấy thông tin đơn hàng
        DonHang order = donHangRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        SanPhamChiTiet product = sanPhamChiTietRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra trạng thái đơn hàng (chỉ cho phép thêm sản phẩm khi đang chờ xác nhận)
        if (!"Chờ xác nhận".equals(order.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể thêm sản phẩm khi đơn hàng ở trạng thái 'Chờ xác nhận'.");
        }

        // Tạo mới một chi tiết đơn hàng
        DonHangChiTiet donHangChiTiet = new DonHangChiTiet();
        donHangChiTiet.setDonHang(order);
        donHangChiTiet.setSanPhamChiTiet(product);
        donHangChiTiet.setSoLuong(soLuong);
        donHangChiTiet.setGia(product.getGiaBan()); // Lấy giá hiện tại của sản phẩm
        BigDecimal thanhTien = product.getGiaBan().multiply(BigDecimal.valueOf(soLuong));
        donHangChiTiet.setThanhTien(thanhTien);

        // Lưu chi tiết đơn hàng
        donHangChiTietRepo.save(donHangChiTiet);

        // Tính lại tổng giá trị đơn hàng
        BigDecimal tongGiaTri = order.getDonHangChiTiets().stream()
                .map(DonHangChiTiet::getThanhTien) // Lấy giá trị thành tiền (BigDecimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Tính tổng bằng phương thức add

        order.setTongTien(tongGiaTri); // Gán tổng giá trị mới cho đơn hàng

        // Xử lý mã voucher (nếu có)
        if (order.getGiamGia() != null) {
            Voucher voucher = order.getGiamGia();

            // Tính tổng tiền giảm giá
            BigDecimal tongTienGiamGia;
            if ("Giảm giá %".equals(voucher.getLoaiVoucher())) {
                // Voucher giảm giá theo phần trăm
                tongTienGiamGia = tongGiaTri.subtract(tongGiaTri.multiply(voucher.getGiaTri()).divide(BigDecimal.valueOf(100)));
            } else if ("Giảm giá số tiền".equals(voucher.getLoaiVoucher())) {
                // Voucher giảm giá theo số tiền
                tongTienGiamGia = tongGiaTri.subtract(voucher.getGiaTri());
            } else {
                tongTienGiamGia = BigDecimal.ZERO; // Loại voucher không hợp lệ
            }

            // Đảm bảo tổng tiền giảm giá không vượt quá tổng giá trị đơn hàng
            if (tongTienGiamGia.compareTo(tongGiaTri) > 0) {
                tongTienGiamGia = tongGiaTri;
            }

            // Lưu tổng tiền giảm giá vào đơn hàng
            order.setTongTienGiamGia(tongTienGiamGia);
        }
//        else {
//            // Nếu không có voucher, đặt tổng tiền giảm giá là 0
//            order.setTongTienGiamGia(BigDecimal.ZERO);
//        }

        donHangRepo.save(order);
        redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm vào đơn hàng thành công.");
        return "redirect:/donhangadmin-detail/" + orderId;
    }


    // xoá don hang chi tiet
    @PostMapping("/donhangadmin-detail/{orderId}/remove-product/{chiTietId}")
    public String removeProductFromOrder(@PathVariable Integer orderId, @PathVariable Integer chiTietId, RedirectAttributes redirectAttributes) {
        // Tìm đơn hàng
        DonHang order = donHangRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Xóa chi tiết đơn hàng khỏi danh sách
        donHangChiTietRepo.deleteById(chiTietId);
        // Tính lại tổng giá trị đơn hàng
        BigDecimal newTongGiaTri = order.getDonHangChiTiets().stream()
                .map(DonHangChiTiet::getThanhTien) // Lấy giá trị thành tiền (BigDecimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Tính tổng bằng phương thức add

        order.setTongTien(newTongGiaTri);

        if (order.getGiamGia() != null) {
            Voucher voucher = order.getGiamGia();

            // Tính tổng tiền giảm giá
            BigDecimal tongTienGiamGia;
            if ("Giảm giá %".equals(voucher.getLoaiVoucher())) {
                // Voucher giảm giá theo phần trăm
                tongTienGiamGia = newTongGiaTri.subtract(newTongGiaTri.multiply(voucher.getGiaTri()).divide(BigDecimal.valueOf(100)));
            } else if ("Giảm giá số tiền".equals(voucher.getLoaiVoucher())) {
                // Voucher giảm giá theo số tiền
                tongTienGiamGia = newTongGiaTri.subtract(voucher.getGiaTri());
            } else {
                tongTienGiamGia = BigDecimal.ZERO; // Loại voucher không hợp lệ
            }

            // Đảm bảo tổng tiền giảm giá không vượt quá tổng giá trị đơn hàng
            if (tongTienGiamGia.compareTo(newTongGiaTri) > 0) {
                tongTienGiamGia = newTongGiaTri;
            }

            // Lưu tổng tiền giảm giá vào đơn hàng
            order.setTongTienGiamGia(tongTienGiamGia);
        }
        redirectAttributes.addFlashAttribute("message", "Xoá sản phẩm đơn hàng thành công.");
        donHangRepo.save(order);

        return "redirect:/donhangadmin-detail/" + orderId;
    }

    //cập nhật số lượng
    @PostMapping("/donhangadmin-detail/{orderId}/update-product-quantity/{chiTietId}")
    public String updateProductQuantity(@PathVariable Integer orderId,
                                        @PathVariable Integer chiTietId,
                                        @RequestParam Integer newQuantity,
                                        RedirectAttributes redirectAttributes) {
        // Tìm đơn hàng
        DonHang order = donHangRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Kiểm tra danh sách chi tiết đơn hàng
        if (order.getDonHangChiTiets() == null || order.getDonHangChiTiets().isEmpty()) {
            throw new RuntimeException("Không có chi tiết đơn hàng nào trong đơn hàng này");
        }

        // Tìm chi tiết đơn hàng cần cập nhật
        DonHangChiTiet chiTietToUpdate = order.getDonHangChiTiets().stream()
                .filter(chiTiet -> chiTiet.getId() == chiTietId )
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng với ID: " + chiTietId));

        // Kiểm tra số lượng mới (phải lớn hơn 0)
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0");
        }

        // Tính toán và cập nhật số lượng và thành tiền
        BigDecimal pricePerUnit = chiTietToUpdate.getSanPhamChiTiet().getGiaBan(); // Giá mỗi sản phẩm
        chiTietToUpdate.setSoLuong(newQuantity); // Cập nhật số lượng
        chiTietToUpdate.setThanhTien(pricePerUnit.multiply(BigDecimal.valueOf(newQuantity))); // Cập nhật thành tiền

        // Cập nhật tổng giá trị đơn hàng
        BigDecimal newTongGiaTri = order.getDonHangChiTiets().stream()
                .map(DonHangChiTiet::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTongTien(newTongGiaTri);

        if (order.getGiamGia() != null) {
            Voucher voucher = order.getGiamGia();

            // Tính tổng tiền giảm giá
            BigDecimal tongTienGiamGia;
            if ("Giảm giá %".equals(voucher.getLoaiVoucher())) {
                // Voucher giảm giá theo phần trăm
                tongTienGiamGia = newTongGiaTri.subtract(newTongGiaTri.multiply(voucher.getGiaTri()).divide(BigDecimal.valueOf(100)));
            } else if ("Giảm giá số tiền".equals(voucher.getLoaiVoucher())) {
                // Voucher giảm giá theo số tiền
                tongTienGiamGia = newTongGiaTri.subtract(voucher.getGiaTri());
            } else {
                tongTienGiamGia = BigDecimal.ZERO; // Loại voucher không hợp lệ
            }

            // Đảm bảo tổng tiền giảm giá không vượt quá tổng giá trị đơn hàng
            if (tongTienGiamGia.compareTo(newTongGiaTri) > 0) {
                tongTienGiamGia = newTongGiaTri;
            }

            // Lưu tổng tiền giảm giá vào đơn hàng
            order.setTongTienGiamGia(tongTienGiamGia);
        }

        // Lưu thay đổi
        donHangRepo.save(order);
        redirectAttributes.addFlashAttribute("message", "Cập nhật số lượng đơn hàng thành công.");
        return "redirect:/donhangadmin-detail/" + orderId;
    }

    @GetMapping("/donhang/view-inphieugiao/{orderId}")
    public ResponseEntity<Resource> inPhieuGiao(@PathVariable Integer orderId) {
        phieuGiaoHangService.inPhieuGiao(orderId);
        String fileName = "in_phieu_giao_" + orderId + ".pdf";
        File file = new File(fileName);

        if (!file.exists()) {
            throw new RuntimeException("Phiếu giao hàng không tồn tại.");
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/donhang/view-inhoadon/{orderId}")
    public ResponseEntity<Resource> inHoaDon(@PathVariable Integer orderId) {
        phieuGiaoHangService.inHoaDon(orderId);
        String fileName = "in_hoa_don_" + orderId + ".pdf";
        File file = new File(fileName);

        if (!file.exists()) {
            throw new RuntimeException("Hoá đơn không tồn tại.");
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
