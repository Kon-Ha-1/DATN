package com.example.fsneaker.repositories;

import com.example.fsneaker.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepo extends JpaRepository<GioHang, Integer> {
    Optional<GioHang> findByMaGioHang(String maGioHang);//Tìm giỏ hàng theo mã
    Optional<GioHang> findByKhachHangId(Integer idKhachHang);

    @Query("SELECT g FROM GioHang g WHERE g.khachHang.email = :email OR g.khachHang.soDienThoai = :soDienThoai")
    GioHang findByUsername(@Param("email")String email,@Param("soDienThoai")String soDienThoai);
}
