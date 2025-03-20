package com.example.fsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String maDonHang;

    @ManyToOne
    @JoinColumn(name = "idNhanVien")
    private NhanVien nhanVien;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idKhachHang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "idGiamGia")
    private Voucher giamGia;
    private LocalDate ngayMua;
    private LocalDateTime ngayTao;
    private String loaiDonHang;
    private String trangThai;
    private BigDecimal tongTien;
    private BigDecimal tongTienGiamGia;
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<DonHangChiTiet> donHangChiTiets;
    private String tenNguoiNhan;
    private String diaChiGiaoHang;
    private String soDienThoaiGiaoHang;
    private String emailGiaoHang;
}
