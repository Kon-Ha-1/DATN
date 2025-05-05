package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.ThuongHieu;
import com.example.fsneaker.entity.XuatXu;
import com.example.fsneaker.repositories.ThuongHieuRepo;
import com.example.fsneaker.service.ThuongHieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ThuongHieuController {

    @Autowired
    private ThuongHieuRepo thuongHieuRepo;
    @Autowired
    private ThuongHieuService thuongHieuService;
    @GetMapping("/qlthuonghieu")
    public String index( Model model,
                         @RequestParam(name = "page", defaultValue = "0")Integer pageNo,
                         @RequestParam(name = "limit", defaultValue = "5")Integer pageSize,
                         Pageable pageable){

        ThuongHieu thuongHieu = new ThuongHieu();
        model.addAttribute("thuongHieu",thuongHieu);
        Page<ThuongHieu> thuongHieuList = thuongHieuService.getThuongHieuAll(pageable);

//        List<ThuongHieu> thuongHieuList = thuongHieuRepo.findAll();
        model.addAttribute("thieu", thuongHieuList);
        return "/templateadmin/qlthuonghieu";

    }
// day la quan ly thuong hieu
    @PostMapping("/qlthuonghieu/store")
    public String store(@ModelAttribute("thuongHieu") ThuongHieu thuongHieu, RedirectAttributes redirectAttributes, Authentication authentication){
        Map<String , String > errors = new HashMap<>();
        if(thuongHieu.getMaThuongHieu() == null || thuongHieu.getMaThuongHieu().isBlank()){
            errors.put("maThuongHieu","Mã thương hiệu không được đê trống!");
        }else if(thuongHieu.getMaThuongHieu().length() < 5 || thuongHieu.getMaThuongHieu().length() > 20){
            errors.put("maThuongHieu", "Mã thương hiệu từ 5 đến 20 ký tự!");
        }else if(thuongHieuRepo.existsByMaThuongHieu(thuongHieu.getMaThuongHieu())){
            redirectAttributes.addFlashAttribute("error","Mã thương hiệu đã tồn tại!");
            return "redirect:/qlthuonghieu";
        }
        if(thuongHieu.getTenThuongHieu() == null || thuongHieu.getTenThuongHieu().isBlank()){
            errors.put("tenThuongHieu","Tên thương hiệu không được để trống!");
        }else if(thuongHieu.getTenThuongHieu().length() < 2 || thuongHieu.getTenThuongHieu().length() > 30){
            errors.put("tenThuongHieu", "Tên thương hiệu từ 2 đến 30 ký tự!");
        }
        if(!errors.isEmpty()){
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:/qlthuonghieu";
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        thuongHieuService.themTH(thuongHieu, tenNguoiDung);
        redirectAttributes.addFlashAttribute("message","Thêm thương hiệu thành công!");
        return "redirect:/qlthuonghieu";
    }

    @GetMapping("/qlthuonghieu/edit/{id}")
    public String editXuatXu(@PathVariable int id, Model model) {
        ThuongHieu th = thuongHieuRepo.getThuongHieuById(id);
        model.addAttribute("th",th);
        List<ThuongHieu> list = thuongHieuRepo.findAll();
        model.addAttribute("thieu",list);
        return "templateadmin/qlthuonghieu.html";
    }

    @PostMapping("/qlthuonghieu/update")
    public String update(
            @RequestParam int id,
            @RequestParam String maThuongHieu,
            @RequestParam String tenThuongHieu,
            @RequestParam int trangThai,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        ThuongHieu existingThuongHieu = thuongHieuRepo.getThuongHieuById(id);
        if (existingThuongHieu != null) {
            existingThuongHieu.setMaThuongHieu(maThuongHieu);
            existingThuongHieu.setTenThuongHieu(tenThuongHieu);
            existingThuongHieu.setTrangThai(trangThai);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String tenNguoiDung = userDetails.getDisplayName();
            thuongHieuService.themTH(existingThuongHieu, tenNguoiDung);
        }
        redirectAttributes.addFlashAttribute("message","Sửa thương hiệu thành công!");
        return "redirect:/qlthuonghieu";
    }

}