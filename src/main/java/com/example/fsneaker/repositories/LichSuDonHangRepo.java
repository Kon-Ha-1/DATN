package com.example.fsneaker.repositories;

import com.example.fsneaker.entity.LichSuDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LichSuDonHangRepo extends JpaRepository<LichSuDonHang, Integer> {
    @Query("select o from LichSuDonHang o where o.donHang.id =?1 order by o.id asc")
    public List<LichSuDonHang> lichSuDonHangCT(Integer id);
}

