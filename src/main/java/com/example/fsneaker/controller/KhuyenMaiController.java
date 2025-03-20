package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.KhuyenMai;
import com.example.fsneaker.repositories.KhuyenMaiRepo;
import com.example.fsneaker.service.KhuyenMaiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class KhuyenMaiController {
    @Autowired
    public KhuyenMaiRepo khuyenMaiRepository;

    @Autowired
    public KhuyenMaiService khuyenMaiService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");


    @GetMapping("create")
    public String create(Model model){
        model.addAttribute("khuyenMai", new KhuyenMai());
        return "templateadmin/them-khuyen-mai";
    }

    @GetMapping("/qlkhuyenmai")
    public String index( Model model,@RequestParam(defaultValue = "0") int page) {

        int pageSize = 5; // Số lượng bản ghi mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize); // Sắp xếp theo id giảm dần

        Page<KhuyenMai> khuyenMaiPage = khuyenMaiRepository.findAllWithSorting(pageable);



        // Thêm dữ liệu phân trang vào model
        model.addAttribute("khuyenMais", khuyenMaiPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", khuyenMaiPage.getTotalPages());
        return "templateadmin/qlkhuyenmai";
    }

    @PostMapping("add")
    public String add( Model model, @Valid KhuyenMai khuyenMai , BindingResult validate, Authentication authentication, RedirectAttributes redirectAttributes){
        if(validate.hasErrors()){
            //lỗi
            Map<String, String> errors = new HashMap<>();
            for (FieldError e: validate.getFieldErrors()) {
                errors.put(e.getField(), e.getDefaultMessage());
            }

            model.addAttribute("khuyenMai",khuyenMai);
            model.addAttribute("errors",errors);
            return "templateadmin/them-khuyen-mai";
        }


        // kiểm tra điều kiệu của mã khi thêm mới
        KhuyenMai existingKhuyenMai = khuyenMaiRepository.findByMaKhuyenMai(khuyenMai.getMaKhuyenMai());
        if (existingKhuyenMai != null) {
            // Nếu mã khuyến mãi đã tồn tại, thông báo lỗi
            model.addAttribute("khuyenMai", khuyenMai);
            model.addAttribute("errorMaKhuyenMai", "Mã khuyến mãi đã tồn tại, vui lòng chọn mã khác!");
            return "templateadmin/them-khuyen-mai";
        }



        // Kiểm tra giá trị khuyến mãi
        if (khuyenMai.getLoaiKhuyenMai().equalsIgnoreCase("Giảm giá %")) {
            // Kiểm tra giá trị phần trăm không được vượt quá 100%
            if (khuyenMai.getGiaTri().compareTo(BigDecimal.ONE) < 0 || khuyenMai.getGiaTri().compareTo(new BigDecimal("100")) > 0) {
                model.addAttribute("khuyenMai", khuyenMai);
                model.addAttribute("errorGiaTri", "Giá trị phần trăm phải từ 1 đến 100%.");
                return "templateadmin/them-khuyen-mai"; // Trả về form với thông báo lỗi
            }

            //  Kiểm tra nếu đơn tối thiểu ít hơn giá trị giảm theo %
            if (khuyenMai.getDonToiThieu().compareTo(khuyenMai.getGiaTri()) <= 0) {
                model.addAttribute("khuyenMai", khuyenMai);
                model.addAttribute("errorDonToiThieu", "Đơn tối thiểu phải lớn hơn giá trị giảm.");
                return "templateadmin/them-khuyen-mai";
            }

        } else if (khuyenMai.getLoaiKhuyenMai().equalsIgnoreCase("Giảm giá số tiền")) {
            // Kiểm tra giá trị tiền phải lớn hơn 0
            if (khuyenMai.getGiaTri().compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("khuyenMai", khuyenMai);
                model.addAttribute("errorGiaTri", "Giá trị giảm phải lớn hơn 0.");
                return "templateadmin/them-khuyen-mai"; // Trả về form với thông báo lỗi
            }

            // Kiểm tra giá trị tiền phải nhỏ hơn hoặc bằng đơn tối thiểu
            if (khuyenMai.getGiaTri().compareTo(khuyenMai.getDonToiThieu()) > 0) {
                model.addAttribute("khuyenMai", khuyenMai);
                model.addAttribute("errorGiaTri", "Giá trị giảm phải nhỏ hơn hoặc bằng đơn tối thiểu.");
                return "templateadmin/them-khuyen-mai"; // Trả về form với thông báo lỗi
            }
        }


        // Kiểm tra logic: ngày bắt đầu phải trước ngày kết thúc
        if (khuyenMai.getNgayBatDau().after(khuyenMai.getNgayKetThuc())) {
            model.addAttribute("khuyenMai", khuyenMai);
            model.addAttribute("errorNgay", "Ngày bắt đầu không được sau ngày kết thúc!");
            return "templateadmin/them-khuyen-mai";
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        khuyenMaiService.themKM(khuyenMai, tenNguoiDung);
        redirectAttributes.addFlashAttribute("message","Thêm khuyến mãi thành công!");
        return "redirect:/qlkhuyenmai";
    }




    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
        KhuyenMai khuyenMai = this.khuyenMaiRepository.findById(id).get();
        model.addAttribute("khuyenMai", khuyenMai);
        return "templateadmin/sua-khuyen-mai";
    }

    @PostMapping("/update/{id}")
    public String updateKhuyenMai(@PathVariable Integer id, @Valid @ModelAttribute KhuyenMai km, BindingResult validate, Authentication authentication, RedirectAttributes redirectAttributes, Model model) {
        // Tìm kiếm khuyến mãi hiện tại theo ID
        KhuyenMai existingKhuyenMai = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khuyến mãi không tồn tại"));

        // Kiểm tra lỗi từ validate
        if (validate.hasErrors()) {
            // Lưu lại thông tin khuyến mãi cũ để hiển thị lại trong form
            model.addAttribute("khuyenMai", km);
            // Chuyển đổi lỗi thành map để hiển thị
            Map<String, String> errors = new HashMap<>();
            for (FieldError e : validate.getFieldErrors()) {
                errors.put(e.getField(), e.getDefaultMessage());
            }
            model.addAttribute("errors", errors);
            return "templateadmin/sua-khuyen-mai"; // Trả về form với thông báo lỗi
        }



        // Kiểm tra điều kiện của mã khi cập nhật
        if (!existingKhuyenMai.getMaKhuyenMai().equals(km.getMaKhuyenMai())) {
            KhuyenMai existingByMa = khuyenMaiRepository.findByMaKhuyenMai(km.getMaKhuyenMai());
            if (existingByMa != null) {
                model.addAttribute("khuyenMai", km);
                model.addAttribute("errorMaKhuyenMai", "Mã khuyến mãi đã tồn tại, vui lòng chọn mã khác!");
                return "templateadmin/sua-khuyen-mai"; // Trả về form với thông báo lỗi
            }
        }

        // Kiểm tra giá trị khuyến mãi
        if (km.getLoaiKhuyenMai().equalsIgnoreCase("Giảm giá %")) {
            // Kiểm tra giá trị phần trăm không được vượt quá 100%
            if (km.getGiaTri().compareTo(BigDecimal.ONE) < 0 || km.getGiaTri().compareTo(new BigDecimal("100")) > 0) {
                model.addAttribute("khuyenMai", km);
                model.addAttribute("errorGiaTri", "Giá trị phần trăm phải từ 1 đến 100%.");
                return "templateadmin/sua-khuyen-mai"; // Trả về form với thông báo lỗi
            }

// Kiểm tra nếu đơn tối thiểu ít hơn giá trị giảm theo %
            if (km.getDonToiThieu().compareTo(km.getGiaTri()) <= 0) {
                model.addAttribute("khuyenMai", km);
                model.addAttribute("errorDonToiThieu", "Đơn tối thiểu phải lớn hơn giá trị giảm.");
                return "templateadmin/sua-khuyen-mai";
            }

        } else if (km.getLoaiKhuyenMai().equalsIgnoreCase("Giảm giá số tiền")) {
            // Kiểm tra giá trị tiền phải lớn hơn 0
            if (km.getGiaTri().compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("khuyenMai", km);
                model.addAttribute("errorGiaTri", "Giá trị giảm phải lớn hơn 0.");
                return "templateadmin/sua-khuyen-mai"; // Trả về form với thông báo lỗi
            }

            // Kiểm tra giá trị tiền phải nhỏ hơn hoặc bằng đơn tối thiểu
            if (km.getGiaTri().compareTo(km.getDonToiThieu()) > 0) {
                model.addAttribute("khuyenMai", km);
                model.addAttribute("errorGiaTri", "Giá trị giảm phải nhỏ hơn hoặc bằng đơn tối thiểu.");
                return "templateadmin/sua-khuyen-mai"; // Trả về form với thông báo lỗi
            }
        }

        // Kiểm tra logic: ngày bắt đầu không được trùng với ngày kết thúc
        if (km.getNgayBatDau().after(km.getNgayKetThuc())) {
            model.addAttribute("khuyenMai", km);
            model.addAttribute("errorNgay", "Ngày bắt đầu và ngày kết thúc không được trùng nhau!");
            return "templateadmin/sua-khuyen-mai"; // Trả về form với thông báo lỗi
        }

        // Cập nhật thông tin cho khuyến mãi hiện tại
        existingKhuyenMai.setMaKhuyenMai(km.getMaKhuyenMai());
        existingKhuyenMai.setTenKhuyenMai(km.getTenKhuyenMai());
        existingKhuyenMai.setLoaiKhuyenMai(km.getLoaiKhuyenMai());
        existingKhuyenMai.setMoTa(km.getMoTa());
        existingKhuyenMai.setGiaTri(km.getGiaTri());
        existingKhuyenMai.setDonToiThieu(km.getDonToiThieu()); // Cập nhật đơn tối thiểu
        existingKhuyenMai.setNgayBatDau(km.getNgayBatDau());
        existingKhuyenMai.setNgayKetThuc(km.getNgayKetThuc());
        existingKhuyenMai.setTrangThai(km.getTrangThai());

        // Lưu khuyến mãi đã cập nhật
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        khuyenMaiService.themKM(existingKhuyenMai, tenNguoiDung);
        redirectAttributes.addFlashAttribute("message","Sửa khuyến mãi thành công!");
        return "redirect:/qlkhuyenmai"; // Chuyển hướng đến trang quản lý khuyến mãi
    }

    @GetMapping("search")
    public String search(@RequestParam("keyword") String keyword,
                         @RequestParam(value = "ngayBatDau", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate ngayBatDau,
                         @RequestParam(value = "ngayKetThuc", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate ngayKetThuc,
                         @RequestParam(value = "trangThai", required = false) Integer trangThai,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         Model model) {
        int pageSize = 5; // Số lượng bản ghi mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending()); // Sắp xếp theo id tăng dần

        // Sử dụng phương thức searchKhuyenMai từ servic   e
        Page<KhuyenMai> khuyenMaiPage = khuyenMaiService.searchKhuyenMai(keyword, ngayBatDau, ngayKetThuc,trangThai, pageable);


        // Thêm dữ liệu phân trang vào model
        model.addAttribute("khuyenMais", khuyenMaiPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", khuyenMaiPage.getTotalPages());
        model.addAttribute("keyword", keyword); // Để giữ lại từ khóa trong form tìm kiếm
        model.addAttribute("ngayBatDau", ngayBatDau); // Để giữ lại ngày bắt đầu trong form tìm kiếm
        model.addAttribute("ngayKetThuc", ngayKetThuc); // Để giữ lại ngày kết thúc trong form tìm kiếm
        model.addAttribute("trangThai", trangThai); // Để giữ lại trạng thái trong form tìm kiếm
        return "templateadmin/qlkhuyenmai";

    }
}
