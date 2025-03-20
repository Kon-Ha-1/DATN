package com.example.fsneaker.service;

import com.example.fsneaker.entity.DonHang;
import com.example.fsneaker.entity.Voucher;
import com.example.fsneaker.repositories.VoucherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class VoucherService {
    //Chỗ này là của trướng nhóm code cấm đụng vô
    @Autowired
    private VoucherRepo voucherRepo;
    public Voucher getVoucherById(int id){
        return voucherRepo.findById(id).orElse(null);
    }
    public Voucher getByIdVoucher(int idVoucher){
        return  voucherRepo.findById(idVoucher).orElseThrow(() -> new IllegalArgumentException("Mã voucher không hợp lệ: " + idVoucher));
    }
    public Page<Voucher> searchVoucher(String keyword, LocalDate ngayBatDau, LocalDate ngayKetThuc, Integer trangThai, PageRequest pageable){
        return voucherRepo.searchPage(keyword,ngayBatDau,ngayKetThuc,trangThai,pageable);
    }
    public Voucher themVc(Voucher voucher, String tenNguoiDung){
        Date now = new Date();
        if(voucher.getId() == 0){
            voucher.setNgayTao(now);
            voucher.setNguoiTao(tenNguoiDung);
        }
        voucher.setNgaySua(now);
        voucher.setNguoiSua(tenNguoiDung);
        return voucherRepo.save(voucher);
    }
    public BigDecimal applyVoucher(DonHang donHang, Voucher voucher) {
        if (donHang == null || voucher == null) {
            throw new IllegalArgumentException("Đơn hàng hoặc voucher không hợp lệ!");
        }
        BigDecimal tongTien = donHang.getTongTien(); // Lấy tổng tiền của đơn hàng dưới dạng BigDecimal
        BigDecimal tongTienGiamGia = tongTien;

        if (voucher.getLoaiVoucher().equalsIgnoreCase("Giảm giá %")) {
            tongTienGiamGia = tongTien.subtract(tongTien.multiply(voucher.getGiaTri().divide(BigDecimal.valueOf(100))));
        } else if (voucher.getLoaiVoucher().equalsIgnoreCase("Giảm giá số tiền")) {
            tongTienGiamGia = tongTien.subtract(voucher.getGiaTri());
        } else {
            throw new IllegalArgumentException("Loại giảm giá không hợp lệ: " + voucher.getLoaiVoucher());
        }

        // Đảm bảo tổng tiền không âm
        return tongTienGiamGia.max(BigDecimal.ZERO);
    }
    //    public double calculateDiscount(Voucher voucher, double totalAmount){
//        if(voucher.getLoaiVoucher().equals("Giảm giá %")){
//            return totalAmount * (voucher.getGiaTri() / 100);
//        }else if(voucher.getLoaiVoucher().equals("Giảm giá số tiền")){
//            return Math.min(voucher.getGiaTri(), totalAmount);
//        }
//        return 0;
//    }
    public List<Voucher> getAllVoucherByTrangThaiAndGiaTri(Integer trangThai){
        return voucherRepo.findAllVoucherByTrangThaiAndAndGiaTri(trangThai);
    }
    public Voucher themVoucher(Voucher voucher){
        return voucherRepo.save(voucher);
    }
    //Ai code thì cOde vô đây
}
