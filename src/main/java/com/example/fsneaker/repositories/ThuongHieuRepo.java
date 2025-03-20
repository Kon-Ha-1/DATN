package com.example.fsneaker.repositories;

import com.example.fsneaker.entity.ThuongHieu;
import com.example.fsneaker.entity.XuatXu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ThuongHieuRepo extends JpaRepository<ThuongHieu, Integer> {

    @Query("Select th from ThuongHieu th where th.id = :id")
    public ThuongHieu getThuongHieuById(int id);

    boolean existsByMaThuongHieu(String maThuongHieu);
    @Query("SELECT th FROM ThuongHieu th ORDER BY th.ngaySua DESC ,th.ngayTao DESC ")
    Page<ThuongHieu> findThuongHieuAll(Pageable pageable);
}
