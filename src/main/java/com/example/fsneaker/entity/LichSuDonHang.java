package com.example.fsneaker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "LichSuDonHang")
@Data

public class LichSuDonHang {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "IdHoaDon")
    private DonHang donHang;

    @Column(name = "TrangThaiCu")
    private String trangThaiCu;

    @Column(name = "TrangThaiMoi")
    private String trangThaiMoi;

    @Column(name = "NgaySua")
    private LocalDateTime ngaySua;

    @PrePersist
    public void prePersist() {
        ngaySua = LocalDateTime.now();
    }
}
