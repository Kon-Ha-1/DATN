package com.example.fsneaker.service;


import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.entity.NhanVien;
import com.example.fsneaker.repositories.KhachHangRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
public class KhachHangService {


    @Autowired
    private KhachHangRepo khachHangRepo;

    //public List<KhachHang> getAllKhachHang(){
//        return khachHangRepo.findAll();
//    }

    public KhachHang themKH(KhachHang khachHang, String tenNguoiDung){
        Date now = new Date();
        if(khachHang.getId() == 0){
            khachHang.setNgayTao(now);
            khachHang.setNguoiTao(tenNguoiDung);
        }
        khachHang.setNgaySua(now);
        khachHang.setNguoiSua(tenNguoiDung);
        return khachHangRepo.save(khachHang);
    }


    public KhachHang getKhachHangById(int id){
        return khachHangRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Mã khách hàng không hợp lệ: " + id));
    }
    //    public List<KhachHang> timKiemKhachHang(String keyword){
//        return khachHangRepo.findByTenKhachHangContainingOrEmailContainingOrSoDienThoaiContaining(keyword, keyword,keyword);
//    }
//    public KhachHang getKhachHangBySoDienThoai(String sdt){
//        return khachHangRepo.findBySoDienThoai(sdt);
//    }
    public String taoMaKhachHang(){
        String format = "00000";
        String part = String.format("%05d", new Random().nextInt(1000));
        return "KH" + part;
    }

    public Page<KhachHang> searchPaginated(String keyword, int page , int size){
        return khachHangRepo.searchByKhachHang(keyword, PageRequest.of(page,size));
    }
    public Page<KhachHang> findPaginated(int page, int  size){
        return khachHangRepo.findAll(PageRequest.of(page,size));
    }
    public long countTotalKhachHang(int nam){
        return khachHangRepo.countByNam(nam);
    }

    //Chỗ này là code của trưởng nhóm code cấm đụng vào
    public KhachHang timKiemTheoSoDienThoaiHoacEmail(String keyword){
        return khachHangRepo.findBySoDienThoaiOrEmail(keyword,keyword);
    }

    public KhachHang authenticateUser(String username, String password) {
        //Tìm người dùng theo username
        Optional<KhachHang> khachHangOptional = khachHangRepo.findByEmailOrSoDienThoai(username, username);

        if (khachHangOptional.isPresent()) {
            KhachHang khachHang = khachHangOptional.get();
            //Kiểm tra mật khẩu
            if (password.equals(khachHang.getMatKhau())) {
                return khachHang;
            }
        }
        return null;
    }
    public KhachHang getKhachHangByUserName(String username) {
        return khachHangRepo.findByEmailOrSoDienThoai(username, username).orElse(null);
    }

    // Tìm khách hàng theo email
    public KhachHang findByEmail(String email) {
        return khachHangRepo.findByEmail(email);
    }

    // Tìm khách hàng theo ID
    public KhachHang findById(Integer id) {
        Optional<KhachHang> khachHang = khachHangRepo.findById(id);
        return khachHang.orElse(null);
    }
    // Lưu nhân viên vào cơ sở dữ liệu
    public KhachHang save(KhachHang khachHang) {
        return khachHangRepo.save(khachHang);
    }
    public KhachHang findByResetToken(String token){
        return khachHangRepo.findByResetToken(token);
    }
    public boolean existsByMaKhachHang(String maKhachHang){
        return khachHangRepo.existsByMaKhachHang(maKhachHang);
    }
    public boolean isDuplicationMaKhachHang(Integer id, String maKhachHang){
        KhachHang existing = khachHangRepo.findByMaKhachHang(maKhachHang);
        return existing != null && existing.getId() != id;
    }

    public boolean existsBySoDienThoai(String soDienThoai){
        return khachHangRepo.existsBySoDienThoai(soDienThoai);
    }
    public boolean isDuplicationSoDienThoai(Integer id,String soDienThoai){
        KhachHang existing = khachHangRepo.findBySoDienThoai(soDienThoai);
        return existing != null && existing.getId() != id;
    }

    public boolean existsByEmail(String email){
        return khachHangRepo.existsByEmail(email);
    }

    public boolean isDuplicationEmail(Integer id, String email){
        KhachHang existing = khachHangRepo.findByEmail(email);
        return existing != null && existing.getId() != id;
    }
}
