package com.example.fsneaker.repositories;

import com.example.fsneaker.entity.KhuyenMai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhuyenMaiRepo extends JpaRepository<KhuyenMai,Integer> {
    // tìm kiếm theo  tên khuyến mại
    @Query("SELECT k FROM KhuyenMai k WHERE " +
            "(:keyword IS NULL OR k.tenKhuyenMai LIKE %:keyword% OR k.maKhuyenMai LIKE %:keyword% OR k.loaiKhuyenMai LIKE %:keyword%) " +
            "AND (:ngayBatDau IS NULL OR k.ngayBatDau >= :ngayBatDau) " +
            "AND (:ngayKetThuc IS NULL OR k.ngayKetThuc <= :ngayKetThuc)"+
            "AND (:trangThai IS NULL OR k.trangThai = :trangThai) ORDER BY k.ngaySua DESC, k.ngayTao DESC"
    )

    Page<KhuyenMai> searchByKeywordAndDate(@Param("keyword") String keyword,
                                           @Param("ngayBatDau") LocalDate ngayBatDau,
                                           @Param("ngayKetThuc") LocalDate ngayKetThuc,
                                           @Param("trangThai") Integer trangThai,
                                           Pageable pageable);

    KhuyenMai findByMaKhuyenMai(String maKhuyenMai);

    // Truy vấn để sắp xếp theo trạng thái trước (Hoạt động lên đầu)
    @Query("SELECT k FROM KhuyenMai k ORDER BY k.ngaySua DESC, k.ngayTao DESC")
    Page<KhuyenMai> findAllWithSorting(Pageable pageable);
    @Query("SELECT km FROM KhuyenMai km WHERE km.trangThai = :trangThai")
    List<KhuyenMai> findByIdAndTrangThai(@Param("trangThai")int trangThai);

}
