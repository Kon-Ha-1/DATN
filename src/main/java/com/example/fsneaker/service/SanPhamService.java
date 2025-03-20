package com.example.fsneaker.service;

import com.example.fsneaker.entity.SanPham;
import com.example.fsneaker.repositories.SanPhamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.Optional;

@Service
public class SanPhamService {
    @Autowired
    private SanPhamRepo sanPhamRepo;

    public SanPham themSP(SanPham sanPham, String tenNguoiDung){
        Date now = new Date();
        if(sanPham.getId() == 0){
            sanPham.setNgayTao(now);
            sanPham.setNguoiTao(tenNguoiDung);
        }
        sanPham.setNgaySua(now);
        sanPham.setNguoiSua(tenNguoiDung);
        return sanPhamRepo.save(sanPham);
    }
    public boolean existsByMaSanPham(String maSanPham){
        return sanPhamRepo.existsByMaSanPham(maSanPham);
    }
    public SanPham getSanPhamById(Integer id){
        Optional<SanPham> sanPham = sanPhamRepo.findById(id);
        return sanPham.orElse(null);
    }
}
