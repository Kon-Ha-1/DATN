package com.example.fsneaker.repositories;

import com.example.fsneaker.entity.DonHang;
import com.example.fsneaker.entity.DonHangChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface DonHangChiTietRepo extends JpaRepository<DonHangChiTiet, Integer> {
    //Chô này là code của trưởng nhóm câm đụng
    List<DonHangChiTiet> findAllByDonHangId(int id);
    //Kiểm tra nếu sản phẩm đã có trong hóa đơn
    DonHangChiTiet findByDonHangIdAndSanPhamChiTietId(int idDonHang, int idSanPhamChiTiet);
    @Query("SELECT c.sanPhamChiTiet.sanPham.id ,c.sanPhamChiTiet.sanPham.tenSanPham,c.sanPhamChiTiet.ngayTao, c.sanPhamChiTiet.mauSac.tenMauSac, c.sanPhamChiTiet.kichThuoc.tenKichThuoc ,MAX(c.gia),SUM(c.soLuong), MAX(c.sanPhamChiTiet.soLuong), c.sanPhamChiTiet.imanges FROM DonHangChiTiet c WHERE c.donHang.trangThai = :trangThai GROUP BY c.sanPhamChiTiet.sanPham.id, c.sanPhamChiTiet.id,c.sanPhamChiTiet.sanPham.tenSanPham,c.sanPhamChiTiet.ngayTao, c.sanPhamChiTiet.mauSac.tenMauSac, c.sanPhamChiTiet.kichThuoc.tenKichThuoc, c.sanPhamChiTiet.imanges  ORDER BY SUM(c.soLuong) DESC")
    Page<Object[]> findBestSellingProducts(@Param("trangThai")String trangThai, Pageable pageable);
    @Query("SELECT c.sanPhamChiTiet.sanPham.id ,c.sanPhamChiTiet.sanPham.tenSanPham,c.sanPhamChiTiet.ngayTao, c.sanPhamChiTiet.mauSac.tenMauSac, c.sanPhamChiTiet.kichThuoc.tenKichThuoc ,c.gia, SUM(c.soLuong), c.sanPhamChiTiet.soLuong, c.sanPhamChiTiet.imanges FROM DonHangChiTiet c WHERE c.donHang.trangThai = :trangThai GROUP BY c.sanPhamChiTiet.sanPham.id,c.sanPhamChiTiet.sanPham.tenSanPham,c.sanPhamChiTiet.ngayTao, c.sanPhamChiTiet.mauSac.tenMauSac, c.sanPhamChiTiet.kichThuoc.tenKichThuoc ,c.gia, c.sanPhamChiTiet.soLuong,c.sanPhamChiTiet.imanges ORDER BY (c.gia * SUM(c.soLuong)) DESC")
    Page<Object[]> sanPhamDoanhThuCaoNhat(@Param("trangThai")String trangThai, Pageable pageable);
    @Query("SELECT c.sanPhamChiTiet.sanPham.id ,c.sanPhamChiTiet.sanPham.tenSanPham,c.sanPhamChiTiet.ngayTao, c.sanPhamChiTiet.mauSac.tenMauSac, c.sanPhamChiTiet.kichThuoc.tenKichThuoc ,MAX(c.gia),SUM(c.soLuong), MAX(c.sanPhamChiTiet.soLuong), c.sanPhamChiTiet.imanges FROM DonHangChiTiet c WHERE c.donHang.trangThai = :trangThai AND c.donHang.ngayMua BETWEEN :startDate AND :endDate GROUP BY c.sanPhamChiTiet.sanPham.id, c.sanPhamChiTiet.id,c.sanPhamChiTiet.sanPham.tenSanPham,c.sanPhamChiTiet.ngayTao, c.sanPhamChiTiet.mauSac.tenMauSac, c.sanPhamChiTiet.kichThuoc.tenKichThuoc, c.sanPhamChiTiet.imanges  ORDER BY SUM(c.soLuong) DESC")
    Page<Object[]> sanPhamBanChayTheoNgay(@Param("trangThai")String trangThai,@Param("startDate") LocalDate startDate,@Param("endDate")LocalDate endDate, Pageable pageable);
    @Query("SELECT c.sanPhamChiTiet.sanPham.id ,c.sanPhamChiTiet.sanPham.tenSanPham,c.sanPhamChiTiet.ngayTao, c.sanPhamChiTiet.mauSac.tenMauSac, c.sanPhamChiTiet.kichThuoc.tenKichThuoc ,c.gia, SUM(c.soLuong), c.sanPhamChiTiet.soLuong, c.sanPhamChiTiet.imanges FROM DonHangChiTiet c WHERE c.donHang.trangThai = :trangThai AND c.donHang.ngayMua BETWEEN :startDate AND :endDate GROUP BY c.sanPhamChiTiet.sanPham.id,c.sanPhamChiTiet.sanPham.tenSanPham,c.sanPhamChiTiet.ngayTao, c.sanPhamChiTiet.mauSac.tenMauSac, c.sanPhamChiTiet.kichThuoc.tenKichThuoc ,c.gia, c.sanPhamChiTiet.soLuong,c.sanPhamChiTiet.imanges ORDER BY (c.gia * SUM(c.soLuong)) DESC")
    Page<Object[]> sanPhamDoanhThuCaoNhatTheoNgay(@Param("trangThai")String trangThai,@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate, Pageable pageable);
    //Tìm tất cả các chi tiết đơn hàng theo DonHang
    List<DonHangChiTiet> findByDonHang(DonHang donHang);

    //Truy vấn số lượng sản phẩm bán được theo thương hiệu
    @Query("SELECT Min(dhct.sanPhamChiTiet.id) ,dhct.sanPhamChiTiet.sanPham.tenSanPham, dhct.sanPhamChiTiet.giaBan, coalesce(dhct.sanPhamChiTiet.giaBanGiamGia, NULL), dhct.sanPhamChiTiet.mauSac.tenMauSac, SUM(dhct.soLuong) as totalQuantity, dhct.sanPhamChiTiet.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri FROM DonHangChiTiet dhct LEFT JOIN dhct.sanPhamChiTiet.sanPham.khuyenMai khuyenMai WHERE dhct.sanPhamChiTiet.sanPham.thuongHieu.id = :id GROUP BY dhct.sanPhamChiTiet.sanPham.tenSanPham, dhct.sanPhamChiTiet.giaBan,dhct.sanPhamChiTiet.giaBanGiamGia, dhct.sanPhamChiTiet.mauSac.tenMauSac , dhct.sanPhamChiTiet.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProductsByBrand(@Param("id") Integer id, Pageable pageable);

    DonHangChiTiet findByDonHangIdAndSanPhamChiTietId(Integer idDonHang, Integer idSanPhamChiTiet);

    @Query("SELECT  Min(dhct.sanPhamChiTiet.id), dhct.sanPhamChiTiet.sanPham.tenSanPham, dhct.sanPhamChiTiet.giaBan,coalesce(dhct.sanPhamChiTiet.giaBanGiamGia,NULL), dhct.sanPhamChiTiet.mauSac.tenMauSac,dhct.sanPhamChiTiet.imanges,khuyenMai.loaiKhuyenMai, khuyenMai.giaTri, SUM(dhct.soLuong) FROM DonHangChiTiet dhct LEFT JOIN dhct.sanPhamChiTiet.sanPham.khuyenMai khuyenMai WHERE dhct.sanPhamChiTiet.sanPham.thuongHieu.id = :idThuongHieu GROUP BY dhct.sanPhamChiTiet.sanPham.tenSanPham, dhct.sanPhamChiTiet.giaBan, dhct.sanPhamChiTiet.giaBanGiamGia, dhct.sanPhamChiTiet.mauSac.tenMauSac,dhct.sanPhamChiTiet.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri ORDER BY SUM(dhct.soLuong) DESC")
    Page<Object[]> findNikeByPopularity(@Param("idThuongHieu")Integer idThuongHieu, Pageable pageable);

    void deleteDonHangChiTietByDonHangId(Integer idDonHang);
    //Từ chỗ này đi ai code của ai thì note lại tên tránh nhầm lẫn
    //+
    @Query("select o from DonHangChiTiet o where o.donHang.id = ?1 ")
    List<DonHangChiTiet> donHangDetail(Integer idDonHang);

}

