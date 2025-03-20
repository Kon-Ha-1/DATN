package com.example.fsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DonHangChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String maDonHangChiTiet;

    @ManyToOne
    @JoinColumn(name = "idDonHang")
    private DonHang donHang;

    @ManyToOne
    @JoinColumn(name = "idSanPhamChiTiet")
    private SanPhamChiTiet sanPhamChiTiet;

    private int soLuong;
    private BigDecimal gia;
    private BigDecimal thanhTien;
}

