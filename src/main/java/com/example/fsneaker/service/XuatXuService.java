package com.example.fsneaker.service;

import com.example.fsneaker.entity.XuatXu;
import com.example.fsneaker.repositories.MauSacRepo;
import com.example.fsneaker.repositories.XuatXuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class XuatXuService {
    @Autowired
    private XuatXuRepo xuatXuRepo;

    public Page<XuatXu> getXuatXuAll(Pageable pageable){
        return xuatXuRepo.findXuatXuAll(pageable);
    }
    public XuatXu themXX(XuatXu xuatXu,String tenNguoiDung){
        Date now = new Date();
        if(xuatXu.getId() == 0){
            xuatXu.setNgayTao(now);
            xuatXu.setNguoiTao(tenNguoiDung);
        }
        xuatXu.setNgaySua(now);
        xuatXu.setNguoiSua(tenNguoiDung);
        return xuatXuRepo.save(xuatXu);
    }
}
