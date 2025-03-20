package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.KichThuoc;
import com.example.fsneaker.repositories.KichThuocRepo;
import com.example.fsneaker.service.KichThuocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class KichThuocController {
    @Autowired
    KichThuocRepo kichThuocRepo;
    @Autowired
    private KichThuocService kichThuocService;
//    @GetMapping("/qlkichthuoc")
//    public String index(Model model){
//        List<KichThuoc> list = kichThuocRepo.findAll();
//        KichThuoc kt = new KichThuoc();
//        model.addAttribute("kt",kt);
//        model.addAttribute("kichthuoc", list);
//        return "templateadmin/qlkichthuoc";
//    }

    @GetMapping("/qlkichthuoc")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") Integer pageNo,
                        @RequestParam(name = "limit", defaultValue = "5") Integer pageSize
    ){

//        Pageable pageable = PageRequest.of(pageNo, pageSize);
//        Page<KichThuoc> list = kichThuocRepo.findAll(pageable);

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<KichThuoc> kichThuocs = kichThuocService.getKichThuocAll(pageable);
        KichThuoc kt = new KichThuoc();
        model.addAttribute("kt",kt);
        model.addAttribute("kichthuocs", kichThuocs);
        return "templateadmin/qlkichthuoc";
    }


    @PostMapping("qlkichthuoc/store")
    public String store(@ModelAttribute("kt") KichThuoc kt,
                         Model model, RedirectAttributes redirectAttributes, Authentication authentication){
        Map<String, String> errors = new HashMap<>();
        //Kiểm tra lỗi validate
        if(kt.getMakichThuoc() == null || kt.getMakichThuoc().isBlank()){
            errors.put("makichThuoc","Mã kích thước không được để trống!");
        }else if(kt.getMakichThuoc().length() < 5 || kt.getMakichThuoc().length() > 20){
            errors.put("makichThuoc", "Mã kích thước phải từ 5 đến 20 ký tự!");
        }else if(kichThuocRepo.existsByMakichThuoc(kt.getMakichThuoc())){
            redirectAttributes.addFlashAttribute("error","Mã kích thước đã tồn tại!");
            return "redirect:/qlkichthuoc";
        }
        if(kt.getTenKichThuoc() == null || kt.getTenKichThuoc().isBlank()){
            errors.put("tenKichThuoc","Tên kích thước không được để trống!");
        }else if(kt.getTenKichThuoc().length() != 2){
            errors.put("tenKichThuoc","Tên kích thước phải đúng 2 ký tự!");
        }
        if(!errors.isEmpty()){
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:/qlkichthuoc";
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        kichThuocService.themKT(kt, tenNguoiDung);
        redirectAttributes.addFlashAttribute("message","Thêm kích thước thành công!");
        return "redirect:/qlkichthuoc";
    }

    @GetMapping("/qlkichthuoc/edit/{id}")
    public String editKichThuoc(@PathVariable int id, Model model) {
        KichThuoc kt = kichThuocRepo.getKichThuocById(id);
        model.addAttribute("editKichThuoc",kt);
        List<KichThuoc> list = kichThuocRepo.findAll();
        model.addAttribute("kthuoc",list);
        return "templateadmin/qlkichthuoc";
    }

    @PostMapping("/qlkichthuoc/update")
    public String update(
            @RequestParam int id,
            @RequestParam String makichThuoc,
            @RequestParam String tenKichThuoc,
            @RequestParam int trangThai,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        KichThuoc existingKichThuoc = kichThuocRepo.getKichThuocById(id);
        if (existingKichThuoc != null) {
            existingKichThuoc.setMakichThuoc(makichThuoc);
            existingKichThuoc.setTenKichThuoc(tenKichThuoc);
            existingKichThuoc.setTrangThai(trangThai);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String tenNguoiDung = userDetails.getDisplayName();
            kichThuocService.themKT(existingKichThuoc, tenNguoiDung);
        }
        redirectAttributes.addFlashAttribute("message", "Sửa kích thước thành công!");
        return "redirect:/qlkichthuoc";
    }

}
