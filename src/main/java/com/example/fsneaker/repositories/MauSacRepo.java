package com.example.fsneaker.repositories;

import com.example.fsneaker.entity.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MauSacRepo extends JpaRepository<MauSac, Integer> {

    @Query("Select ms from MauSac ms where ms.id = :id")
    public MauSac getMauSacById(int id);

    //Chỗ này là code của trưởng nhóm cẩm đụng vào
    @Query("SELECT ms.tenMauSac, COUNT(spct.id) FROM MauSac ms join SanPhamChiTiet spct ON ms.id = spct.mauSac.id WHERE spct.sanPham.thuongHieu.id = :idThuongHieu GROUP BY ms.tenMauSac HAVING COUNT(spct.id) > 0")
    List<Object[]> findByMauSacWithProduct(@Param("idThuongHieu") Integer idThuongHieu);
    boolean existsByMaMauSac(String maMauSac);
    @Query("SELECT ms FROM MauSac ms ORDER BY ms.ngaySua DESC , ms.ngayTao DESC ")
    Page<MauSac> findByMauSac(Pageable pageable);
}
