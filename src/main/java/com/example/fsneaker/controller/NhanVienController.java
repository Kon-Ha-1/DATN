package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.NhanVien;
import com.example.fsneaker.repositories.NhanVienRepo;
import com.example.fsneaker.service.NhanVienService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NhanVienController {
    @Autowired
    public NhanVienRepo nhanVienRepo;

    @Autowired
    public NhanVienService nhanVienService;

    @GetMapping("/them-nhan-vien-form")
    public String create(Model model){
        model.addAttribute("nhanVien", new NhanVien());
        return "templateadmin/addNhanVien";
    }

    @GetMapping("/qlnhanvien")
    public String index( Model model,@RequestParam(defaultValue = "0") int page) {

        int pageSize = 5; // Số lượng bản ghi mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<NhanVien> nhanVienPage = nhanVienService.getAll(pageable);

        // Thêm dữ liệu phân trang vào model
        model.addAttribute("nhanViens", nhanVienPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", nhanVienPage.getTotalPages());
        return "templateadmin/qlnhanvien";
    }


    @PostMapping("/add-nhan-vien")
    public String add(Model model, @Valid @ModelAttribute("nhanVien") NhanVien nhanVien, BindingResult validate, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (validate.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError e : validate.getFieldErrors()) {
                errors.put(e.getField(), e.getDefaultMessage());
            }
            model.addAttribute("errors", errors);
            return "templateadmin/addNhanVien";
        }
        if(nhanVienService.existsByMaNhanVien(nhanVien.getMaNhanVien())){
            model.addAttribute("errorMaNhanVien","Mã nhân viên đã tồn tại!");
            return "templateadmin/addNhanVien";
        }
        if(nhanVienService.existsByEmail(nhanVien.getEmail())){
            model.addAttribute("errorEmail","Email đã tồn tại!");
            return "templateadmin/addNhanVien";
        }
        if(nhanVienService.existsBySoDienThoai(nhanVien.getSoDienThoai())){
            model.addAttribute("errorSoDienThoai", "Số điện thoại đã tồn tại!");
            return "templateadmin/addNhanVien";
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        nhanVienService.themNV(nhanVien, tenNguoiDung);
        redirectAttributes.addFlashAttribute("message", "Thêm nhân viên thành công!");
        return "redirect:/qlnhanvien";
    }


    @GetMapping("/edit-nhan-vien/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
        NhanVien nhanVien = this.nhanVienRepo.findById(id).get();
        model.addAttribute("nhanVien", nhanVien);
        return "templateadmin/updateNhanVien";
    }


    @PostMapping("/update-nhan-vien/{id}")
    public String updateNhanVien(@PathVariable Integer id,@Valid @ModelAttribute NhanVien nhanVien, BindingResult validate, Authentication authentication,RedirectAttributes redirectAttributes, Model model) {
        if (validate.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError e : validate.getFieldErrors()) {
                errors.put(e.getField(), e.getDefaultMessage());
            }
            model.addAttribute("errors", errors);
            return "templateadmin/updateNhanVien";
        }
        NhanVien existingNhanVien = nhanVienRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nhân viên không tồn tại"));
        if(nhanVienService.isDeplicationMaNhanVien(id, nhanVien.getMaNhanVien())){
            model.addAttribute("errorMaNhanVien", "Mã nhân viên đã tồn tại!");
            return "templateadmin/updateNhanVien";
        }
        if(nhanVienService.isDeplicationEmail(id, nhanVien.getEmail())){
            model.addAttribute("errorEmail","Email đã tồn tai!");
            return "templateadmin/updateNhanVien";
        }
        if(nhanVienService.isDeplicationSoDienThoai(id, nhanVien.getSoDienThoai())){
            model.addAttribute("errorSoDienThoai","Số điện thoại đã tồn tại!");
            return "templateadmin/updateNhanVien";
        }
        existingNhanVien.setMaNhanVien(nhanVien.getMaNhanVien());
        existingNhanVien.setTenNhanVien(nhanVien.getTenNhanVien());
        existingNhanVien.setEmail(nhanVien.getEmail());
        existingNhanVien.setSoDienThoai(nhanVien.getSoDienThoai());
        existingNhanVien.setNgaySinh(nhanVien.getNgaySinh());
        existingNhanVien.setGioiTinh(nhanVien.getGioiTinh());
        existingNhanVien.setDiaChi(nhanVien.getDiaChi());
        existingNhanVien.setVaiTro(nhanVien.getVaiTro());
        existingNhanVien.setMatKhau(existingNhanVien.getMatKhau());
        existingNhanVien.setCccd(nhanVien.getCccd());
        existingNhanVien.setTrangThai(nhanVien.getTrangThai());
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        nhanVienService.themNV(existingNhanVien, tenNguoiDung);
        redirectAttributes.addFlashAttribute("Sửa nhân viên thành công!");
        return "redirect:/qlnhanvien";
    }

    @GetMapping("/search-nhan-vien")
    public String search(@RequestParam("keyword") String keyword,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         Model model) {

        int pageSize = 5; // Số lượng bản ghi mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize); // Sắp xếp theo id tăng dần

        Page<NhanVien> nhanVienPage = nhanVienService.searchNhanVien(keyword,pageable);

        // Thêm dữ liệu phân trang vào model
        model.addAttribute("nhanViens", nhanVienPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", nhanVienPage.getTotalPages());
        return "templateadmin/qlnhanvien";
    }

}
