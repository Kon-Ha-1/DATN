package com.example.fsneaker.service;

import com.example.fsneaker.entity.KhuyenMai;
import com.example.fsneaker.repositories.KhuyenMaiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

//Tạo và cập nhật mã khuyến mãi,
// áp dụng giảm giá,
// lọc khuyến mãi theo điều kiện,
// phân trang khuyến mãi

@Service
public class KhuyenMaiService {
    @Autowired
    private KhuyenMaiRepo khuyenMaiRepository;

    public Page<KhuyenMai> searchKhuyenMai(String keyword, LocalDate ngayBatDau, LocalDate ngayKetThuc, Integer trangThai,  Pageable pageable) {

        return khuyenMaiRepository.searchByKeywordAndDate(keyword, ngayBatDau, ngayKetThuc, trangThai, pageable);
    }
    public KhuyenMai themKM(KhuyenMai khuyenMai, String tenNguoiDung){
        Date now = new Date();
        if(khuyenMai.getId() == 0){
            khuyenMai.setNgayTao(now);
            khuyenMai.setNguoiTao(tenNguoiDung);
        }
        khuyenMai.setNgaySua(now);
        khuyenMai.setNguoiSua(tenNguoiDung);
        return khuyenMaiRepository.save(khuyenMai);
    }
}
