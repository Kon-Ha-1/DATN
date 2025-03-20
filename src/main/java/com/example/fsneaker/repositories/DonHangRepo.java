package com.example.fsneaker.repositories;

import com.example.fsneaker.entity.DonHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonHangRepo extends JpaRepository<DonHang, Integer> {

    @Query("SELECT dh FROM DonHang dh WHERE dh.trangThai = :trangThai AND YEAR(dh.ngayMua) = :nam")
    List<DonHang> findByTrangThaiAndNam(@Param("trangThai")String trangThai, @Param("nam")int nam);
    List<DonHang> findByTrangThai(String trangThai);

    //    DonHang findById(int id);
    //Đếm số đơn hàng đã thanh toán
    @Query("SELECT COUNT(dh) FROM DonHang dh WHERE dh.trangThai = :trangThai And YEAR(dh.ngayMua) = :nam")
    Long countByTrangThaiAndNam(@Param("trangThai")String trangThai,@Param("nam")int nam);
    DonHang findByKhachHangId(Integer idKhachHang);
    //Lấy tất cả các đơn hàng đã thanh toán trong vòng 1 tuần qua
    List<DonHang> findByNgayMuaBetweenAndTrangThaiEquals(LocalDate startDate, LocalDate endDate, String trangThai);

    @Query("SELECT dh.trangThai, COUNT(dh) FROM DonHang dh GROUP BY dh.trangThai")
    List<Object[]> demDonHangTheoTrangThai();
    //Lấy tổng thu nhập trong tháng hiện tại
    @Query("SELECT FUNCTION('DAY',dh.ngayMua), SUM(COALESCE(dh.tongTienGiamGia,dh.tongTien)) FROM DonHang dh WHERE dh.trangThai = :trangThai AND MONTH(dh.ngayMua) = MONTH(CURRENT_DATE ) AND YEAR(dh.ngayMua) = YEAR(CURRENT_DATE ) GROUP BY FUNCTION('DAY',dh.ngayMua)")
    List<Object[]> findThuNhapTheoThang(@Param("trangThai")String trangThai);
    //Lấy số lượng đơn hàng trong tháng hiện tại
    @Query("SELECT FUNCTION('DAY',dh.ngayMua), COUNT(dh) FROM DonHang dh WHERE dh.trangThai = :trangThai AND MONTH(dh.ngayMua) = MONTH(CURRENT_DATE ) AND YEAR(dh.ngayMua) = YEAR(CURRENT_DATE) GROUP BY FUNCTION('DAY',dh.ngayMua)")
    List<Object[]> findDonHangTheoThang(@Param("trangThai")String trangThai);
    //Lấy tổng thu nhập trong năm hiện tại
    @Query("SELECT FUNCTION('MONTH',dh.ngayMua), SUM(COALESCE(dh.tongTienGiamGia,dh.tongTien)) FROM DonHang dh WHERE YEAR(dh.ngayMua) = YEAR(CURRENT_DATE ) GROUP BY FUNCTION('MONTH',dh.ngayMua)")
    List<Object[]> findThuNhapTheoNam();
    //Lấy số lượng đơn hàng trong năm hiện tại
    @Query("SELECT FUNCTION('MONTH',dh.ngayMua), COUNT(dh) FROM DonHang dh WHERE YEAR(dh.ngayMua) = YEAR(CURRENT_DATE) GROUP BY FUNCTION('MONTH',dh.ngayMua)")
    List<Object[]> findDonHangTheoNam();
    @Query("SELECT dh.khachHang.tenKhachHang,dh.khachHang.email,dh.khachHang.soDienThoai, count(dh.id) as soLanMua,SUM(COALESCE(dh.tongTienGiamGia,dh.tongTien)) as tongTienMua from DonHang dh WHERE dh.trangThai = :trangThai AND dh.ngayMua BETWEEN :startDate AND :endDate GROUP BY dh.khachHang.tenKhachHang ,dh.khachHang.email,dh.khachHang.soDienThoai ORDER BY soLanMua DESC")
    Page<Object[]> thongKeKhachHangTheoSoLanMua(@Param("trangThai") String trangThai,@Param("startDate") LocalDate startDate, @Param("endDate")LocalDate endDate, Pageable pageable);
    @Query("SELECT dh.khachHang.tenKhachHang,dh.khachHang.email,dh.khachHang.soDienThoai, count(dh.id) as soLanMua,SUM(COALESCE(dh.tongTienGiamGia,dh.tongTien)) as tongTienMua from DonHang dh WHERE dh.trangThai = :trangThai AND dh.ngayMua BETWEEN :startDate AND :endDate GROUP BY dh.khachHang.tenKhachHang ,dh.khachHang.email,dh.khachHang.soDienThoai ORDER BY tongTienMua DESC")
    Page<Object[]> thongKeKhachHangTheoTongTien(@Param("trangThai")String trangThai, @Param("startDate") LocalDate startDate, @Param("endDate")LocalDate endDate, Pageable pageable);
    @Query("SELECT dh.khachHang.tenKhachHang,dh.khachHang.email,dh.khachHang.soDienThoai, count(dh.id) as soLanMua,SUM(COALESCE(dh.tongTienGiamGia,dh.tongTien)) as tongTienMua from DonHang dh WHERE dh.trangThai = :trangThai GROUP BY dh.khachHang.tenKhachHang ,dh.khachHang.email,dh.khachHang.soDienThoai ORDER BY soLanMua DESC")
    Page<Object[]> thongKeKHTheoSoLanMua(@Param("trangThai")String trangThai, Pageable pageable);
    @Query("SELECT dh.khachHang.tenKhachHang,dh.khachHang.email,dh.khachHang.soDienThoai, count(dh.id) as soLanMua,SUM(COALESCE(dh.tongTienGiamGia,dh.tongTien)) as tongTienMua from DonHang dh WHERE dh.trangThai = :trangThai GROUP BY dh.khachHang.tenKhachHang ,dh.khachHang.email,dh.khachHang.soDienThoai ORDER BY tongTienMua DESC")
    Page<Object[]> thongKeKHTheoTongTien(@Param("trangThai")String trangthai, Pageable pageable);
    DonHang findByMaDonHang(String maDonHang);

    //Tính tổng thu nhập trong khoảng thời gian
    @Query("SELECT SUM(coalesce(d.tongTienGiamGia,d.tongTien)) FROM DonHang d WHERE d.trangThai = :trangThai AND d.ngayMua BETWEEN :startDate AND :endDate")
    BigDecimal tinhTongThuNhap(@Param("trangThai")String trangThai,@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);

    //Đếm số lượng khách hàng duy nhất trong khoảng thời gian
    @Query("SELECT COUNT(distinct d.khachHang.id) FROM DonHang d WHERE d.trangThai = :trangThai AND d.ngayMua BETWEEN :startDate AND :endDate")
    Long tinhTongKhachHang(@Param("startDate")LocalDate startDate,LocalDate endDate, @Param("trangThai")String trangThai);

    //Đếm số đơn hàng trong khoảng thời gian
    @Query("SELECT COUNT(d.id) FROM DonHang d WHERE d.trangThai = :trangThai AND d.ngayMua between :startDate AND :endDate")
    Long tinhTongDonHang(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate, @Param("trangThai")String trangThai);

    //Tính tổng số sản phẩm đã bán trong khoảng thời gian
    @Query("SELECT SUM(chiTiet.soLuong) FROM DonHang d JOIN d.donHangChiTiets chiTiet WHERE d.trangThai = :trangThai AND d.ngayMua BETWEEN :startDate AND :endDate")
    Long tinhTongSanPhamDaBan(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate, @Param("trangThai")String trangThai);

    @Query("SELECT dh FROM DonHang  dh WHERE dh.khachHang.id = :idKhachHang AND dh.trangThai = :trangThai AND dh.loaiDonHang = :loaiDonHang")
    DonHang findByKhachHangAndTrangThaiAndLoaiDonHang(@Param("idKhachHang") Integer idKhachHang,@Param("trangThai") String trangThai,@Param("loaiDonHang") String loaiDonHang);

    /////////
    @Query("select o from DonHang o where (?1 is null or (o.maDonHang like ?1 or o.khachHang.tenKhachHang like ?1 or o.khachHang.soDienThoai like ?1))" +
            " and (?2 is null or o.ngayTao >=?2) " +
            " and (?3 is null or o.ngayTao <=?3) " +
            " order by o.id desc ")
    public Page<DonHang> searchPageHoaDon(String keyword, LocalDateTime startDate, LocalDateTime endDate, PageRequest p);

    @Query("select o from DonHang o where (?1 is null or (o.maDonHang like ?1 or o.khachHang.tenKhachHang like ?1 or o.khachHang.soDienThoai like ?1))" +
            " and (?2 is null or o.ngayTao >=?2) " +
            " and (?3 is null or o.ngayTao <=?3) " +
            " and (?4 is null or o.trangThai = ?4) " +
            " order by o.id desc ")
    public Page<DonHang> searchPageHoaDonfindStatus(String keyword, LocalDateTime startDate, LocalDateTime endDate, String status, PageRequest p);
    @Query("select o from DonHang o where (?1 is null or (o.maDonHang like ?1 or o.khachHang.tenKhachHang like ?1 or o.khachHang.soDienThoai like ?1))" +
            " and (?2 is null or o.ngayTao >=?2) " +
            " and (?3 is null or o.ngayTao <=?3) " +
            " and (?4 is null or o.trangThai in (?4)) " +
            " order by o.id desc ")
    public Page<DonHang> searchPageHoaDonfindListStatus(String keyword, LocalDateTime startDate, LocalDateTime endDate, List<String> status, PageRequest p);

  //anh2108
  @Query("SELECT d FROM DonHang d LEFT JOIN FETCH d.donHangChiTiets chiTiet LEFT JOIN FETCH chiTiet.sanPhamChiTiet" +
          " WHERE d.trangThai = ?1 and d.khachHang.id = ?2 order by d.ngayTao desc")
  List<DonHang> findAllWithDetailsByTrangThai( String trangThai, Integer idKhachHang);

    @Query("SELECT d FROM DonHang d LEFT JOIN FETCH d.donHangChiTiets chiTiet LEFT JOIN FETCH chiTiet.sanPhamChiTiet" +
            " WHERE d.trangThai in (?1) and d.khachHang.id = ?2 order by d.ngayTao desc")
    List<DonHang> findAllDetailsByTrangThai( List<String> status, Integer idKhachHang);
}
