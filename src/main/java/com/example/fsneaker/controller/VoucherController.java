package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.NhanVien;
import com.example.fsneaker.entity.Voucher;
import com.example.fsneaker.repositories.NhanVienRepo;
import com.example.fsneaker.repositories.VoucherRepo;
import com.example.fsneaker.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
//@EnableScheduling
@Component
@Controller

public class VoucherController {
    @Autowired
    VoucherRepo voucherRepo;
    @Autowired
    NhanVienRepo nhanVienRepo;
    @Autowired
    VoucherService voucherService;


    @GetMapping("/qlvoucher")
    public String Voucher(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int pageNo,
            @RequestParam(name = "limit", defaultValue = "5") int pageSize,
            @RequestParam(value="keyword",required = false) String keyword,
            @RequestParam(value="ngayBatDau", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate ngayBatDau,
            @RequestParam(value = "ngayKetThuc", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate ngayKetThuc,
            @RequestParam(value="trangThai",required = false)Integer trangThai
    ) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Voucher> list = voucherService.searchVoucher(keyword, ngayBatDau, ngayKetThuc, trangThai, pageRequest);
        model.addAttribute("keyword", keyword);
        model.addAttribute("ngayBatDau",ngayBatDau);
        model.addAttribute("ngayKetThuc", ngayKetThuc);
        model.addAttribute("trangThai",trangThai);
        model.addAttribute("data", list);
        return "templateadmin/qlvoucher";
    }

    @GetMapping("/qlvoucher/create")
    public String create(Model model) {
        List<NhanVien> listNV = this.nhanVienRepo.findAll();
        model.addAttribute("dataNV", listNV);
        return "templateadmin/add-voucher";
    }
    @PostMapping("/qlvoucher/add")
    public String add(
            @Valid @ModelAttribute Voucher voucher,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            List<FieldError> listErrors = result.getFieldErrors();
            Map<String, String> errors = new HashMap<>();

            for (FieldError fe : listErrors) {
                errors.put(fe.getField(), fe.getDefaultMessage());
            }
            List<NhanVien> listNV = this.nhanVienRepo.findAll();
            model.addAttribute("dataNV", listNV);
            model.addAttribute("errors", errors);
            model.addAttribute("data", voucher);
            return "templateadmin/add-voucher";
        }
        Voucher existingVoucher = voucherRepo.findByMaVoucher(voucher.getMaVoucher());
        if(existingVoucher != null){
            //Nếu mã voucher đã tồn tại, thông báo lỗi
            model.addAttribute("data",voucher);
            model.addAttribute("errorMaVoucher", "Mã voucher đã tồn tại!");
            return "templateadmin/add-voucher";
        }
        if(voucher.getSoLuong() <= 0){
            model.addAttribute("errorSoLuong", "Số lượng phải lớn hơn 0!");
            return "templateadmin/add-voucher";
        }
        //Kiểm tra giá trị khuyến mãi
        if(voucher.getLoaiVoucher().equalsIgnoreCase("Giảm giá %")){
            //Kiểm tra giá trị phần trăm không được vượt quá 100%
            if(voucher.getGiaTri().compareTo(BigDecimal.ONE) < 0 || voucher.getGiaTri().compareTo(new BigDecimal("100")) > 0) {
                model.addAttribute("data", voucher);
                model.addAttribute("errorGiaTri", "Giá trị phần trăm phải từ 1 đến 100%!");
                return "templateadmin/add-voucher";
            }
            //Kiểm trá nếu đơn tối thiểu ít hơn giá trị giảm theo %
            if(voucher.getDonToiThieu().compareTo(voucher.getGiaTri()) <= 0){
                model.addAttribute("data", voucher);
                model.addAttribute("errorDonToiThieu", "Đơn tối thiểu phải lớn hơn giá trị giảm!");
                return "templateadmin/add-voucher";
            }
        }else if(voucher.getLoaiVoucher().equalsIgnoreCase("Giảm giá số tiền")){
            //Kiểm tra giá trị tiền phải lớn hơn 0
            if(voucher.getGiaTri().compareTo(BigDecimal.ZERO) <= 0){
                model.addAttribute("data", "voucher");
                model.addAttribute("errorGiaTri","Giá trị giảm lớn hơn 0!");
                return "templateadmin/add-voucher";
            }
            //Kiểm tra giá trị tiền phải nhỏ hơn hoặc bằng đơn tối thiểu
            if(voucher.getGiaTri().compareTo(voucher.getDonToiThieu()) > 0){
                model.addAttribute("data", voucher);
                model.addAttribute("errorGiaTri", "Giá trị giảm phải nhỏ hơn hoặc bằng đơn tối thiểu!");
                return "templateadmin/add-voucher";
            }
        }
        //Kiểm tra logic: ngày bắt đầu phải trước ngày kết thúc
        if(voucher.getNgayBatDau().after(voucher.getNgayKetThuc())){
            model.addAttribute("data",voucher);
            model.addAttribute("errorNgay", "Ngày bắt đầu phải trước ngày kết thúc!");
            return "templateadmin/add-voucher";
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        voucherService.themVc(voucher, tenNguoiDung);
        redirectAttributes.addFlashAttribute("message", "Thêm voucher thành công!");
        return "redirect:/qlvoucher";
    }
    @GetMapping("/qlvoucher-edit/{id}")
    public String edit(
            @PathVariable("id") Integer id,
            Model model) {
        Voucher voucher = voucherService.getVoucherById(id);
        List<NhanVien> listNV = this.nhanVienRepo.findAll();
        model.addAttribute("dataNV", listNV);

        model.addAttribute("data", voucher);
        return "templateadmin/edit-voucher";
    }

    @PostMapping("/qlvoucher-update/{id}")
    public String update(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute Voucher voucher,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            List<FieldError> listErrors = result.getFieldErrors();
            Map<String, String> errors = new HashMap<>();

            for (FieldError fe : listErrors) {
                errors.put(fe.getField(), fe.getDefaultMessage());
            }
            List<NhanVien> listNV = this.nhanVienRepo.findAll();
            model.addAttribute("dataNV", listNV);
            model.addAttribute("errors", errors);
            model.addAttribute("data", voucher);
            return "templateadmin/edit-voucher";
        }
        // Lấy đối tượng Voucher hiện tại từ cơ sở dữ liệu
        Voucher existingVoucher = voucherService.getVoucherById(id);
        if (existingVoucher == null) {
            redirectAttributes.addFlashAttribute("error", "Voucher không tồn tại!");
            return "redirect:/qlvoucher";
        }
        //Kiểm tra giá trị khuyến mãi
        if(voucher.getLoaiVoucher().equalsIgnoreCase("Giảm giá %")){
            //Kiểm tra giá trị phần trăm không được vượt quá 100%
            if(voucher.getGiaTri().compareTo(BigDecimal.ONE) < 0 || voucher.getGiaTri().compareTo(new BigDecimal("100")) > 0) {
                model.addAttribute("data", voucher);
                model.addAttribute("errorGiaTri", "Giá trị phần trăm phải từ 1 đến 100%!");
                return "templateadmin/edit-voucher";
            }
            //Kiểm trá nếu đơn tối thiểu ít hơn giá trị giảm theo %
            if(voucher.getDonToiThieu().compareTo(voucher.getGiaTri()) <= 0){
                model.addAttribute("data", voucher);
                model.addAttribute("errorDonToiThieu", "Đơn tối thiểu phải lớn hơn giá trị giảm!");
                return "templateadmin/edit-voucher";
            }
        }else if(voucher.getLoaiVoucher().equalsIgnoreCase("Giảm giá số tiền")){
            //Kiểm tra giá trị tiền phải lớn hơn 0
            if(voucher.getGiaTri().compareTo(BigDecimal.ZERO) <= 0){
                model.addAttribute("data", voucher);
                model.addAttribute("errorGiaTri","Giá trị giảm lớn hơn 0!");
                return "templateadmin/edit-voucher";
            }
            //Kiểm tra giá trị tiền phải nhỏ hơn hoặc bằng đơn tối thiểu
            if(voucher.getGiaTri().compareTo(voucher.getDonToiThieu()) > 0){
                model.addAttribute("data", voucher);
                model.addAttribute("errorGiaTri", "Giá trị giảm phải nhỏ hơn hoặc bằng đơn tối thiểu!");
                return "templateadmin/edit-voucher";
            }
        }
        //Kiểm tra logic: ngày bắt đầu phải trước ngày kết thúc
        if(voucher.getNgayBatDau().after(voucher.getNgayKetThuc())){
            model.addAttribute("data",voucher);
            model.addAttribute("errorNgay", "Ngày bắt đầu phải trước ngày kết thúc!");
            return "templateadmin/edit-voucher";
        }
        voucher.setNgayTao(existingVoucher.getNgayTao());
        voucher.setNguoiTao(existingVoucher.getNguoiTao());
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        voucherService.themVc(voucher,tenNguoiDung);
        redirectAttributes.addFlashAttribute("message", "Sửa voucher thành công!");
        return "redirect:/qlvoucher";
    }
//    @PostMapping("/saveVoucher")
//    public String saveVoucher(@ModelAttribute("voucher") Voucher voucher) {
//        // Cập nhật trạng thái trước khi lưu
//        voucher.setTrangThai(voucher.getTrangThai());
//
//        voucherRepo.save(voucher);
//        return "redirect:/qlvoucher";
//    }

//    @Scheduled(cron = "0 0 0 * * ?")
//    public void updateVoucherStatus() {
//        LocalDateTime today = LocalDateTime.now();
//
//        List<Voucher> upcomingVouchers = voucherRepo.findByNgayBatDauAfter(today);
//        for (Voucher voucher : upcomingVouchers) {
//            voucher.setTrangThai(2); // Sắp diễn ra
//            voucherRepo.save(voucher);
//        }
//
//        List<Voucher> ongoingVouchers = voucherRepo.findByNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(today, today);
//        for (Voucher voucher : ongoingVouchers) {
//            voucher.setTrangThai(1); // Đang diễn ra
//            voucherRepo.save(voucher);
//        }
//
//        List<Voucher> expiredVouchers = voucherRepo.findByNgayKetThucBefore(today);
//        for (Voucher voucher : expiredVouchers) {
//            voucher.setTrangThai(0); // Ngừng hoạt động
//            voucherRepo.save(voucher);
//        }
//
//        System.out.println("Cập nhật trạng thái voucher thành công!");
//    }

}
