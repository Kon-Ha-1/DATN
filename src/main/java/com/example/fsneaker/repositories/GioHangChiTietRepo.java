package com.example.fsneaker.repositories;

import com.example.fsneaker.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GioHangChiTietRepo extends JpaRepository<GioHangChiTiet, Integer> {
    @Query("SELECT g FROM GioHangChiTiet g WHERE g.gioHang.id = :idGioHang AND g.sanPhamChiTiet.sanPham.id = :idSanPham AND g.sanPhamChiTiet.kichThuoc.id = :idKichThuoc AND g.sanPhamChiTiet.mauSac.id = :idMauSac")
    GioHangChiTiet findByGioHangIdAndSanPhamIdAndKichThuocIdAndMauSacId(@Param("idGioHang") Integer idGioHang,
                                                                        @Param("idSanPham")Integer idSanPham,
                                                                        @Param("idKichThuoc")Integer idKichThuoc,
                                                                        @Param("idMauSac") Integer idMauSac);
    List<GioHangChiTiet> findByGioHangId(Integer idGioHang);
    @Query("SELECT COALESCE(SUM(g.soLuong),0) FROM GioHangChiTiet g WHERE g.gioHang.id = :idGioHang")
    int demTongSoLuongTrongGioHang(@Param("idGioHang") Integer idGioHang);

    GioHangChiTiet findByGioHangIdAndSanPhamChiTietId(Integer idGioHang, Integer idSanPhamChiTiet);
    @Query("SELECT SUM(ghct.soLuong * coalesce(ghct.sanPhamChiTiet.giaBanGiamGia, ghct.sanPhamChiTiet.giaBan)) FROM GioHangChiTiet ghct WHERE ghct.gioHang.id = :gioHangId")
    BigDecimal sumGiaByGioHangId(@Param("gioHangId")Integer gioHangId);

    void deleteGioHangChiTietByGioHangId(Integer idGioHang);
}
