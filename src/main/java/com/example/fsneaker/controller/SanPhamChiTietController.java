package com.example.fsneaker.controller;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.KichThuoc;
import com.example.fsneaker.entity.MauSac;
import com.example.fsneaker.entity.SanPham;
import com.example.fsneaker.entity.SanPhamChiTiet;
import com.example.fsneaker.repositories.KichThuocRepo;
import com.example.fsneaker.repositories.MauSacRepo;
import com.example.fsneaker.repositories.SanPhamChiTietRepo;
import com.example.fsneaker.repositories.SanPhamRepo;
import com.example.fsneaker.response.ValidationErrorResponse;
import com.example.fsneaker.service.SanPhamChiTietService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class SanPhamChiTietController {
    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;
    @Autowired
    private SanPhamRepo sanPhamRepo;
    @Autowired
    private MauSacRepo mauSacRepo;
    @Autowired
    private KichThuocRepo kichThuocRepo;
//    @Autowired
//    private StorageService storageService;

//    @GetMapping("/qlsanphamchitiet")
//     public String index(Model model) {
//        return "templateadmin/qlsanphamchitiet.html";
//     }

    @GetMapping("/them-san-pham-chi-tiet-form")
    public String themSanPhamChiTietForm(Model model){
        model.addAttribute("listSanPham", sanPhamRepo.findAll());
        model.addAttribute("listKichThuoc", kichThuocRepo.findAll());
        model.addAttribute("listMauSac", mauSacRepo.findAll());
        SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();
        model.addAttribute("sanPhamChiTiet",sanPhamChiTiet);
        return "templateadmin/them-san-pham-chi-tiet";
    }
    @PostMapping("/them-san-pham-chi-tiet")
    public String themSanPhamChiTiet(@Valid @ModelAttribute("sanPhamChiTiet") SanPhamChiTiet sanPhamChiTiet,
                                     BindingResult result,
                                     Model model,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes,
                                     @RequestParam(value = "sanPhamId", required = false) Integer sanPhamId,
                                     @RequestParam(value = "kichThuocIds", required = false) Integer[] kichThuocIds,
                                     @RequestParam(value = "mauSacIds", required = false) Integer[] mauSacIds,
                                     @RequestParam(value = "images", required = false) MultipartFile[] images){
        if(kichThuocIds == null || kichThuocIds.length == 0){
            model.addAttribute("errorKichThuoc","Chọn ít nhất một kích thước!");
        }
        if(mauSacIds == null || mauSacIds.length == 0){
            model.addAttribute("errorMauSac","Chọn ít nhất một màu sắc!");
        }
        if(images == null || images.length == 0 || images[0].isEmpty()){
            model.addAttribute("errorHinhAnh","Chọn ít nhất một hình ảnh, không quá 20MB!");
        }
        if(sanPhamChiTiet.getSoLuong() <= 0){
            model.addAttribute("errorSoLuong","Số lượng phải lớn hơn 0!");
        }
        if(result.hasErrors() || model.containsAttribute("errorKichThuoc") || model.containsAttribute("errorMauSac") || model.containsAttribute("errorHinhAnh") || model.containsAttribute("errorSoLuong")){
            model.addAttribute("listSanPham", sanPhamRepo.findAll());
            model.addAttribute("listKichThuoc", kichThuocRepo.findAll());
            model.addAttribute("listMauSac", mauSacRepo.findAll());
            return "templateadmin/them-san-pham-chi-tiet";
        }
        // Kiểm tra nếu có ảnh được upload
        List<String> imageNames = new ArrayList<>();
        if (images != null && images.length > 0) {
            String uploadDir = "C:/Users/ASUS/Documents/GitHub/Fsneaker/src/main/resources/static/images/product/";
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageName = image.getOriginalFilename();
                    Path path = Paths.get(uploadDir + imageName);
                    imageNames.add(imageName);
                    try {
                        Files.createDirectories(path.getParent());
                        Files.write(path, image.getBytes());
                    } catch (IOException e) {
                        model.addAttribute("errorMessage", "Lỗi lưu ảnh!");
                        return "templateadmin/them-san-pham-chi-tiet";
                    }
                }
            }
        }

        // Lấy giá trị phiếu giảm giá
        BigDecimal giaTriGiamGia = sanPhamChiTietRepo.giaTri(sanPhamId);

        // Tính giá bán sau giảm
        if (giaTriGiamGia != null && giaTriGiamGia.compareTo(BigDecimal.valueOf(100)) <= 0) {
            BigDecimal phanTramGiamGia = giaTriGiamGia.divide(BigDecimal.valueOf(100));
            BigDecimal giaBanGiamGia = sanPhamChiTiet.getGiaBan().multiply(BigDecimal.ONE.subtract(phanTramGiamGia));
            sanPhamChiTiet.setGiaBanGiamGia(giaBanGiamGia);
        } else if (giaTriGiamGia != null && giaTriGiamGia.compareTo(BigDecimal.valueOf(100)) > 0) {
            BigDecimal giabanGiamGia = giaTriGiamGia.subtract(giaTriGiamGia);
            sanPhamChiTiet.setGiaBanGiamGia(giabanGiamGia);
        } else {
            sanPhamChiTiet.setGiaBanGiamGia(BigDecimal.ZERO);
        }
        // Tiến hành lưu trữ sản phẩm chi tiết với các kích thước và màu sắc được chọn
        for (Integer kichThuocId : kichThuocIds) {
            for (Integer mauSacId : mauSacIds) {
                int maxStt = sanPhamChiTietRepo.findMaxStt();
                int index = maxStt + 1;
                SanPhamChiTiet newSanPhamChiTiet = new SanPhamChiTiet();
                String uniqueMaSanPhamChiTiet = "SPCT" + index;
                newSanPhamChiTiet.setMaSanPhamChiTiet(uniqueMaSanPhamChiTiet);
                newSanPhamChiTiet.setSanPham(sanPhamChiTiet.getSanPham());
                newSanPhamChiTiet.setKichThuoc(kichThuocRepo.getKichThuocById(kichThuocId));
                newSanPhamChiTiet.setMauSac(mauSacRepo.getMauSacById(mauSacId));
                newSanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong());
                newSanPhamChiTiet.setGiaBan(sanPhamChiTiet.getGiaBan());
                newSanPhamChiTiet.setGiaBanGiamGia(sanPhamChiTiet.getSanPham().getKhuyenMai() != null ? sanPhamChiTiet.getGiaBanGiamGia() : null);
                newSanPhamChiTiet.setTrangThai(sanPhamChiTiet.getTrangThai());
                // Lưu tên ảnh vào sản phẩm chi tiết nếu có
                if (!imageNames.isEmpty()) {
                    newSanPhamChiTiet.setImanges(String.join(",", imageNames));
                }
//                if (imageName != null) {
//                    newSanPhamChiTiet.setImanges(imageName);
//                }
                index++;
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                String tenNguoiDung = userDetails.getDisplayName();
                sanPhamChiTietService.themSPCT(newSanPhamChiTiet, tenNguoiDung);
            }
        }
        redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm chi tiết thành công!");
        return "redirect:/qlsanphamchitiet";
    }
    @GetMapping("/sua-san-pham-chi-tiet-form/{id}")
    public String suaSanPhamChiTietForm(@PathVariable("id")Integer id, Model model){
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietService.getSanPhamChiTietById(id);
        model.addAttribute("sanPhamChiTiet", sanPhamChiTiet);
        model.addAttribute("listSanPham", sanPhamRepo.findAll());
        model.addAttribute("listKichThuoc", kichThuocRepo.findAll());
        model.addAttribute("listMauSac", mauSacRepo.findAll());
        return "templateadmin/sua-san-pham-chi-tiet";
    }
    @PostMapping("/sua-san-pham-chi-tiet/{id}")
    public String suaSanPhamChiTiet(@PathVariable("id")Integer id,
                                    @Valid @ModelAttribute("sanPhamChiTiet") SanPhamChiTiet sanPhamChiTiet,
                                    BindingResult result,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes,
                                    Model model,
                                    @RequestParam(name = "sanPhamId", required = false) Integer sanPhamId,
                                    @RequestParam(value = "imagees", required = false) MultipartFile[] imagees){

        if (result.hasErrors()) {
            model.addAttribute("listSanPham", sanPhamRepo.findAll());
            model.addAttribute("listKichThuoc", kichThuocRepo.findAll());
            model.addAttribute("listMauSac", mauSacRepo.findAll());
            return "templateadmin/sua-san-pham-chi-tiet";
        }
        // Lấy sản phẩm chi tiết hiện tại từ cơ sở dữ liệu
        SanPhamChiTiet existingProductDetail = sanPhamChiTietService.getSanPhamChiTietById(id);
        if (existingProductDetail == null) {
            model.addAttribute("errorMessage","Không tìm thấy sản phẩm chi tiêt!");
            return "templateadmin/sua-san-pham-chi-tiet";
        }

        // Cập nhật các thông tin từ request
        existingProductDetail.setSanPham(sanPhamChiTiet.getSanPham());
        existingProductDetail.setKichThuoc(sanPhamChiTiet.getKichThuoc());
        existingProductDetail.setMauSac(sanPhamChiTiet.getMauSac());
        existingProductDetail.setMaSanPhamChiTiet(existingProductDetail.getMaSanPhamChiTiet());
        existingProductDetail.setSoLuong(sanPhamChiTiet.getSoLuong());
        existingProductDetail.setGiaBan(sanPhamChiTiet.getGiaBan());
        existingProductDetail.setNgaySanXuat(sanPhamChiTiet.getNgaySanXuat());
        existingProductDetail.setTrangThai(sanPhamChiTiet.getTrangThai());
        // Xử lý ảnh nếu có tải lên ảnh mới
        // Xử lý lưu trữ nhiều ảnh
        if (imagees != null && imagees.length > 0) {
            String uploadDir = "C:/Users/ASUS/Documents/GitHub/Fsneaker/src/main/resources/static/images/product/";
            List<String> imageNames = new ArrayList<>();

            for (MultipartFile image : imagees) {
                if (!image.isEmpty()) {
                    String imageName = image.getOriginalFilename();
                    Path path = Paths.get(uploadDir + imageName);
                    try {
                        Files.createDirectories(path.getParent());
                        Files.write(path, image.getBytes());
                        imageNames.add(imageName); // Thêm tên ảnh vào danh sách
                        // Lưu danh sách ảnh vào cột `imanges` (chuỗi các tên ảnh cách nhau bởi dấu phẩy)
                        existingProductDetail.setImanges(String.join(",", imageNames));
                    } catch (IOException e) {
                        model.addAttribute("errorMessage", "Lỗi lưu ảnh: "+ imageName);
                        return "templateadmin/sua-san-pham-chi-tiet";
                    }
                }else{
                    existingProductDetail.setImanges(existingProductDetail.getImanges());
                }
            }


        }

        // Tính giá bán sau giảm giá (logic giữ nguyên)
        BigDecimal giaTriGiamGia = sanPhamChiTietRepo.giaTri(sanPhamId);
        if (giaTriGiamGia != null && giaTriGiamGia.compareTo(BigDecimal.valueOf(100)) <= 0) {
            BigDecimal phanTramGiamGia = giaTriGiamGia.divide(BigDecimal.valueOf(100));
            BigDecimal giaBanGiamGia = existingProductDetail.getGiaBan().multiply(BigDecimal.ONE.subtract(phanTramGiamGia));
            existingProductDetail.setGiaBanGiamGia(giaBanGiamGia);
        } else if (giaTriGiamGia != null && giaTriGiamGia.compareTo(BigDecimal.valueOf(100)) > 0) {
            BigDecimal giabanGiamGia = giaTriGiamGia.subtract(giaTriGiamGia);
            existingProductDetail.setGiaBanGiamGia(giabanGiamGia);
        } else {
            existingProductDetail.setGiaBanGiamGia(null);
        }
        existingProductDetail.setNgayTao(existingProductDetail.getNgayTao());
        existingProductDetail.setNguoiTao(existingProductDetail.getNguoiTao());
        // Lưu lại thay đổi
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String tenNguoiDung = userDetails.getDisplayName();
        sanPhamChiTietService.themSPCT(existingProductDetail, tenNguoiDung);
        redirectAttributes.addFlashAttribute("message", "Sửa sản phẩm chi tiết thành công!");
        return "redirect:/qlsanphamchitiet";
    }
}
