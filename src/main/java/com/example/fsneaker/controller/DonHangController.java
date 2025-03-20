
package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.*;
import com.example.fsneaker.service.*;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/don-hang")
public class DonHangController {
    @Autowired
    private DonHangService donHangService;
    @Autowired
    private DonHangChiTietService donHangChiTietService;
    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private NhanVienService nhanVienService;
    @GetMapping("/chi-tiet")
    public String loadDataGioHang(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "id", required = false) Integer id, @SessionAttribute(value = "khachHang", required = false) KhachHang khachHang, Model model) {
        int pageSizeM = 10;
        Page<SanPhamChiTiet> sanPhamChiTietPage;
        if (keyword != null && !keyword.isEmpty()) {
            sanPhamChiTietPage = sanPhamChiTietService.searchPaginated(keyword, page, pageSizeM);
            model.addAttribute("keyword", keyword);
        } else {
            sanPhamChiTietPage = sanPhamChiTietService.findPaginated(page, pageSizeM);
        }
        if (id != null) { //Kiểm tra xem có id không
            //Lấy danh sách sản phẩm chi tiết theo tráng
            DonHang donHang = donHangService.getDonHangById(id);

            model.addAttribute("tongTien", donHang.getTongTien() != null ? donHang.getTongTien() : BigDecimal.ZERO);
            model.addAttribute("tongTienChuaGiam", donHang.getTongTien());
            model.addAttribute("khachHangId",donHang.getKhachHang());
            //Kiểm tra xem có voucher không và cập nhật thông tin
            boolean hasVoucher = donHang.getGiamGia() != null;
            model.addAttribute("hasVoucher", hasVoucher);
            if (hasVoucher) {
                Voucher voucher = donHang.getGiamGia(); //Lấy voucher đã áp dụng
                BigDecimal tongTienGiamGia = donHang.getTongTienGiamGia(); // Tổng tiền sau khi giảm giá
                BigDecimal tienGiam = donHang.getTongTien().subtract(tongTienGiamGia);// Tính số tiền được giảm
                model.addAttribute("tongTien", tongTienGiamGia);//Cập nhật tổng tiền hiển thị
                model.addAttribute("voucher", voucher); //Thêm thông tin voucher vào model
                model.addAttribute("tienGiam", tienGiam); //Thêm vào model
            }
            //Lấy danh sách voucher có trạng thái và giá tri phù hợp, sau đó lọc theo tổng tiền
            List<Voucher> vouchers = voucherService.getAllVoucherByTrangThaiAndGiaTri(1);
            List<Voucher> filteredVouchers = vouchers.stream()
                    .filter(voucher -> voucher.getDonToiThieu() != null &&
                            donHang.getTongTien() != null &&
                            voucher.getDonToiThieu().compareTo(donHang.getTongTien()) <= 0)
                    .collect(Collectors.toList());
            model.addAttribute("vouchers", filteredVouchers);
            List<DonHangChiTiet> dsSanPhamtrongGioHang = donHangChiTietService.getAllDonHangChiTietById(id);
            model.addAttribute("gioHangs", dsSanPhamtrongGioHang);
//            model.addAttribute("khachHang", new KhachHang());
            //Hiển thị một form trống để người dùng tạo hóa đơn mới
            model.addAttribute("donHang", donHang);
        } else {
            model.addAttribute("gioHangs", null); //Không có id thì không hiển thị giỏ haàng
            model.addAttribute("donHang", null); //Không hiển thị thông tin hóa đơn
            model.addAttribute("khachHang", null);
        }
        // Hiển thị danh sách hóa đơn hiện có
        model.addAttribute("donHangs", donHangService.getDonHangByTrangThai());
        //Hiển thị danh sách sản phẩm chi tiết hiện có
        model.addAttribute("sanPhamChiTiets", sanPhamChiTietPage);
        model.addAttribute("khachHang", new KhachHang());
        //model.addAttribute("voucher", voucher);
        return "templateadmin/banhangtaiquay";
    }

    // Hiển thị form tạo hóa đơn và danh sách hóa đơn trên cùng một trang
    @GetMapping("/hien-thi")
    public String showCreateHoaDonForm(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        int pageSize = 10;
        Page<SanPhamChiTiet> sanPhamChiTietPage;
        if (keyword != null && !keyword.isEmpty()) {
            sanPhamChiTietPage = sanPhamChiTietService.searchPaginated(keyword, page, pageSize);
            model.addAttribute("keyword", keyword);
        } else {
            sanPhamChiTietPage = sanPhamChiTietService.findPaginated(page, pageSize);
        }
        List<Voucher> vouchers = voucherService.getAllVoucherByTrangThaiAndGiaTri(1);
        model.addAttribute("vouchers", vouchers);
        //Hiển thị một form trống để người dùng tạo hóa đơn mới
        model.addAttribute("donHang", new DonHang());
        // Hiển thị danh sách hóa đơn hiện có
        model.addAttribute("donHangs", donHangService.getDonHangByTrangThai());
        //model.addAttribute("message", "Lỗi cc");
        //Hiển thị danh sách sản phẩm chi tiết hiện có
        model.addAttribute("khachHang", new KhachHang());
        model.addAttribute("sanPhamChiTiets", sanPhamChiTietPage);
        return "templateadmin/banhangtaiquay";
    }

    @GetMapping("/tim-kiem-khach-hang")
    public String timKiemKhachHang(@RequestParam("keyword") String keyword, @RequestParam(value = "idDonHang", required = false) Integer idDonHang, RedirectAttributes redirectAttributes, Model model) {
        //Tìm kiếm khách hàng theo số điện thoại hoặc email
        KhachHang khachHang = khachHangService.timKiemTheoSoDienThoaiHoacEmail(keyword);
        //Lây hóa đơn theo id
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        if(donHang == null){
            redirectAttributes.addFlashAttribute("errorMessage","Chưa chọn hóa đơn hoặc chưa có hóa đơn được tạo!");
            return "redirect:/don-hang/hien-thi";
        }
        //Kiểm tra xem hóa đơn đã có khách hàng hay không
        boolean hasCustomer = donHang.getKhachHang() != null;
        model.addAttribute("existingCustomer", hasCustomer);

        if (khachHang != null) {
            if (hasCustomer) {
                //Nếu hóa đơn đã có khách hàng, kiểm tra xem có cần thay thế hay không
                //Ví dụ: có thể thêm logic xác nhận người dùng muốn thay thế khách hàng hiện tại
                // Để đơn giản, ta thay thế khách hàng ngày lập tức ở dây

                //Thay thế khách hàng hiện tại bằng khách hàng mới
                donHang.setKhachHang(khachHang);
                donHangService.capNhatDonHang(donHang);
                redirectAttributes.addFlashAttribute("message", "Khách hàng đã được thay thế vào hóa đơn!");
            }
            //Nêu tìm thấy khách hàng, gán khách hàng vào hóa đơn
            if (donHang.getKhachHang() == null) {
                donHang.setKhachHang(khachHang);
                donHangService.capNhatDonHang(donHang);
                redirectAttributes.addFlashAttribute("message", "Khách hàng đã được gán vào hóa đơn!");
            }
            model.addAttribute("khachHang", khachHang);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng với số điện thoại hoặc email này!");
        }
        //Nếu có hóa đơn, tiính lại tổng tiền sau giảm giá
        if (donHang.getGiamGia() != null) {
            BigDecimal tongTienGiamGia = donHang.getTongTienGiamGia();
            model.addAttribute("tongTien", tongTienGiamGia);
            model.addAttribute("voucher", donHang.getGiamGia());
        }
        //Hiển thị danh sách sản phẩm trong giỏ hàng
        model.addAttribute("gioHangs", donHangChiTietService.getAllDonHangChiTietById(idDonHang));
        return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
    }
    @GetMapping("/them-khach-hang-le")
    public String themKhachHangLe(@RequestParam(value = "idDonHang", required = false) Integer idDonHang, RedirectAttributes redirectAttributes, Model model){
        KhachHang khachHang =khachHangService.getKhachHangById(1);
        //Lây hóa đơn theo id
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        if(donHang == null){
            redirectAttributes.addFlashAttribute("errorMessage","Chưa chọn hóa đơn hoặc chưa có hóa đơn được tạo!");
            return "redirect:/don-hang/hien-thi";
        }
        if (donHang.getKhachHang() != null) {
            donHang.setKhachHang(khachHang);
            donHangService.capNhatDonHang(donHang);
            redirectAttributes.addFlashAttribute("message", "Khách hàng đã được thay thế vào hóa đơn!");
        }else {
            donHang.setKhachHang(khachHang);
            donHangService.capNhatDonHang(donHang);
            redirectAttributes.addFlashAttribute("message", "Khách hàng đã được gắn vào hóa đơn!");
        }
        //Nếu có hóa đơn, tiính lại tổng tiền sau giảm giá
        if (donHang.getGiamGia() != null) {
            BigDecimal tongTienGiamGia = donHang.getTongTienGiamGia();
            model.addAttribute("tongTien", tongTienGiamGia);
            model.addAttribute("voucher", donHang.getGiamGia());
        }
        //Hiển thị danh sách sản phẩm trong giỏ hàng
        model.addAttribute("gioHangs", donHangChiTietService.getAllDonHangChiTietById(idDonHang));
        return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
    }
    @PostMapping("/them-khach-hang")
    public String themKhachHang(@ModelAttribute("khachHang")KhachHang khachHang,@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "page", defaultValue = "0") int page,@RequestParam(value = "idDonHang",required = false) Integer idDonHang,RedirectAttributes redirectAttributes, Model model){

        Map<String, String> errors = new HashMap<>();
        //Kiểm tra thủ công từng trường
        if(khachHang.getTenKhachHang() == null || khachHang.getTenKhachHang().isEmpty()){
            errors.put("tenKhachHang", "Tên khách hàng không được để trống!");
        }
        if(khachHang.getTenKhachHang().length() < 5 || khachHang.getTenKhachHang().length() > 30){
            errors.put("tenKhachHang","Tên khách hàng phải từ 5 đến 30 ký tự!");
        }
        if(khachHang.getEmail() == null || khachHang.getEmail().isEmpty()){
            errors.put("email","Email không được để trống!");
        }
        if(!khachHang.getEmail().matches(".+@.+\\..+")){
            errors.put("email","Email không hợp lệ!");
        }
        if(khachHang.getDiaChi() == null || khachHang.getDiaChi().isEmpty()){
            errors.put("diaChi","Địa chỉ không được để trống!");
        }
        if(khachHang.getDiaChi().length() < 5 || khachHang.getDiaChi().length() > 100){
            errors.put("diaChi", "Địa chỉ phải từ 5 đến 100 ký tự!");
        }
        if(khachHang.getSoDienThoai() == null || khachHang.getSoDienThoai().isEmpty()){
            errors.put("soDienThoai", "Số điện thoại không được để trống!");
        }
        if(!khachHang.getSoDienThoai().matches("^\\d+$")){
            errors.put("soDienThoai","Số điện thoại phải là số");
        }
        if(khachHang.getSoDienThoai().length() != 10 && khachHang.getSoDienThoai().length() != 11){
            errors.put("soDienThoai", "Số điện thoại phải là 10 hoặc 11 số!");
        }
        if(!errors.isEmpty()){
            int pageSizeM = 10;
            Page<SanPhamChiTiet> sanPhamChiTietPage;
            if (keyword != null && !keyword.isEmpty()) {
                sanPhamChiTietPage = sanPhamChiTietService.searchPaginated(keyword, page, pageSizeM);
                model.addAttribute("keyword", keyword);
            } else {
                sanPhamChiTietPage = sanPhamChiTietService.findPaginated(page, pageSizeM);
            }
            if (idDonHang != null) { //Kiểm tra xem có id không
                //Lấy danh sách sản phẩm chi tiết theo tráng
                DonHang donHang = donHangService.getDonHangById(idDonHang);
                if(donHang != null){
                    model.addAttribute("tongTien", donHang.getTongTien());
                    model.addAttribute("tongTienChuaGiam", donHang.getTongTien());
                    //Kiểm tra xem có voucher không và cập nhật thông tin
                    boolean hasVoucher = donHang.getGiamGia() != null;
                    model.addAttribute("hasVoucher", hasVoucher);
                    if (hasVoucher) {
                        Voucher voucher = donHang.getGiamGia(); //Lấy voucher đã áp dụng
                        BigDecimal tongTienGiamGia = donHang.getTongTienGiamGia(); // Tổng tiền sau khi giảm giá
                        model.addAttribute("tongTien", tongTienGiamGia);//Cập nhật tổng tiền hiển thị
                        model.addAttribute("voucher", voucher); //Thêm thông tin voucher vào model
                    }
                    donHang.setDiaChiGiaoHang(khachHang.getDiaChi());
                    donHang.setEmailGiaoHang(khachHang.getEmail());
                    donHang.setTenNguoiNhan(khachHang.getTenKhachHang());
                    donHang.setSoDienThoaiGiaoHang(khachHang.getSoDienThoai());
                    donHangService.capNhatDonHang(donHang);
                    List<Voucher> vouchers = voucherService.getAllVoucherByTrangThaiAndGiaTri(1);
                    model.addAttribute("vouchers", vouchers);
                    List<DonHangChiTiet> dsSanPhamtrongGioHang = donHangChiTietService.getAllDonHangChiTietById(idDonHang);
                    model.addAttribute("gioHangs", dsSanPhamtrongGioHang);
//            model.addAttribute("khachHang", new KhachHang());
                    //Hiển thị một form trống để người dùng tạo hóa đơn mới
                    model.addAttribute("donHang", donHang);
                }

            } else {
                model.addAttribute("gioHangs", null); //Không có id thì không hiển thị giỏ haàng
                model.addAttribute("donHang", null); //Không hiển thị thông tin hóa đơn
                //model.addAttribute("khachHang", null);
            }
            // Hiển thị danh sách hóa đơn hiện có
            model.addAttribute("donHangs", donHangService.getDonHangByTrangThai());
            //Hiển thị danh sách sản phẩm chi tiết hiện có
            model.addAttribute("sanPhamChiTiets", sanPhamChiTietPage);
            model.addAttribute("showModal", true);
            model.addAttribute("errors",errors);
            return "templateadmin/banhangtaiquay";
        }
        if(idDonHang == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Hóa đơn không tồn tại!");
            return "redirect:/don-hang/hien-thi";
        }
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        if(donHang == null){
            redirectAttributes.addFlashAttribute("errorMessage","Bạn chưa chọn hóa đơn!");
            return "redirect:/don-hang/hien-thi";
        }else{
            // Tạo mã khách hàng và thiết lập các thông tin khác
            String maKhachHang = khachHangService.taoMaKhachHang();
            khachHang.setMaKhachHang(maKhachHang);
            khachHang.setGioiTinh(true);
            khachHang.setTrangThai(true);
            khachHang.setMatKhau("12345678");

            // Gán khách hàng cho hóa đơn
            donHang.setKhachHang(khachHang);
            donHangService.capNhatDonHang(donHang);
        }

        return "redirect:/don-hang/chi-tiet?id=" +donHang.getId();
    }
    @PostMapping("/tao")
    public String taoHoaDon( @ModelAttribute DonHang donHang, @RequestParam(value = "idKhachHang", required = false) Integer idKhachHang, RedirectAttributes redirectAttributes, Authentication authentication, Model model) {
        // Kiểm tra số lượng hóa đơn "Đang chờ"
        List<DonHang> hoaDonChoList = donHangService.getDonHangByTrangThai();
        if (hoaDonChoList.size() >= 5) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ tạo nhiều nhất 5 hóa đơn!");
            return "redirect:/don-hang/hien-thi";
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer idNhanVien = userDetails.getId();
        NhanVien nhanVien = nhanVienService.getByIdNhanVien(idNhanVien);
        donHang.setNhanVien(nhanVien);
        LocalDateTime ngayTaoLocalDate = LocalDateTime.now();
        String maDonHang = donHangService.taoMaDonHang();
        String trangThai = "Ðang chờ";
        donHang.setNgayTao(ngayTaoLocalDate);
        donHang.setMaDonHang(maDonHang);
        donHang.setTrangThai(trangThai);
        donHang.setKhachHang(null);
        donHang.setLoaiDonHang("Bán hàng tại quầy");
        donHang.setTongTien(BigDecimal.ZERO);
        // Khách hàng không bắt buộc khi tạo đơn hàng, chỉ gán nếu khachHangId không null
        Boolean themThanhCong = donHangService.themHoaDon(donHang);
        if (themThanhCong) {
            redirectAttributes.addFlashAttribute("message", "Thêm hóa đơn thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Thêm hóa đơn thất bại!");
        }
        redirectAttributes.addFlashAttribute("donHangs", donHangService.getDonHangByTrangThai());

//        donHangService.save(donHang);
        return "redirect:/don-hang/hien-thi";
    }

    @PostMapping("/huy/{maDonHang}")
    public String huyDonHang(@PathVariable String maDonHang, RedirectAttributes redirectAttributes) {
        DonHang donHang = donHangService.getDonHangByMa(maDonHang);
        if (donHang != null && "Ðang chờ".equals(donHang.getTrangThai())) {
            hoanLaiSanPhamVaoKho(donHang);
            donHang.setTrangThai("Đã hủy");
            Boolean capNhatThanhCong = donHangService.capNhatTrangThai(donHang);
            if (capNhatThanhCong) {
                redirectAttributes.addFlashAttribute("message", "Hủy hóa đơn thành công!");

            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Hủy hóa đơn thất bại!");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy hóa đơn. Đơn hàng không tồn tại hoặc không ở trạng thái chờ.");
        }
        return "redirect:/don-hang/hien-thi";
    }

    @PostMapping("/them-san-pham")
    public String themSanPhamVaoHoaDon(@RequestParam("idSanPhamChiTiet") Integer idSanPhamChiTiet, @RequestParam(value = "idDonHang", required = false) Integer idDonHang, @RequestParam("soLuong") int soLuong, RedirectAttributes redirectAttributes, Model model) {
        //Lấy hóa đơn và tính toán tổng tiền mới
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        model.addAttribute("donHang", donHang);
        //Lấy thông tin chi tiết sản phẩm
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.getSanPhamChiTietById(idSanPhamChiTiet);
        int soLuongSPCT = sanPhamChiTiet.getSoLuong();
        //Kiểm tra só lượng sản phẩm trong kho
        if(soLuong <= 0){
            redirectAttributes.addFlashAttribute("errorMessage","Số lượng phải lớn hơn 0!");
            return "redirect:/don-hang/them-san-pham-vao-hoa-don/"+donHang.getId();
        }
        if (soLuong > soLuongSPCT) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số lượng trong kho không đủ!");
            return "redirect:/don-hang/chi-tiet?id="+donHang.getId();
        }

        //Kiểm tra sản phẩm đã tồn tại trong đơn hàng chưa
        DonHangChiTiet sanPhamTonTai = donHangService.kiemTraSanPhamTrongDonHang(idDonHang, idSanPhamChiTiet);
        if (sanPhamTonTai != null) {
            //Nếu đã tồn tại, trả về thông báo lỗi
            int soLuongHienTai = sanPhamTonTai.getSoLuong();
            int tongSoLuongMoi = soLuongHienTai + soLuong;
            //Kiểm tra số lượng mới có vượt quá kho không
            if(tongSoLuongMoi > soLuongSPCT && soLuongSPCT < 0){
                redirectAttributes.addFlashAttribute("errorMessage","Số lượng trong kho không đủ!");
                return "redirect:/don-hang/chi-tiet?id"+donHang.getId();
            }
            sanPhamTonTai.setSoLuong(tongSoLuongMoi);
            BigDecimal giaBan = sanPhamChiTiet.getSanPham().getKhuyenMai() != null ? sanPhamChiTiet.getGiaBanGiamGia() : sanPhamChiTiet.getGiaBan();
            BigDecimal thanhTienMoi = giaBan.multiply(BigDecimal.valueOf(tongSoLuongMoi));
            sanPhamTonTai.setThanhTien(thanhTienMoi);
            donHangChiTietService.updateHoaDonChiTietById(sanPhamTonTai);
            //Cập nhật số lượng sản phẩm trong kho
            sanPhamChiTiet.setSoLuong(soLuongSPCT - soLuong);
            sanPhamChiTietService.capNhatSanPhamChiTiet(sanPhamChiTiet);
            //Cập nhật tổng tiền hóa đơn
            donHang.setTongTien(donHang.getTongTien().add(giaBan.multiply(BigDecimal.valueOf(soLuong))));
            donHangService.capNhatDonHang(donHang);
        }else {
            if (donHang != null) {
                BigDecimal tongTienHoaDonA = donHang.getTongTien();
                BigDecimal donGia = sanPhamChiTiet.getSanPham().getKhuyenMai() != null ? sanPhamChiTiet.getGiaBanGiamGia() : sanPhamChiTiet.getGiaBan();
                BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
                BigDecimal tongTienHoaDonZ = tongTienHoaDonA.add(thanhTien);
                //Cập nhật tổng tiền hóa đơn và số lượng sản phẩm
                donHang.setTongTien(tongTienHoaDonZ);
                donHangService.capNhatDonHang(donHang);

                sanPhamChiTiet.setSoLuong(soLuongSPCT - soLuong);
                sanPhamChiTietService.capNhatSanPhamChiTiet(sanPhamChiTiet);

                //Tạo chi tiết hóa đơn mới
                DonHangChiTiet donHangChiTiet = new DonHangChiTiet();
                donHangChiTiet.setDonHang(donHang);
                donHangChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
                donHangChiTiet.setGia(donGia);
                donHangChiTiet.setSoLuong(soLuong);
                donHangChiTiet.setThanhTien(thanhTien);

                donHangChiTietService.themDonHangChiTiet(donHangChiTiet);
            }
        }
        //Trả về trang danh sách với dữ liệu mới
        return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
    }

    @PostMapping("/xoa-san-pham/{id}")
    public String xoaSanPhamTrongHoaDon(@RequestParam("idDonHang") int idDonHang, @RequestParam("idDonHangChiTiet") Integer idDonHangChiTiet, @RequestParam("idSanPhamChiTiet") Integer idSanPhamChiTiet, @RequestParam("soLuong") int soLuongSP, Model model) {
        //Lấy hóa đơn và cập nhật tổng tiền
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        DonHangChiTiet donHangChiTiet = donHangChiTietService.getDonHangChiTietById(idDonHangChiTiet);
        BigDecimal tongTien = donHang.getTongTien();
        BigDecimal thanhTien = donHangChiTiet.getThanhTien();
        BigDecimal truTienKhiXoa = tongTien.subtract(thanhTien);

        donHang.setTongTien(truTienKhiXoa);
        donHangService.capNhatDonHang(donHang);
        //Cập nhật số lượng sản phẩm chi tiết
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.getSanPhamChiTietById(idSanPhamChiTiet);
        int soLuongSPCT = sanPhamChiTiet.getSoLuong();
        int soLuongSPCTSauKhiXoa = soLuongSPCT + soLuongSP;
        sanPhamChiTiet.setSoLuong(soLuongSPCTSauKhiXoa);

        sanPhamChiTietService.capNhatSanPhamChiTiet(sanPhamChiTiet);

        //Xóa chi tiết hóa đơn
        donHangChiTietService.xoaDonHangChiTiet(idDonHangChiTiet);

        //Load lại giữ liệu hóa đơn
        return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
    }

    @PostMapping("/cap-nhat-so-luong")
    public String capNhatSoLuong(@RequestParam("idDonHang") int idDonHang, @RequestParam("idDonHangChiTiet") Integer idDonHangChiTiet, @RequestParam("idSanPhamChiTiet") Integer idSanPhamChiTiet, @RequestParam(value = "soLuong",required = false) Integer soLuongSP, Model model,RedirectAttributes redirectAttributes){
        // lấy thông tin sản phẩm và hóa đơn
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.getSanPhamChiTietById(idSanPhamChiTiet);
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        DonHangChiTiet donHangChiTiet = donHangChiTietService.getDonHangChiTietById(idDonHangChiTiet);

        int tongSLBanDau = sanPhamChiTiet.getSoLuong() + donHangChiTiet.getSoLuong();

        //Kiểm tra số lượng hợp lệ
        if(soLuongSP <= 0){
            redirectAttributes.addFlashAttribute("errorMessage", "Số lượng phải lớn hơn 0!");
            return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
        }
        if(soLuongSP > tongSLBanDau){
            redirectAttributes.addFlashAttribute("errorMessage", "Số lượng trong kho không đủ!");
            return "redirect:/don-hang/chi-tiet?id=" + idDonHang;
        }
        //Cập nhật thông tin
        int tongSLDaSua = tongSLBanDau - soLuongSP;
        BigDecimal thanhTienDaSua = sanPhamChiTiet.getGiaBan().multiply(BigDecimal.valueOf(soLuongSP));

        // Câập nhật lại thông tin tổng tiền hóa đơn
        BigDecimal tongTienHDBanDau = donHang.getTongTien().subtract(donHangChiTiet.getThanhTien());
        BigDecimal tongTienHDDaSua = tongTienHDBanDau.add(thanhTienDaSua);

        donHang.setTongTien(tongTienHDDaSua);
        donHangService.capNhatDonHang(donHang);

        // Cập nhật số lượng sản phẩm trong kho
        sanPhamChiTiet.setSoLuong((int) tongSLDaSua);
        sanPhamChiTietService.capNhatSanPhamChiTiet(sanPhamChiTiet);

        // Cập nhật thông tin chi tiết hóa đơn
        donHangChiTiet.setSoLuong(soLuongSP);
        donHangChiTiet.setThanhTien(thanhTienDaSua);
        donHangChiTietService.updateHoaDonChiTietById(donHangChiTiet);

        //Cập nhật dữ liệu model cho Thymeleaf

        model.addAttribute("gioHangs", donHangChiTietService.getAllDonHangChiTietById(idDonHang));
        model.addAttribute("donHangId", donHangService.getDonHangById(idDonHang));
        model.addAttribute("tongTien", donHang.getTongTien());
        List<Voucher> vouchers = voucherService.getAllVoucherByTrangThaiAndGiaTri(1);
        List<Voucher> filteredVouchers = vouchers.stream()
                .filter(voucher -> voucher.getDonToiThieu() != null
                        && voucher.getDonToiThieu().compareTo(donHang.getTongTien()) <= 0)
                .collect(Collectors.toList());

        model.addAttribute("vouchers", filteredVouchers);
        //Hiển thị một form trống để người dùng tạo hóa đơn mới
        model.addAttribute("donHang", new DonHang());
        // Hiển thị danh sách hóa đơn hiện có
        model.addAttribute("donHangs",donHangService.getDonHangByTrangThai());
        //Hiển thị danh sách sản phẩm chi tiết hiện có
        model.addAttribute("sanPhamChiTiets",sanPhamChiTietService.getSanPhamChiTiet());
        redirectAttributes.addFlashAttribute("message","Cập nhật sản phẩm thành công!");
        return "redirect:/don-hang/chi-tiet?id=" +donHang.getId();
    }
//    @PostMapping("/update-san-pham-quantity")
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> updateSanPhamQuantity(@RequestParam Integer idDonHang, @RequestParam Integer idSanPhamChiTiet, @RequestParam Integer soLuong, RedirectAttributes redirectAttributes) {
//        try {
//            // Lấy thông tin sản phẩm và hóa đơn
//            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.getSanPhamChiTietById(idSanPhamChiTiet);
//            DonHang donHang = donHangService.getDonHangById(idDonHang);
//
//
//
//            // Lấy chi tiết hóa đơn dựa trên id đơn hàng và sản phẩm
//            DonHangChiTiet donHangChiTiet = donHangChiTietService.getDonHangIdAndSanPhamChiTietId(idDonHang, idSanPhamChiTiet);
//
//            // Tính toán tổng số lượng và tổng tiền
//            BigDecimal giaBan = sanPhamChiTiet.getGiaBan();
//            BigDecimal soLuongBigDecimal = BigDecimal.valueOf(soLuong);
//            BigDecimal thanhTienDaSua = giaBan.multiply(soLuongBigDecimal);
//
//            // Cập nhật lại tổng tiền hóa đơn
//            BigDecimal tongTienHDBanDau = donHang.getTongTien().subtract(donHangChiTiet.getThanhTien());
//            BigDecimal tongTienHDDaSua = tongTienHDBanDau.add(thanhTienDaSua);
//
//            // Cập nhật tổng tiền cho hóa đơn
//            donHang.setTongTien(tongTienHDDaSua);
//            donHangService.capNhatDonHang(donHang);
//
//            // Cập nhật số lượng sản phẩm trong kho
//            double tongSLDaSua = sanPhamChiTiet.getSoLuong() + donHangChiTiet.getSoLuong() - soLuong;
//            sanPhamChiTiet.setSoLuong((int) tongSLDaSua);
//            sanPhamChiTietService.capNhatSanPhamChiTiet(sanPhamChiTiet);
//
//            // Cập nhật thông tin chi tiết hóa đơn
//            donHangChiTiet.setSoLuong(soLuong);
//            donHangChiTiet.setThanhTien(thanhTienDaSua);
//            donHangChiTietService.updateHoaDonChiTietById(donHangChiTiet);
//
//            //Lấy lại danh sách voucher hợp lệ
//            List<Voucher> vouchers = voucherService.getAllVoucherByTrangThaiAndGiaTri(1);
//            List<Voucher> filteredVouchers = vouchers.stream()
//                    .filter(voucher -> voucher.getDonToiThieu().compareTo(donHang.getTongTien()) <= 0)
//                    .collect(Collectors.toList());
//            Map<String, Object> response = new HashMap<>();
//            response.put("message","Cập nhật số lượng thành công");
//            response.put("tongTienMoi", tongTienHDDaSua);
//            response.put("vouchers",filteredVouchers);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            e.printStackTrace(); // In ra lỗi nếu có
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Lỗi khi cập nhật số lượng"));
//        }
//    }

    @PostMapping("/thanh-toan")
    public String thanhToanHoaDon(@RequestParam(value="idDonHang",required = false) Integer idDonHang,
                                  @RequestParam("tienKhachTra") String tienKhachTraStr,
                                  @RequestParam(value = "idKH", required = false) Integer idKH,
                                  @RequestParam(value = "idVoucher", required = false) Integer idVoucher,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        List<DonHang> donHangs = donHangService.getDonHangByTrangThai();
        if(donHangs == null || donHangs.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage","Không có hóa đơn!");
            return "redirect:/don-hang/hien-thi";
        }
        if(idDonHang == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Không có sản phẩm nào trong hóa đơn!");
            return "redirect:/don-hang/hien-thi";
        }
        //Lấy hóa đơn từ Id
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        List<DonHangChiTiet> gioHangs = donHangChiTietService.getAllDonHangChiTietById(idDonHang);

        if (donHang == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hóa đơn không tồn tại");
            return "redirect:/don-hang/hien-thi";
        }

        //Áp dụng voucher nếu có
        if (idVoucher != null) {
            Voucher voucher = voucherService.getVoucherById(idVoucher);
            donHang.setGiamGia(voucher); //Áp dụng voucher cho hóa đơn
            BigDecimal tongTienGiamGia = donHang.getTongTienGiamGia();// Tông tiền sau khi giảm
            model.addAttribute("tongTien", tongTienGiamGia);
        }
        BigDecimal  tongTien = donHang.getTongTien();
//        if(idVoucher != null){
//            Voucher voucher = voucherService.getVoucherById(idVoucher);
//            donHang.setGiamGia(voucher);
//        }
        //Xử lý nhập tiền khách trả và tính tiền dư
        try {
            BigDecimal tienKhachTra = new BigDecimal(tienKhachTraStr);
            BigDecimal tienDu;
            if(idVoucher != null){
                BigDecimal tongTienGiamGia = donHang.getTongTienGiamGia();
                tienDu = tienKhachTra.subtract(tongTienGiamGia);//Tính tiền dư dựa trên tổng tiền đã giảm
            }else{
                tienDu = tienKhachTra.subtract(tongTien);
            }

            if (tienDu.compareTo(BigDecimal.ZERO) < 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Số tiền khách trả không đủ.");
                return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
            }
            //Gửi thông tin tính toán sang giao diện
            model.addAttribute("tienDu", tienDu);
            model.addAttribute("tienKhachTra", tienKhachTra);
            model.addAttribute("donHangTT", donHang);
        } catch (NumberFormatException e) {
            //Xứ lý nếu người dùng nhập không phải là số
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập số nguyên cho tiền khách trả!");
            return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
        }
        //Cập nhật trạng thái hóa đơn
        donHang.setTrangThai("Đã thanh toán");
        donHang.setNgayMua(LocalDate.now());
        //Liên kết khách hàng với hóa đơn nếu khách hàng đã được chọn
        if (idKH != null) {
            KhachHang khachHang = khachHangService.getKhachHangById(idKH);
            if (khachHang == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng");
                return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
            }
            donHang.setKhachHang(khachHang);

            //Gửi email hóa đơn cho khách hàng sau khi thanh toán thành công
            String emailKhachHang = khachHang.getEmail();
            String subject = "Hóa đơn thanh toán";
            String text = buildInvoiceEmailContent(donHang); //Hàm này xây dựng nội dung email
            if(emailKhachHang != null){
                try {
                    emailService.sendInvoiceEmail(emailKhachHang, subject, text);
                } catch (MessagingException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không thẻ gửi email hóa đơn!");
                    return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
                }
            }
        }
        //Cập nhật hóa đơn vào cơ sở dữ liệu
        donHangService.capNhatDonHang(donHang);
        redirectAttributes.addFlashAttribute("message","Thanh toán hóa đơn thành công!");
        //Cập nhật lại dữ liệu hiển thị
        model.addAttribute("donHangs", donHangService.getDonHangByTrangThai());
        return "redirect:/don-hang/hien-thi"; // Trả về trang thanh toán
    }

    @GetMapping("/them-san-pham-vao-hoa-don/{id}")
    public String testBanHang(@PathVariable Integer id,@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "keyword", required = false) String keyword, Model model) {
        int pageSize = 10;
        Page<SanPhamChiTiet> sanPhamChiTietPage;
        if (keyword != null && !keyword.isEmpty()) {
            sanPhamChiTietPage = sanPhamChiTietService.searchPaginated(keyword, page, pageSize);
            model.addAttribute("keyword", keyword);
        } else {
            sanPhamChiTietPage = sanPhamChiTietService.findPaginated(page, pageSize);
        }
        DonHang donHang = donHangService.getDonHangById(id);
        //Hiển thị một form trống để người dùng tạo hóa đơn mới
        model.addAttribute("donHang", donHang);
        // Hiển thị danh sách hóa đơn hiện có
        model.addAttribute("donHangs", donHangService.getDonHangByTrangThai());
        //Hiển thị danh sách sản phẩm chi tiết hiện có
        model.addAttribute("sanPhamChiTiets", sanPhamChiTietPage);
        model.addAttribute("id", id);
        return "templateadmin/them-san-pham-vao-hoa-don";
    }
    //    @GetMapping("/tim-kiem-san-pham")
//    public String timKiemSanPham(@RequestParam(value = "page", defaultValue = "0") int page,
//                                 @RequestParam(value = "keyword", required = false) String keyword, Model model){
//        int pageSize = 10;
//        Page<SanPhamChiTiet> sanPhamChiTietPage;
//        if (keyword != null && !keyword.isEmpty()) {
//            sanPhamChiTietPage = sanPhamChiTietService.searchPaginated(keyword, page, pageSize);
//            model.addAttribute("keyword", keyword);
//        } else {
//            sanPhamChiTietPage = sanPhamChiTietService.findPaginated(page, pageSize);
//        }
//        DonHang donHang = donHangService.getDonHangById();
//        //Hiển thị một form trống để người dùng tạo hóa đơn mới
//        model.addAttribute("donHang", donHang);
//        // Hiển thị danh sách hóa đơn hiện có
//        model.addAttribute("donHangs", donHangService.getDonHangByTrangThai());
//        //Hiển thị danh sách sản phẩm chi tiết hiện có
//        model.addAttribute("sanPhamChiTiets", sanPhamChiTietPage);
//        return "templateadmin/them-san-pham-vao-hoa-don";
//    }
    @PostMapping("/ap-voucher")
    public String apVoucher(@RequestParam("idVoucher") Integer idVoucher, @RequestParam(value = "idDonHang", required = false) Integer idDonHang, Model model, RedirectAttributes redirectAttributes) {
        if (idDonHang == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chưa chọn hóa đơn!");
            return "redirect:/don-hang/hien-thi";
        }
        //Lấy thông tin hóa đơn
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        if (donHang == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hóa đơn không tồn tài!");
            return "redirect:/don-hang/hien-thi";
        }
        if(idVoucher == null || idVoucher == 0){
            redirectAttributes.addFlashAttribute("errorMessage", "Chưa chọn voucher!");
            return "redirect:/don-hang/chi-tiet?id="+ donHang.getId();
        }
        //Kiểm tra và lấy voucher
        Voucher voucher = voucherService.getByIdVoucher(idVoucher);
        if (voucher == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã voucher không tồn tại!");
            return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
        }
        BigDecimal tongTienHoaDon = donHang.getTongTien();
        BigDecimal donToiThieu = voucher.getDonToiThieu();
        if(tongTienHoaDon.compareTo(donToiThieu) < 0){
            redirectAttributes.addFlashAttribute("errorMessage", "Mã voucher dành cho hóa đơn có tổng tiền phải lớn hơn " + voucher.getDonToiThieu());
            return "redirect:/don-hang/chi-tiet?id="+donHang.getId();
        }
        Date now = new Date();
        //Kiểm tra ngày hết hạn
        if (voucher.getNgayKetThuc().before(now)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã voucher đã hết hạn!");
            return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
        }
        //Áp dụng voucher và tính số tiền sau khi giảm
        BigDecimal giamGia = voucherService.applyVoucher(donHang, voucher);
        BigDecimal tongTienGiamGia = giamGia;
        int slVoucher = voucher.getSoLuong();
        //Cập nhật hóa đơn
        donHang.setGiamGia(voucher);
        voucher.setSoLuong(slVoucher - 1);
        voucherService.themVoucher(voucher);
        donHang.setTongTienGiamGia(tongTienGiamGia);
        donHangService.capNhatDonHang(donHang);
        redirectAttributes.addFlashAttribute("message", "Voucher đã được áp dụng thành công!");
        return "redirect:/don-hang/chi-tiet?id=" + donHang.getId();
    }
    @PostMapping("/huy-voucher/{idDonHang}")
    public String huyVoucher(@PathVariable Integer idDonHang,@RequestParam("idVoucher")Integer idVoucher ,RedirectAttributes redirectAttributes){
        DonHang donHang = donHangService.getDonHangById(idDonHang);
        if(donHang.getGiamGia() != null){

            //Lưu lại tổng tiền trước khi hủy voucher
            //double phanTramGiam = donHang.getGiamGia().getGiaTri(); // Giả sử bạn lưu tỉ lệ giảm trong voucher
            //double tongTienBanDau = donHang.getTongTien(); // Tổng tiền sau khi áp voucher
            //double tongTienKhongGiam = tongTienBanDau / (1 - phanTramGiam); // Tính tổng tiền ban đầu

            // Xóa voucher
            donHang.setGiamGia(null);
            donHang.setTongTienGiamGia(null);
            donHang.setTongTien(donHang.getTongTien()); // Reset tổng tiền về giá trị ban đầu
            Voucher voucher = voucherService.getVoucherById(idVoucher);
            int slVoucher = voucher.getSoLuong();
            voucher.setSoLuong(slVoucher + 1);
            voucherService.themVoucher(voucher);
            donHangService.capNhatDonHang(donHang); // Cập nhật hóa đơn

            redirectAttributes.addFlashAttribute("message", "Hủy voucher thành công!");
        }else{
            redirectAttributes.addFlashAttribute("errorMessage", "Hủy voucher thất bại!");
        }
        return "redirect:/don-hang/chi-tiet?id="+ donHang.getId(); // Chuyển hướng về trang hiển thị đơn hàng
    }
//    @GetMapping("/tim-khach-hang")
//    public String timKhachHang(@RequestParam("idDonHang")int idDonHang,@RequestParam(value ="sdtKH", required = false, defaultValue = "")String sdtKH,Model model){
//        DonHang donHang = donHangService.getDonHangById(idDonHang);
////        KhachHang khachHang = khachHangService.getKhachHangBySoDienThoai(sdtKH);
//        if (sdtKH.isEmpty()) {
//            model.addAttribute("khachHang", null);
//        } else {
//            KhachHang khachHang = khachHangService.getKhachHangBySoDienThoai(sdtKH);
//            if(khachHang != null) {
//                model.addAttribute("khachHang", khachHang);
//            } else {
//                model.addAttribute("khachHang", null);
//            }
//        }
//       return "templateadmin/list";
//    }

    //    @PostMapping("/them-moi")
//    public String themMoiKhachHang(@ModelAttribute("khachHang") KhachHang khachHang){
//        khachHangService.themKH(khachHang);
//        return "redirect:/don-hang/chi-tiet";
//    }
    private String buildInvoiceEmailContent(DonHang donHang) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Hóa đơn thanh toán</h1>");
        sb.append("<p>Cảm ơn quý khách đã mua hàng!</p>");
        sb.append("<p>Mã đơn hàng: ").append(donHang.getId()).append("</p>");
        sb.append("<p>Ngày mua: ").append(donHang.getNgayMua()).append("</p>");
        sb.append("<p>Tổng tiền: ").append(donHang.getTongTien()).append(" VND</p>");
        sb.append("<p>Trạng thái: ").append(donHang.getTrangThai()).append("</p>");
        return sb.toString();
    }

    private void hoanLaiSanPhamVaoKho(DonHang donHang) {
        List<DonHangChiTiet> donHangChiTiets = donHangChiTietService.getByDonHang(donHang);
        for (DonHangChiTiet chiTiet : donHangChiTiets) {
            SanPhamChiTiet sanPhamChiTiet = chiTiet.getSanPhamChiTiet();
            int soLuong = chiTiet.getSoLuong();
            sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() + soLuong); //Cộng lại số lượng vào kho
            sanPhamChiTietService.capNhatSanPhamChiTiet(sanPhamChiTiet);
        }
    }
}