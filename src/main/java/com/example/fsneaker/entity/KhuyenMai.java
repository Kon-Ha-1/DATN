package com.example.fsneaker.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="KhuyenMai")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    @Size(min = 5, max = 10, message = "Mã khuyến mãi phải có từ 5 đến 10 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Mã khuyến mãi chỉ chứa chữ và số")

    @Column(name="MaKhuyenMai", unique = true)
    private String maKhuyenMai;

    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(min = 5, max = 20, message = "Tên khuyến mãi phải có từ 5 đến 20 ký tự")
    @Column(name="TenKhuyenMai" ,unique = true)
    private String tenKhuyenMai;


    @Column(name="LoaiKhuyenMai")
    private String loaiKhuyenMai;


    @Column(name="MoTa")
    private String moTa;

    @NotNull(message = "giá trị không được để trống")
    @Column(name="GiaTri")
    private BigDecimal giaTri;

    @NotNull(message = "đơn tối thiểu không được để trống")
    @Column(name = "donToiThieu")
    private BigDecimal donToiThieu;


    @Temporal(TemporalType.DATE)
    @Column(name="NgayBatDau")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date ngayBatDau;

    @Temporal(TemporalType.DATE)
    @Column(name="NgayKetThuc")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date ngayKetThuc;


    @Column(name="TrangThai")
    private int trangThai;
    private Date ngayTao;
    private String nguoiTao;
    private Date ngaySua;
    private String nguoiSua;
}
