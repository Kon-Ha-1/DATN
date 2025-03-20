package com.example.fsneaker.service;

import com.example.fsneaker.entity.GioHang;
import com.example.fsneaker.entity.GioHangChiTiet;
import com.example.fsneaker.repositories.GioHangChiTietRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GioHangChiTietService {
    @Autowired
    private GioHangChiTietRepo gioHangChiTietRepo;
    public GioHangChiTiet saveGioHangChiTiet(GioHangChiTiet gioHangChiTiet){
        return gioHangChiTietRepo.save(gioHangChiTiet);
    }
    public void deleteGioHangChiTiet(GioHangChiTiet gioHangChiTiet){
        gioHangChiTietRepo.delete(gioHangChiTiet);
    }
    public GioHangChiTiet getByGioHangIdAndSanPhamIdAndKichThuocIdAndMauSacId(Integer idGioHang, Integer idSanPham, Integer idKichThuoc, Integer idMauSac){
        return  gioHangChiTietRepo.findByGioHangIdAndSanPhamIdAndKichThuocIdAndMauSacId(idGioHang,idSanPham,idKichThuoc,idMauSac);
    }
    public List<GioHangChiTiet> getByGioHangId(Integer idGioHang){
        return gioHangChiTietRepo.findByGioHangId(idGioHang);
    }
    public int getSoLuongTrongGioHang(Integer idGioHang){
        return gioHangChiTietRepo.demTongSoLuongTrongGioHang(idGioHang);
    }
    public void mergeCart (GioHang tempGioHang , GioHang userGioHang){
        List<GioHangChiTiet> tempItems = gioHangChiTietRepo.findByGioHangId(tempGioHang.getId());
        for(GioHangChiTiet item : tempItems){
            GioHangChiTiet existingItem = gioHangChiTietRepo.findByGioHangIdAndSanPhamChiTietId(userGioHang.getId(),item.getSanPhamChiTiet().getId());
            if(existingItem != null){
                //Cập nhật số lượng
                existingItem.setSoLuong(existingItem.getSoLuong() + item.getSoLuong());
                gioHangChiTietRepo.save(existingItem);
            }else{
                //Chuyển sản phẩm sang giỏ hàng mới
                item.setGioHang(userGioHang);
                gioHangChiTietRepo.save(item);
            }
        }
    }

    public BigDecimal tinhTongTien(Integer idGioHang){
        BigDecimal tongTien = gioHangChiTietRepo.sumGiaByGioHangId(idGioHang);
        return tongTien != null ? tongTien : BigDecimal.valueOf(0.0);
    }
}
