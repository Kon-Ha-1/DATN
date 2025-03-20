package com.example.fsneaker.service;

import com.example.fsneaker.entity.KichThuoc;
import com.example.fsneaker.repositories.KichThuocRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class KichThuocService {
    @Autowired
    private KichThuocRepo kichThuocRepo;

    public List<Object[]> getKichThuocVoiSanPham(Integer idThuongHieu){
        return kichThuocRepo.findKichThuocWithSanPham(idThuongHieu);
    }
    public Page<KichThuoc> getKichThuocAll(Pageable pageable){
        return kichThuocRepo.findAllKichThuoc(pageable);
    }
    public KichThuoc themKT(KichThuoc kichThuoc, String tenNguoiDung){
        Date now = new Date();
        if(kichThuoc.getId() == 0){
            kichThuoc.setNgayTao(now);
            kichThuoc.setNguoiTao(tenNguoiDung);
        }
        kichThuoc.setNgaySua(now);
        kichThuoc.setNguoiSua(tenNguoiDung);
        return kichThuocRepo.save(kichThuoc);
    }
}
