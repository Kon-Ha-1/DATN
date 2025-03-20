package com.example.fsneaker.service;

import com.example.fsneaker.entity.GioHang;
import com.example.fsneaker.entity.GioHangChiTiet;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.repositories.GioHangChiTietRepo;
import com.example.fsneaker.repositories.GioHangRepo;
import com.example.fsneaker.repositories.KhachHangRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GioHangService {
    @Autowired
    private GioHangRepo gioHangRepo;
    @Autowired
    private GioHangChiTietRepo gioHangChiTietRepo;
    @Autowired
    private KhachHangRepo khachHangRepo;

    public GioHang getGioHangByMa(String maGioHang){
        return gioHangRepo.findByMaGioHang(maGioHang).orElseThrow();
    }
    public GioHang savaGioHang(GioHang gioHang){
        return gioHangRepo.save(gioHang);
    }

    public GioHang layGioHangTheoKhachHang(int idKhachHang){
        return gioHangRepo.findByKhachHangId(idKhachHang).orElse(null);
    }
    public void mergeGioHang(String sessionId, KhachHang khachHang){
        GioHang gioHangTamThoi = gioHangRepo.findByMaGioHang(sessionId).orElseThrow();
        GioHang gioHangKhachHang = gioHangRepo.findByKhachHangId(khachHang.getId()).orElse(null);
        if(gioHangTamThoi != null){
            if(gioHangKhachHang == null){
                //Nêếu khách hàng chưa có giỏ hàng, chuyển giỏ hàng tạm thời sang tài khoản
                gioHangTamThoi.setKhachHang(khachHang);
                gioHangTamThoi.setTrangThai(1);
                gioHangRepo.save(gioHangTamThoi);
            }else{
                //Hợp nhất các sản phẩm từ giỏ tạm thời vào giỏ hàng của khách hàng
                for(GioHangChiTiet item : gioHangTamThoi.getGioHangChiTietList()){
                    GioHangChiTiet existingItem = gioHangKhachHang.findChiTietBySanPhamId(item.getSanPhamChiTiet().getSanPham());
                    if(existingItem != null){
                        existingItem.setSoLuong(existingItem.getSoLuong() + item.getSoLuong());

                    }else{
                        gioHangKhachHang.addChiTiet(item);
                    }
                }
                gioHangRepo.save(gioHangKhachHang);

                //Xóa giỏ hàng tạm thời
                gioHangRepo.delete(gioHangTamThoi);
            }
        }
    }

    public GioHang getGioHangBySessionId(String sessionId){
        return gioHangRepo.findByMaGioHang(sessionId).orElseGet(() -> {
            //Nếu không tìm thấy, tạo giỏ hàng mới về sessionId
            GioHang gioHangMoi = new GioHang();
            gioHangMoi.setMaGioHang(sessionId);
            gioHangMoi.setNgayTao(LocalDate.now());
            gioHangMoi.setTrangThai(1);
            gioHangRepo.save(gioHangMoi);
            return gioHangMoi;
        });
    }
    public GioHang getGioHangByUserId(int userId){
        return gioHangRepo.findByKhachHangId(userId).orElseGet(() -> {
           //nếu không tìm thây, tạo giỏ hàng mới cho user
           KhachHang khachHang = khachHangRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với Id "+userId));
           GioHang gioHangMoi = new GioHang();
           gioHangMoi.setKhachHang(khachHang);
           gioHangMoi.setMaGioHang("CART_USER"+userId);
           gioHangMoi.setNgayTao(LocalDate.now());
           gioHangMoi.setTrangThai(1);
           gioHangRepo.save(gioHangMoi);
           return gioHangMoi;
        });
    }
    @Transactional
    public void delete(GioHang gioHang){
        gioHangChiTietRepo.deleteGioHangChiTietByGioHangId(gioHang.getId());
        gioHangRepo.delete(gioHang);
    }
    public void xoaSanPhamTrongGioHang(int idGioHang){
        List<GioHangChiTiet> gioHangChiTiets = gioHangChiTietRepo.findByGioHangId(idGioHang);
        gioHangChiTietRepo.deleteAll(gioHangChiTiets);
    }
}
