package com.example.fsneaker.service;

import com.example.fsneaker.dto.CustomUserDetails;
import com.example.fsneaker.entity.KhachHang;
import com.example.fsneaker.entity.NhanVien;
import com.example.fsneaker.repositories.KhachHangRepo;
import com.example.fsneaker.repositories.NhanVienRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private NhanVienRepo nhanVienRepo;

    @Autowired
    private KhachHangRepo khachHangRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NhanVien nhanVien = nhanVienRepo.findByEmailOrSoDienThoai(username,username);
        if(nhanVien != null){
            return new CustomUserDetails(
                    nhanVien.getId(),
                    nhanVien.getEmail(),
                    nhanVien.getMatKhau(),
                    nhanVien.getTenNhanVien(),
                    nhanVien.getDiaChi(),
                    nhanVien.getSoDienThoai(),
                    nhanVien.getEmail(),
                    nhanVien.getVaiTro() == 0  ? "ROLE_ADMIN" : nhanVien.getVaiTro() == 1 ? "ROLE_MANAGER" : "ROLE_STAFF"
            );
        }
        KhachHang khachHang= khachHangRepo.findBySoDienThoaiOrEmail(username,username);
        if(khachHang != null){
            return new CustomUserDetails(
                    khachHang.getId(),
                    khachHang.getEmail(),
                    khachHang.getMatKhau(),
                    khachHang.getTenKhachHang(),
                    khachHang.getDiaChi(),
                    khachHang.getSoDienThoai(),
                    khachHang.getEmail(),
                    "ROLE_CUSTOMER"
            );
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
