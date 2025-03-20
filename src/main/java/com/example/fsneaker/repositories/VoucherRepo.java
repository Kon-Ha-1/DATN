package com.example.fsneaker.repositories;

import com.example.fsneaker.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface VoucherRepo extends JpaRepository<Voucher,Integer> {
    @Query("select o from Voucher o where (:keyword is null or (o.loaiVoucher like %:keyword% or o.maVoucher like %:keyword% or o.tenVoucher like %:keyword%))" +
            " and (:ngayBatDau is null or o.ngayBatDau >= :ngayBatDau) " +
            " and (:ngayKetThuc is null or o.ngayKetThuc <= :ngayKetThuc) " +
            " and (:trangThai is null or o.trangThai = :trangThai) " +
            " order by o.ngaySua desc,o.ngayTao desc ")
    public Page<Voucher> searchPage(@Param("keyword") String keyword, @Param("ngayBatDau") LocalDate ngayBatDau, @Param("ngayKetThuc")LocalDate ngayKetThuc, @Param("trangThai") Integer trangThai, PageRequest p);

    List<Voucher> findByNgayBatDauAfter(LocalDateTime today);

    List<Voucher> findByNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(LocalDateTime today, LocalDateTime today2);

    List<Voucher> findByNgayKetThucBefore(LocalDateTime today);


    //Chỗ này là của trướng nhóm code cấm đụng vào
    //Voucher findById(int idVoucher); //Tìm voucher theo mã;
    @Query("SELECT v FROM Voucher v WHERE v.trangThai = :trangThai ORDER BY v.giaTri DESC")
    List<Voucher> findAllVoucherByTrangThaiAndAndGiaTri(@Param("trangThai") Integer trangThai);

    Voucher findByMaVoucher(String maVoucher);
}
