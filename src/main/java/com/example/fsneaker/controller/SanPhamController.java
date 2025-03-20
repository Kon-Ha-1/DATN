package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.*;
import com.example.fsneaker.repositories.*;
import com.example.fsneaker.response.ResponseMessage;
import com.example.fsneaker.response.ValidationErrorResponse;
import com.example.fsneaker.service.SanPhamChiTietService;
import com.example.fsneaker.service.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SanPhamController {

    @Autowired
    private SanPhamRepo sanPhamRepo;

    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;

    @Autowired
    private XuatXuRepo xuatXuRepo;

    @Autowired
    private ThuongHieuRepo thuongHieuRepo;

    @Autowired
    private KhuyenMaiRepo khuyenMaiRepo;
    @Autowired
    private KichThuocRepo kichThuocRepo;
    @Autowired
    private MauSacRepo mauSacRepo;
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;


    @GetMapping("/qlsanpham")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") Integer pageNo,
                        @RequestParam(name = "limit", defaultValue = "5") Integer pageSize,
                        @RequestParam(name = "tab", defaultValue = "home") String tab,
                        @RequestParam(name = "serchSanPham", required = false) String serchSanPham,
                        @RequestParam(name = "idThuongHieu", required = false) Integer idThuongHieu,
                        @RequestParam(name = "idXuatXu", required = false) Integer idXuatXu
    ) {


        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<SanPham> list;

        if (serchSanPham != null && !serchSanPham.isEmpty() && idThuongHieu != null && idXuatXu != null) {
            list = sanPhamRepo.findSanPhamByAll(pageable);
        }

        //search theo mã sản phẩm, tên sản phẩm
        if (serchSanPham != null && !serchSanPham.isEmpty()) {
            list = sanPhamRepo.serchSanPhamByCodeOrName(serchSanPham, pageable);
        }
        // lọc theo thương hiệu
        else if (idThuongHieu != null) {
            list = sanPhamRepo.searchByIdThuongHieu(idThuongHieu, pageable);
        }
        //lọc theo xuất xứ
        else if (idXuatXu != null) {
            list = sanPhamRepo.searchByIdXuatXu(idXuatXu, pageable);
        } else {
            list = sanPhamRepo.findSanPhamByAll(pageable);
        }

        model.addAttribute("listSanPham", list);

        List<KhuyenMai> listKm = khuyenMaiRepo.findByIdAndTrangThai(1);
        model.addAttribute("listKhuyenMai", listKm);
        List<ThuongHieu> listTh = thuongHieuRepo.findAll();
        model.addAttribute("listThuongHieu", listTh);
        List<XuatXu> listXu = xuatXuRepo.findAll();
        model.addAttribute("listXuatXu", listXu);
        model.addAttribute("tab", tab);
        return "templateadmin/qlsanpham";
    }

    @GetMapping("/qlsanphamchitiet")
    public String index2(Model model,
                         @RequestParam(name = "page", defaultValue = "0") Integer pageNo,
                         @RequestParam(name = "limit", defaultValue = "5") Integer pageSize,
                         @RequestParam(name = "searchSanPhamChiTiet", required = false) String searchSanPhamChiTiet,
                         @RequestParam(name = "idSanPham", required = false) Integer idSanPham,
                         @RequestParam(name = "idMauSac", required = false) Integer idMauSac,
                         @RequestParam(name = "idKichThuoc", required = false) Integer idKichThuoc,
                         @RequestParam(name = "minPrice", required = false) Double minPrice,
                         @RequestParam(name = "maxPrice", required = false) Double maxPrice) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<SanPhamChiTiet> list2;


        if (searchSanPhamChiTiet != null && !searchSanPhamChiTiet.isEmpty()) {
            list2 = sanPhamChiTietRepo.searchByMaSanPhamChiTiet(searchSanPhamChiTiet, pageable);
        } else if (idSanPham != null) {
            list2 = sanPhamChiTietRepo.searcBySanPhamId(idSanPham, pageable);
        } else if (idMauSac != null) {
            list2 = sanPhamChiTietRepo.searchByMauSacId(idMauSac, pageable);
        } else if (idKichThuoc != null) {
            list2 = sanPhamChiTietRepo.searchByKichThuocId(idKichThuoc, pageable);
        } else if (minPrice != null && maxPrice != null) {
            list2 = sanPhamChiTietRepo.searchByPrice(minPrice, maxPrice, pageable);
        } else {
            list2 = sanPhamChiTietService.findPaginated(pageNo,pageSize);
        }

        model.addAttribute("listSanPhamChiTiet", list2);
        model.addAttribute("listSanPhams", sanPhamRepo.findAll());
        model.addAttribute("listKichThuoc", kichThuocRepo.findAll());
        model.addAttribute("listMauSac", mauSacRepo.findAll());
        model.addAttribute("tab", "profile");

        return "templateadmin/qlsanpham";
    }
    @GetMapping("/them-san-pham-form")
    public String themSanPhamForm(Model model){
        SanPham sanPham = new SanPham();
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("listKhuyenMai", khuyenMaiRepo.findByIdAndTrangThai(1));
        model.addAttribute("listThuongHieu", thuongHieuRepo.findAll());
        model.addAttribute("listXuatXu", xuatXuRepo.findAll());
        return "templateadmin/them-san-pham";
    }
    @PostMapping("/them-san-pham")
    public String store(
            @Valid @ModelAttribute("sanPham")SanPham sanPham,
            BindingResult result,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "khuyenMaiId", required = false) Integer khuyenMaiId) {

        if (result.hasErrors()) {
            model.addAttribute("errors", result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
            model.addAttribute("sanPham", sanPham);
            model.addAttribute("listKhuyenMai", khuyenMaiRepo.findByIdAndTrangThai(1));
            model.addAttribute("listThuongHieu", thuongHieuRepo.findAll());
            model.addAttribute("listXuatXu", xuatXuRepo.findAll());
            return "templateadmin/them-san-pham";
        }
        if(sanPhamService.existsByMaSanPham(sanPham.getMaSanPham())){
            model.addAttribute("errorMaSanPham", "Mã sản phẩm không được trùng!");
            model.addAttribute("sanPham", sanPham);
            model.addAttribute("listKhuyenMai", khuyenMaiRepo.findByIdAndTrangThai(1));
            model.addAttribute("listThuongHieu", thuongHieuRepo.findAll());
            model.addAttribute("listXuatXu", xuatXuRepo.findAll());
            return "templateadmin/them-san-pham";
        }
        KhuyenMai km = khuyenMaiId != null ? khuyenMaiRepo.findById(khuyenMaiId).orElse(null) : null;
        
        sanPham.setKhuyenMai(km);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        sanPhamService.themSP(sanPham, tenNguoiDung);
        redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
        return "redirect:/qlsanpham";
    }
    @GetMapping("/sua-san-pham-form/{id}")
    public String suaSanPhamForm(@PathVariable("id")Integer id, Model model){
        SanPham sanPham = sanPhamService.getSanPhamById(id);
        model.addAttribute("sanPham", sanPham);
        List<ThuongHieu> listTh = thuongHieuRepo.findAll();
        model.addAttribute("listThuongHieu", listTh);
        List<XuatXu> listXu = xuatXuRepo.findAll();
        model.addAttribute("listXuatXu", listXu);
        List<KhuyenMai> listKm = khuyenMaiRepo.findByIdAndTrangThai(1);
        model.addAttribute("listKhuyenMai", listKm);
        return "templateadmin/sua-san-pham";
    }
    @PostMapping("/sua-san-pham/{id}")
    public String suaSanPham(@PathVariable("id") Integer id,
                             @Valid @ModelAttribute("sanPham") SanPham sanPham,BindingResult result,
                             Model model,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(value = "thuongHieuId", required = false) Integer thuongHieuId,
                             @RequestParam(value = "khuyenMaiId", required = false) Integer khuyenMaiId,
                             @RequestParam(value = "xuatXuId", required = false) Integer xuatXuId){
        if(result.hasErrors()){
            model.addAttribute("listKhuyenMai", khuyenMaiRepo.findByIdAndTrangThai(1));
            model.addAttribute("listThuongHieu", thuongHieuRepo.findAll());
            model.addAttribute("listXuatXu", xuatXuRepo.findAll());
            return "templateadmin/sua-san-pham";
        }
        // Lấy các đối tượng liên quan từ repository
//        KhuyenMai km = (khuyenMaiId != null) ? khuyenMaiRepo.findById(khuyenMaiId).orElse(null) : null;
        // Lưu lại sanPham đã cập nhật
        SanPham sanPhamCu = sanPhamService.getSanPhamById(id);
        sanPhamCu.setMaSanPham(sanPham.getMaSanPham());
        sanPhamCu.setTenSanPham(sanPham.getTenSanPham());
        sanPhamCu.setKhuyenMai(sanPham.getKhuyenMai());
        sanPhamCu.setThuongHieu(sanPham.getThuongHieu());
        sanPhamCu.setXuatXu(sanPham.getXuatXu());
        sanPhamCu.setNgayTao(sanPhamCu.getNgayTao());
        sanPhamCu.setNguoiTao(sanPhamCu.getNguoiTao());
        sanPhamCu.setTrangThai(sanPham.getTrangThai());
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        sanPhamService.themSP(sanPhamCu , tenNguoiDung);
        if (sanPham.getKhuyenMai() != null) {
            updateGiaBanGiamGiaSpt(sanPham, sanPham.getKhuyenMai());
        }else{
            List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietRepo.findChiTietBySanPham(sanPham);
            // Nếu không có khuyến mãi, cập nhật giá bán giảm giá về 0 hoặc null
            for (SanPhamChiTiet sanPhamChiTiet : sanPhamChiTietList) {
                sanPhamChiTiet.setGiaBanGiamGia(null);  // Hoặc sanPhamChiTiet.setGiaBanGiamGia(BigDecimal.ZERO); tùy thuộc vào yêu cầu
                sanPhamChiTietService.themSPCT(sanPhamChiTiet, tenNguoiDung);
            }
        }
        redirectAttributes.addFlashAttribute("message", "Sửa sản phẩm thành công!");
        return "redirect:/qlsanpham";
    }

    public void updateGiaBanGiamGiaSpt(SanPham sanPham, KhuyenMai khuyenMai) {

        List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietRepo.findChiTietBySanPham(sanPham);

        BigDecimal giaTriKhuyenMai = khuyenMai.getGiaTri();

        for (SanPhamChiTiet sanPhamChiTiet : sanPhamChiTietList) {
            BigDecimal giaBan = sanPhamChiTiet.getGiaBan();
            BigDecimal giaBanGiamGia;
            if (giaTriKhuyenMai.compareTo(BigDecimal.valueOf(100)) <= 0) {
                giaBanGiamGia = giaBan.subtract(giaBan.multiply(giaTriKhuyenMai).divide(BigDecimal.valueOf(100)));
            } else {
                giaBanGiamGia = giaBan.subtract(giaTriKhuyenMai);
            }
            sanPhamChiTiet.setGiaBanGiamGia(giaBanGiamGia);
            sanPhamChiTietRepo.save(sanPhamChiTiet);
        }
    }



}
