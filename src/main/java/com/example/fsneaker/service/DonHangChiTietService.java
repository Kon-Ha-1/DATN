package com.example.fsneaker.service;

import com.example.fsneaker.entity.DonHang;
import com.example.fsneaker.entity.DonHangChiTiet;
import com.example.fsneaker.repositories.DonHangChiTietRepo;
import com.example.fsneaker.repositories.DonHangRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class DonHangChiTietService {
    @Autowired
    private DonHangChiTietRepo donHangChiTietRepo;
    //Chỗ này là của trưởng nhóm code cấm đụng
    public List<DonHangChiTiet> getAllDonHangChiTietById(int id){
        return donHangChiTietRepo.findAllByDonHangId(id);
    }
    public void themDonHangChiTiet(DonHangChiTiet donHangChiTiet){
        donHangChiTietRepo.save(donHangChiTiet);
    }
    public void xoaDonHangChiTiet(int id){
        donHangChiTietRepo.deleteById(id);
    }
    public DonHangChiTiet getDonHangChiTietById(int id){
        return donHangChiTietRepo.findById(id).orElse(null);
    }

    public void updateHoaDonChiTietById(DonHangChiTiet donHangChiTiet){
        donHangChiTietRepo.save(donHangChiTiet);
    }

    public Page<Object[]> getBestSellingProducts(String trangThai, int page, int size){
        return donHangChiTietRepo.findBestSellingProducts(trangThai,PageRequest.of(page,size));
    }
    public Page<Object[]> getSanPhamDoanhThuCaoNhat(String trangThai,int page, int size){
        return donHangChiTietRepo.sanPhamDoanhThuCaoNhat(trangThai,PageRequest.of(page,size));
    }
    public Page<Object[]> getSanPhamBanChayTheoNgay(String trangThai,LocalDate startDate, LocalDate endDate, int page, int size){
        return donHangChiTietRepo.sanPhamBanChayTheoNgay(trangThai,startDate,endDate,PageRequest.of(page,size));
    }
    public Page<Object[]> getSanPhamDoanhThuCaoNhatTheoNgay(String trangThai, LocalDate startDate, LocalDate endDate, int page, int size){
        return donHangChiTietRepo.sanPhamDoanhThuCaoNhatTheoNgay(trangThai,startDate, endDate, PageRequest.of(page,size));
    }
    //Phương thức lấy danh sách chi tiết đơn hàng theo đối tượng DơnHang
    public List<DonHangChiTiet> getByDonHang(DonHang donHang){
        return donHangChiTietRepo.findByDonHang(donHang);
    }

    //Phương thức hiển thị sản phẩm bán được theo thương hiệu
    public List<Object[]> getTopSellingProductsByBrand(Integer id, int limit){
        Pageable pageable = PageRequest.of(0, limit);
        return donHangChiTietRepo.findTopSellingProductsByBrand(id, pageable);
    }
    public DonHangChiTiet findById(int id){
        return donHangChiTietRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
    }
    public void save(DonHangChiTiet donHangChiTiet){
        donHangChiTietRepo.save(donHangChiTiet);
    }
    //Từ đây trở đi ai code gì thì note người code lại tránh nhầm lẫn

    public DonHangChiTiet getDonHangIdAndSanPhamChiTietId(Integer idDonHang, Integer idSanPhamChiTiet){
        return donHangChiTietRepo.findByDonHangIdAndSanPhamChiTietId(idDonHang,idSanPhamChiTiet);
    }
    public Page<Object[]> getNikeByPopularity( Integer idThuongHieu, int page, int size){
        return donHangChiTietRepo.findNikeByPopularity(idThuongHieu, PageRequest.of(page, size));
    }
}
