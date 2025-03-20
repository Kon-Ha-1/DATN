package com.example.fsneaker.service;

import com.example.fsneaker.entity.MauSac;
import com.example.fsneaker.repositories.MauSacRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MauSacService {
    @Autowired
    private MauSacRepo mauSacRepo;

    public List<Object[]> getMauSacWithSanPham(Integer idThuongHieu){
        return mauSacRepo.findByMauSacWithProduct(idThuongHieu);
    }
    public Page<MauSac> getMauSacAll(Pageable pageable){
        return mauSacRepo.findByMauSac(pageable);
    }
    public MauSac themMS(MauSac mauSac, String tenNguoiDung){
        Date now = new Date();
        if(mauSac.getId() == 0){
            mauSac.setNgayTao(now);
            mauSac.setNguoiTao(tenNguoiDung);
        }
        mauSac.setNgaySua(now);
        mauSac.setNguoiSua(tenNguoiDung);
        return mauSacRepo.save(mauSac);
    }
}
