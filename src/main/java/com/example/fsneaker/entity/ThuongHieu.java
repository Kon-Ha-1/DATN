package com.example.fsneaker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "ThuongHieu")

public class ThuongHieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "MaThuongHieu")
    @NotEmpty(message = "Mã thương hiệu không được để trống!")
    @Size(min = 2, max =20 , message = "Mã thương hiệu từ 2 đến 20 ký tự!")
    private String maThuongHieu;

    @Column(name = "TenThuongHieu")
    @NotEmpty(message =  "Tên thương hiệu không được để trống!")
    @Size(min= 2, max=30 ,message = "Tên thương hiệu từ 2 đến 30 ký tự!")
    private String tenThuongHieu;

    @Column(name = "TrangThai")
    @NotNull(message = "Bạn chưa chọn trạng thái")
    private int trangThai;
    private Date ngayTao;
    private String nguoiTao;
    private Date ngaySua;
    private String nguoiSua;
}
