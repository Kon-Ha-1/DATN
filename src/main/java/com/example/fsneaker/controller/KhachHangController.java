package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.service.KhachHangService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@SessionAttributes("khachHang")
public class KhachHangController {
    @Autowired
    private KhachHangService khachHangService;
    @GetMapping("/qlkhachhang")
    public String KhachHang(@RequestParam(value = "keyword",required = false)String keyword, @RequestParam(value = "page",defaultValue =  "0")int page, Model model){
        int pageSize = 5;
        Page<KhachHang> khachHangPage;
        if(keyword != null && !keyword.isEmpty()){
            khachHangPage = khachHangService.searchPaginated(keyword,page,pageSize);
            model.addAttribute("keyword",keyword);
        }else{
            khachHangPage = khachHangService.findPaginated(page,pageSize);
        }
        model.addAttribute("khachHangs",khachHangPage);
        return "templateadmin/qlkhachhang";
    }
    @GetMapping("/themkhachhang-form")
    public String addKhachHangForm(Model model){
        KhachHang khachHang = new KhachHang();
        model.addAttribute("khachHang", khachHang);
        return "templateadmin/themkhachhang";
    }

    @PostMapping("/themkhachhang")
    public String addKhachHang(@Valid @ModelAttribute("khachHang") KhachHang khachHang, BindingResult result,Authentication authentication, RedirectAttributes redirectAttributes, Model model){
        if(result.hasErrors()){
            return "templateadmin/themkhachhang";
        }

        if(khachHangService.existsByMaKhachHang(khachHang.getMaKhachHang())){
            model.addAttribute("errorMessage","Mã khách hàng đã tồn tại!");
            return "templateadmin/themkhachhang";
        }
        if(khachHang.getDiaChi().trim().toLowerCase().contains("hà nội")){
            // Danh sách quận/ huyện tại Hà Nội
            List<String> quanHuyenHaNoi = List.of(
                    "ba đình", "hoàn kiếm", "đống đa", "thanh xuân",
                    "cầu giấy", "hai bà trưng", "hoàng mai", "long biên",
                    "hà đông", "bắc từ liêm", "nam từ liêm", "tây hồ",
                    "thanh trì", "gia lâm", "đông anh", "sóc sơn",
                    "mê linh", "chương mỹ", "thanh oai", "thường tín",
                    "phúc thọ", "đan phượng", "quốc oai", "hoài đức",
                    "ba vì", "mỹ đức", "ứng hòa", "thạch thất", "phú xuyên"
            );

            //Kiểm tra xem địa chỉ có chứa một quận/huyện hợp lệ hay không
            boolean coQuanHuyen =quanHuyenHaNoi.stream().anyMatch(khachHang.getDiaChi().toLowerCase()::contains);
            if(!coQuanHuyen){
                model.addAttribute("diaChi", "Địa chỉ Hà Nội phải bao gồm quận hoặc huyện cụ thể, ví dụ: 'Hà Đông, Hà Nội' hoặc 'Gia Lâm, Hà Nội'.");
            }
            return "templateadmin/themkhachhang";
        }
        if(khachHangService.existsBySoDienThoai(khachHang.getSoDienThoai())){
            model.addAttribute("errorSoDienThoai","Số điện thoại đã tồn tại!");
            return "templateadmin/themkhachhang";
        }
        if(khachHangService.existsByEmail(khachHang.getEmail())){
            model.addAttribute("errorEmail","Email đã tồn tại!");
            return "templateadmin/themkhachhang";
        }
        khachHang.setMatKhau("12345678");
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        khachHangService.themKH(khachHang, tenNguoiDung);
        redirectAttributes.addFlashAttribute("message", "Thêm khách hàng thành công!");
        return "redirect:/qlkhachhang";
    }
    @GetMapping("/sua/{id}")
    public String suaKhachHangForm(@PathVariable("id")Integer id, Model model){
        KhachHang khachHang = khachHangService.getKhachHangById(id);
        model.addAttribute("khachHang", khachHang);
        return "templateadmin/suakhachhang";
    }

    // Lưu thôn tin khách hàng sau khi chỉnh sửa
    @PostMapping("/cap-nhat/{id}")
    public String updateKhachHang(@PathVariable("id") Integer id, @Valid @ModelAttribute("khachHang")KhachHang khachHang, BindingResult result,RedirectAttributes redirectAttributes, Authentication authentication, Model model){
        if(result.hasErrors()){
            return "templateadmin/suakhachhang";
        }
        if(khachHangService.isDuplicationMaKhachHang(id,khachHang.getMaKhachHang())){
            model.addAttribute("errorMessage","Mã khách hàng đã tồn tại!");
            return "templateadmin/suakhachhang";
        }
        if(khachHang.getDiaChi().trim().toLowerCase().contains("hà nội")){
            // Danh sách quận/ huyện tại Hà Nội
            List<String> quanHuyenHaNoi = List.of(
                    "ba đình", "hoàn kiếm", "đống đa", "thanh xuân",
                    "cầu giấy", "hai bà trưng", "hoàng mai", "long biên",
                    "hà đông", "bắc từ liêm", "nam từ liêm", "tây hồ",
                    "thanh trì", "gia lâm", "đông anh", "sóc sơn",
                    "mê linh", "chương mỹ", "thanh oai", "thường tín",
                    "phúc thọ", "đan phượng", "quốc oai", "hoài đức",
                    "ba vì", "mỹ đức", "ứng hòa", "thạch thất", "phú xuyên"
            );
            // Kiểm tra xem địa chỉ có chứa thông tin chi tiết (ngõ, phố, khu vực, số nhà)
            boolean coChiTiet = khachHang.getDiaChi().toLowerCase().matches(".*(ngõ|hẻm|số|phố|khu|tòa nhà|làng|thôn).*");

            // Kiểm tra xem địa chỉ có phường/xã
            boolean coPhuongXa = khachHang.getDiaChi().toLowerCase().matches(".*(phường|xã|thị trấn).*");
            //Kiểm tra xem địa chỉ có chứa một quận/huyện hợp lệ hay không
            boolean coQuanHuyen =quanHuyenHaNoi.stream().anyMatch(khachHang.getDiaChi().toLowerCase()::contains);
            if(!coQuanHuyen){
                model.addAttribute("diaChi", "Địa chỉ Hà Nội phải bao gồm quận hoặc huyện cụ thể, ví dụ: 'Hà Đông, Hà Nội' hoặc 'Gia Lâm, Hà Nội'.");
                return "templateadmin/suakhachhang";
            }
            // Nếu thiếu thông tin chi tiết, báo lỗi
            if (!coChiTiet) {
                model.addAttribute("diaChi", "Địa chỉ phải bao gồm thông tin cụ thể như ngõ, phố, số nhà hoặc khu vực!");
                return "templateadmin/suakhachhang";
            }
            // Nếu thiếu phường/xã, báo lỗi
            if (!coPhuongXa) {
                model.addAttribute("diaChi", "Địa chỉ phải bao gồm phường hoặc xã!");
                return "templateadmin/suakhachhang";
            }
        }
        if(khachHangService.isDuplicationSoDienThoai(id, khachHang.getSoDienThoai())){
            model.addAttribute("errorSoDienThoai","Số điện thoại đã tồn tại!");
            return "templateadmin/suakhachhang";
        }
        if(khachHangService.isDuplicationEmail(id,khachHang.getEmail())){
            model.addAttribute("errorEmail","Email đã tồn tại!");
            return "templateadmin/suakhachhang";
        }
        //Lấy thông tin khách hàng cũ từ cơ sở dữ liệu theo id
        KhachHang khachHangCu = khachHangService.getKhachHangById(id);
        //Cập nhật thông tin mới từ form
        khachHangCu.setMaKhachHang(khachHang.getMaKhachHang());
        khachHangCu.setTenKhachHang(khachHang.getTenKhachHang());
        khachHangCu.setEmail(khachHang.getEmail());
        khachHangCu.setNgaySinh(khachHang.getNgaySinh());
        khachHangCu.setGioiTinh(khachHang.getGioiTinh());
        khachHangCu.setDiaChi(khachHang.getDiaChi());
        khachHangCu.setSoDienThoai(khachHang.getSoDienThoai());
        khachHangCu.setTrangThai(khachHang.getTrangThai());
        khachHangCu.setMatKhau(khachHangCu.getMatKhau());
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        // Lưu lại thông tin đã được cập nhật
        khachHangService.themKH(khachHangCu, tenNguoiDung);
        redirectAttributes.addFlashAttribute("message", "Sửa khách hàng thành công!");
        return "redirect:/qlkhachhang";
    }
}

