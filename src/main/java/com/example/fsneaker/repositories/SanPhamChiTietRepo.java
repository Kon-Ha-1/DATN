package com.example.fsneaker.repositories;

import com.example.fsneaker.entity.SanPham;
import com.example.fsneaker.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@Repository
public interface SanPhamChiTietRepo extends JpaRepository<SanPhamChiTiet,Integer> {


    //Lệnh thực hiện update phía sản phẩm
    @Modifying
    @Transactional
    @Query("""
        UPDATE SanPhamChiTiet chiTiet 
        SET chiTiet.giaBanGiamGia = 
            CASE 
                WHEN :tyLeGiamGia <= 100 THEN chiTiet.giaBan - (chiTiet.giaBan * :tyLeGiamGia / 100)
                ELSE chiTiet.giaBan - :tyLeGiamGia 
            END
        WHERE chiTiet.sanPham = :sanPham
    """)
    int updateGiaBanGiamGiaBySanPham(
            @Param("sanPham") SanPham sanPham,
            @Param("tyLeGiamGia") BigDecimal tyLeGiamGia
    );

    //Lấy sản phẩm chi tiết qua SanPham
    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham = :sanPham ORDER BY spct.ngaySua DESC , spct.ngayTao DESC ")
    List<SanPhamChiTiet> findChiTietBySanPham(@Param("sanPham") SanPham sanPham);


    @Query("select e from SanPhamChiTiet e where e.id = :id")
    public SanPhamChiTiet findById(int id);

    //lấy nhiều sản phẩm chi tiết
    @Query("select spct,sp.tenSanPham, ms.tenMauSac,kt.tenKichThuoc from SanPhamChiTiet spct " +
            "join spct.sanPham sp " +
            "join  spct.mauSac ms " +
            "join spct.kichThuoc kt " +
            "where spct.id = :spctId order by spct.ngayTao desc ")
    public List<SanPhamChiTiet> findBySanPhamChitietId(int spctId);

    // Lấy một sản phẩm chi tiết
    @Query("select spct, spct.ngaySanXuat,sp.tenSanPham, ms.tenMauSac,kt.tenKichThuoc from SanPhamChiTiet spct " +
            "join spct.sanPham sp " +
            "join  spct.mauSac ms " +
            "join spct.kichThuoc kt " +
            "where spct.id = :id ")
    public SanPhamChiTiet findBySanPhamChitietI2d(int id);


    //lấy giá trị lớn nhất để sinh ra mã tự động
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(maSanPhamChiTiet, 5) AS integer)), 0) FROM SanPhamChiTiet")
    int findMaxStt();

    //Tìm kiếm theo mã sản phẩm chi tiết
    @Query("select spct from SanPhamChiTiet spct " +
            "where spct.maSanPhamChiTiet like %:maSanPhamChiTiet% ORDER BY spct.ngaySua DESC ,spct.ngayTao DESC ")
    public Page<SanPhamChiTiet> searchByMaSanPhamChiTiet(String maSanPhamChiTiet,  Pageable pageable);

    //Lọc theo sản phẩm
    @Query("select spct from SanPhamChiTiet spct " +
            "join spct.sanPham sp " +
            "where sp.id = :SanPhamId ORDER BY spct.ngaySua DESC, spct.ngayTao DESC ")
    public Page<SanPhamChiTiet> searcBySanPhamId(int SanPhamId,Pageable pageable);

    //lọc theo màu sắc
    @Query("select spct from SanPhamChiTiet spct " +
            "join spct.mauSac ms " +
            "where ms.id = :idMauSac ORDER BY spct.ngaySua DESC , spct.ngayTao DESC ")
    public Page<SanPhamChiTiet> searchByMauSacId(int idMauSac,Pageable pageable);

    //Lọc theo kích thước
    @Query("select spct from SanPhamChiTiet spct " +
            "join spct.kichThuoc kt " +
            "where kt.id = :idKichThuoc ORDER BY spct.ngaySua DESC , spct.ngayTao DESC ")
    public Page<SanPhamChiTiet> searchByKichThuocId(int idKichThuoc,Pageable pageable);

    //Lọc theo khoảng giá bán
    @Query("select spct from SanPhamChiTiet spct " +
            "where spct.giaBan between :minPrice and :maxPrice ORDER BY  spct.ngaySua desc ,spct.ngayTao desc ")
    public Page<SanPhamChiTiet> searchByPrice(Double minPrice, Double maxPrice,Pageable pageable);


    //lấy ra giá trị giảm giá

    @Query("select DISTINCT   km.giaTri from SanPhamChiTiet spct " +
            "join spct.sanPham sp " +
            "join sp.khuyenMai km " +
            "where sp.id = :id ")
    public BigDecimal giaTri(Integer id);

    //Chỗ này là của trưởng nhóm code cấm đụng
    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.tenSanPham LIKE %:keyword% OR " +
            " spct.sanPham.thuongHieu.tenThuongHieu LIKE %:keyword% OR" +
            " spct.sanPham.xuatXu.tenXuatXu LIKE %:keyword% OR" +
            " spct.kichThuoc.tenKichThuoc LIKE %:keyword% OR" +
            " spct.mauSac.tenMauSac LIKE %:keyword ORDER By spct.ngaySua desc , spct.ngayTao DESC ")
    Page<SanPhamChiTiet> searchSanPhamById(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT spct FROM SanPhamChiTiet spct ORDER BY spct.ngaySua DESC ,spct.ngayTao DESC")
    Page<SanPhamChiTiet> finSPCTdAll(Pageable pageable);

    //Chỗ trưởng nhóm code cấm đụng vào

    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan,coalesce(spct.giaBanGiamGia,NULL), spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri  FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.sanPham.thuongHieu.id = :id GROUP BY spct.sanPham.tenSanPham, spct.giaBan,spct.giaBanGiamGia, spct.mauSac.tenMauSac,spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri")
    Page<Object[]> findBySanPhamThuongHieuTenThuongHieu(Integer id, Pageable pageable);

    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan, coalesce(spct.giaBanGiamGia, NULL), spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai,khuyenMai.giaTri FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.sanPham.thuongHieu.id = :idThuongHieu AND spct.mauSac.tenMauSac = :tenMauSac GROUP BY  spct.sanPham.tenSanPham, spct.giaBan,spct.giaBanGiamGia, spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai,khuyenMai.giaTri")
    Page<Object[]> findByThuongHieuAndMauSac(@Param("idThuongHieu") Integer idThuongHieu, @Param("tenMauSac") String tenMauSac, Pageable pageable);

    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan,coalesce(spct.giaBanGiamGia,NULL) ,spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.sanPham.thuongHieu.id = :idThuongHieu AND spct.kichThuoc.tenKichThuoc = :tenKichThuoc GROUP BY  spct.sanPham.tenSanPham,spct.giaBan,spct.giaBanGiamGia,spct.mauSac.tenMauSac,spct.imanges,khuyenMai.loaiKhuyenMai,khuyenMai.giaTri")
    Page<Object[]> findByThuongHieuAndKichThuoc(@Param("idThuongHieu") Integer idThuongHieu, @Param("tenKichThuoc") String tenKichThuoc, Pageable pageable);

    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan,coalesce(spct.giaBanGiamGia,NULL), spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.sanPham.thuongHieu.id = :idThuongHieu AND COALESCE(spct.giaBanGiamGia,spct.giaBan) BETWEEN :minGia AND :maxGia GROUP BY spct.sanPham.tenSanPham, spct.giaBan, spct.giaBanGiamGia, spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri")
    Page<Object[]> findByThuongHieuAndGiaBanBetween(@Param("idThuongHieu") int idThuongHieu, @Param("minGia") int minGia, @Param("maxGia") int maxGia, Pageable pageable);

    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan,coalesce(spct.giaBanGiamGia,NULL), spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.sanPham.thuongHieu.id = :idThuongHieu GROUP BY  spct.sanPham.tenSanPham, spct.giaBan, spct.giaBanGiamGia,spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri")
    Page<Object[]> findByThuongHieuId(@Param("idThuongHieu") Integer idThuongHieu, Pageable pageable);

    //Sắp xếp theo sản phẩm mới
    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan, coalesce(spct.giaBanGiamGia,NULL), spct.mauSac.tenMauSac,spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri, spct.ngayTao  FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.ngayTao >= :tenDaysAgo GROUP BY spct.sanPham.tenSanPham,spct.giaBan,spct.giaBanGiamGia,spct.mauSac.tenMauSac,spct.imanges,khuyenMai.loaiKhuyenMai,khuyenMai.giaTri, spct.ngayTao ORDER BY spct.ngayTao DESC")
    List<Object[]> findSanPhamByNewest(@Param("tenDaysAgo") Date tenDaysAgo);
    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan, coalesce(spct.giaBanGiamGia,NULL), spct.mauSac.tenMauSac,spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri, spct.ngayTao  FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.sanPham.thuongHieu.id = :idThuongHieu AND spct.ngayTao >= :tenDaysAgo GROUP BY spct.sanPham.tenSanPham,spct.giaBan,spct.giaBanGiamGia,spct.mauSac.tenMauSac,spct.imanges,khuyenMai.loaiKhuyenMai,khuyenMai.giaTri, spct.ngayTao ORDER BY spct.ngayTao DESC")
    Page<Object[]> findNikeByNewest(@Param("idThuongHieu") Integer idThuongHieu,@Param("tenDaysAgo") Date tenDaysAgo, Pageable pageable);

    //Sắp xếp theo giá từ thấp đến cao
    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan ,coalesce(spct.giaBanGiamGia, NULL), spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.sanPham.thuongHieu.id = :idThuongHieu GROUP BY spct.sanPham.tenSanPham, spct.giaBan,spct.giaBanGiamGia, spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai,khuyenMai.giaTri ORDER BY coalesce(spct.giaBanGiamGia,spct.giaBan) ASC")
    Page<Object[]> findNikeByPriceAsc(@Param("idThuongHieu") Integer idThuongHieu, Pageable pageable);

    //Sắp xếp theo giá từ cáo xuống thấp
    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan,coalesce(spct.giaBanGiamGia,NULL), spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.sanPham.thuongHieu.id = :idThuongHieu GROUP BY spct.sanPham.tenSanPham, spct.giaBan,spct.giaBanGiamGia, spct.mauSac.tenMauSac,spct.imanges, khuyenMai.loaiKhuyenMai,khuyenMai.giaTri ORDER BY coalesce(spct.giaBanGiamGia, spct.giaBan) DESC")
    Page<Object[]> findNikeByPriceDesc(@Param("idThuongHieu") Integer idThuongHieu, Pageable pageable);

    //Sắp xếp theo tên
    @Query("SELECT  MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan,coalesce(spct.giaBanGiamGia,NULL), spct.mauSac.tenMauSac, spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.sanPham.thuongHieu.id = :idThuongHieu GROUP BY spct.sanPham.tenSanPham, spct.giaBan,spct.giaBanGiamGia, spct.mauSac.tenMauSac,spct.imanges, khuyenMai.loaiKhuyenMai, khuyenMai.giaTri ORDER BY spct.sanPham.tenSanPham ASC")
    Page<Object[]> findNikeByName(@Param("idThuongHieu") Integer idThuongHieu, Pageable pageable);

    //coda của luận
    //Sắp xếp theo sản phẩm mới
    @Query("SELECT spct.sanPham.tenSanPham, spct.giaBan, spct.mauSac.tenMauSac, spct.ngayTao  FROM SanPhamChiTiet spct WHERE spct.sanPham.thuongHieu.id = :idThuongHieu GROUP BY spct.sanPham.tenSanPham,spct.giaBan,spct.mauSac.tenMauSac, spct.ngayTao ORDER BY spct.ngayTao DESC")
    Page<Object[]> findAdidasByNewest(@Param("idThuongHieu") Integer idThuongHieu, Pageable pageable);

    //Sắp xếp theo giá từ thấp đến cao
    @Query("SELECT spct.sanPham.tenSanPham, spct.giaBan , spct.mauSac.tenMauSac FROM SanPhamChiTiet  spct WHERE spct.sanPham.thuongHieu.id = :idThuongHieu GROUP BY spct.sanPham.tenSanPham, spct.giaBan, spct.mauSac.tenMauSac ORDER BY spct.giaBan ASC")
    Page<Object[]> findAdidasByPriceAsc(@Param("idThuongHieu") Integer idThuongHieu, Pageable pageable);

    //Sắp xếp theo giá từ cáo xuống thấp
    @Query("SELECT spct.sanPham.tenSanPham, spct.giaBan, spct.mauSac.tenMauSac FROM SanPhamChiTiet spct WHERE spct.sanPham.thuongHieu.id = :idThuongHieu GROUP BY spct.sanPham.tenSanPham, spct.giaBan, spct.mauSac.tenMauSac ORDER BY spct.giaBan DESC")
    Page<Object[]> findAdidasByPriceDesc(@Param("idThuongHieu") Integer idThuongHieu, Pageable pageable);

    //Sắp xếp theo tên
    @Query("SELECT spct.sanPham.tenSanPham, spct.giaBan, spct.mauSac.tenMauSac FROM SanPhamChiTiet spct WHERE spct.sanPham.thuongHieu.id = :idThuongHieu GROUP BY spct.sanPham.tenSanPham, spct.giaBan, spct.mauSac.tenMauSac ORDER BY spct.sanPham.tenSanPham ASC")
    Page<Object[]> findAdidasByName(@Param("idThuongHieu") Integer idThuongHieu, Pageable pageable);


    ////////////////hai
    @Query("select o from SanPhamChiTiet o where o.sanPham.thuongHieu.id = ?1")
    Page<SanPhamChiTiet> findByPumaThuongHieu(Integer id, Pageable pageable);

    @Query("select o from SanPhamChiTiet o where o.sanPham.thuongHieu.id = ?1" +
            " and (?2 is null or o.mauSac.tenMauSac = ?2)" +
            " and (?3 is null or o.kichThuoc.tenKichThuoc =?3)" +
            " and (?4 is null or o.giaBan >= ?4)" +
            " and (?5 is null or o.giaBan <= ?5)" +
            " and (?6 is null or o.sanPham.tenSanPham = ?6) ")
    Page<SanPhamChiTiet> findByThuongHieuPuma(Integer idThuongHieu, String tenMauSac, String tenKichThuoc, Double fromGiaBan, Double toGiaBan,String tenSanPham, Pageable page);

    @Query("select o from SanPhamChiTiet o where o.sanPham.thuongHieu.id = ?1 order by o.giaBan")
    Page<SanPhamChiTiet> findByPumaSortAsc(Integer id, Pageable pageable);

    @Query("select o from SanPhamChiTiet o where o.sanPham.thuongHieu.id = ?1 order by o.giaBan desc")
    Page<SanPhamChiTiet> findByPumaSortDesc(Integer id, Pageable pageable);

    @Query("select o from SanPhamChiTiet o where o.sanPham.thuongHieu.id = ?1 order by o.sanPham.tenSanPham")
    Page<SanPhamChiTiet> findByPumaSortName(Integer id, Pageable pageable);

    @Query("select o from SanPhamChiTiet o where o.sanPham.thuongHieu.id = ?1 order by o.ngayTao desc")
    Page<SanPhamChiTiet> findByPumaSortProductNew(Integer id, Pageable pageable);

    @Query("SELECT DISTINCT spct.sanPham.id, spct.sanPham.tenSanPham FROM SanPhamChiTiet spct WHERE spct.sanPham.thuongHieu.id = :idThuongHieu")
    List<Object[]> findBySanPham(@Param("idThuongHieu") Integer thuongHieu);

    @Query("SELECT Min(spct.id), spct.sanPham.tenSanPham, spct.giaBan,coalesce(spct.giaBanGiamGia, NULL), spct.mauSac.tenMauSac,spct.imanges ,khuyenMai.loaiKhuyenMai, khuyenMai.giaTri FROM SanPhamChiTiet spct LEFT JOIN spct.sanPham.khuyenMai khuyenMai WHERE spct.sanPham.thuongHieu.id = :idThuongHieu AND spct.sanPham.tenSanPham = :tenSanPham GROUP BY spct.sanPham.tenSanPham, spct.giaBan,spct.giaBanGiamGia, spct.mauSac.tenMauSac,spct.imanges,khuyenMai.loaiKhuyenMai, khuyenMai.giaTri ")
    Page<Object[]> findByTenSanPham(@Param("idThuongHieu") Integer idThuongHieu, @Param("tenSanPham") String tenSanPham, Pageable pageable);

    List<SanPhamChiTiet> findBySanPhamIdAndMauSacId(int idSanPham, int idMauSac);

    SanPhamChiTiet findBySanPhamIdAndKichThuocIdAndMauSacId(Integer idSanPham, Integer idKichThuoc,Integer idMauSac);

    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.mauSac.tenMauSac, spct.imanges FROM SanPhamChiTiet spct WHERE LOWER(spct.sanPham.tenSanPham) LIKE LOWER(CONCAT('%', :keyword , '%')) " +
            "OR LOWER(spct.mauSac.tenMauSac) LIKE LOWER(CONCAT('%' , :keyword, '%'))" +
            "OR LOWER(spct.kichThuoc.tenKichThuoc) LIKE LOWER(CONCAT('%', :keyword, '%')) GROUP BY spct.sanPham.tenSanPham, spct.mauSac.tenMauSac, spct.imanges")
    List<Object[]> searchProducts(@Param("keyword")String keyword);
    //Lệnh thực hiện update phía sản phẩm

    @Query("SELECT MIN(spct.id), spct.sanPham.tenSanPham, spct.giaBan,spct.giaBanGiamGia, spct.mauSac.tenMauSac, spct.imanges, spct.sanPham.khuyenMai.loaiKhuyenMai, spct.sanPham.khuyenMai.giaTri FROM SanPhamChiTiet spct GROUP BY spct.sanPham.tenSanPham, spct.giaBan,spct.giaBanGiamGia, spct.mauSac.tenMauSac, spct.imanges, spct.sanPham.khuyenMai.loaiKhuyenMai, spct.sanPham.khuyenMai.giaTri")
    List<Object[]> findSanPhamChiTietKhuyenMai();

    @Query("select o from SanPhamChiTiet o where o.sanPham.tenSanPham like ?1" +
            " and (?2 is null or o.mauSac.tenMauSac = ?2)" +
            " and (?3 is null or o.kichThuoc.tenKichThuoc = ?3)")
    Page<SanPhamChiTiet> findAddOrder(String tenSanPham, String tenMauSac, String tenKichThuoc, Pageable page);
}
