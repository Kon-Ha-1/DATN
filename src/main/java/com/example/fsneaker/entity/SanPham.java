package com.example.fsneaker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table(name = "SanPham")


public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;


    @NotBlank(message = "Mã sản phẩm không được để trống!")
    @Column(name = "MaSanPham")
    private String maSanPham;

    @NotBlank(message = "Tên sản phẩm không được để trống!")
    @Column(name = "TenSanPham")
    private String tenSanPham;

    @Column(name = "NgayTao")
    private Date ngayTao;

    @NotNull(message = "Chưa chọn thương hiệu!")
    @ManyToOne
    @JoinColumn(name = "IdThuongHieu", referencedColumnName = "Id")
    private ThuongHieu thuongHieu;

    @NotNull(message = "Chưa chọn xuất xứ!")
    @ManyToOne
    @JoinColumn(name = "IdXuatXu", referencedColumnName = "Id")
    private XuatXu xuatXu;


    @ManyToOne
    @JoinColumn(name = "IdKhuyenMai", referencedColumnName = "Id")
    private KhuyenMai khuyenMai;


    @Column(name = "TrangThai")
    private int trangThai;
    private String nguoiTao;
    private Date ngaySua;
    private String nguoiSua;

}
