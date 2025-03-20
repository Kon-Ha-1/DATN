package com.example.fsneaker.service;

import com.example.fsneaker.entity.ThuongHieu;
import com.example.fsneaker.repositories.ThuongHieuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ThuongHieuService {
    @Autowired
    private ThuongHieuRepo thuongHieuRepo;

    public Page<ThuongHieu> getThuongHieuAll(Pageable pageable){
        return thuongHieuRepo.findThuongHieuAll(pageable);
    }
    public ThuongHieu themTH(ThuongHieu thuongHieu, String tenNguoiDung){
        Date now = new Date();
        if(thuongHieu.getId() == 0){
            thuongHieu.setNgayTao(now);
            thuongHieu.setNguoiTao(tenNguoiDung);
        }
        thuongHieu.setNgaySua(now);
        thuongHieu.setNguoiSua(tenNguoiDung);
        return thuongHieuRepo.save(thuongHieu);
    }
}
