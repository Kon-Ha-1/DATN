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
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "XuatXu")

public class XuatXu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "MaXuatXu")
    @NotEmpty(message = "Mã xuất xứ không được để trống!")
    @Size(min = 1, max = 20 , message = "Mã xuất xứ từ 1 đến 20 ký tự!")
    private String maXuatXu;

    @Column(name = "TenXuatXu")
    @NotEmpty(message = "Tên xuất xứ không được để trống!")
    @Size(min = 1, max= 30 , message = "Tên xuất xứ từ 1 đến 30 ký tự!")
    private String tenXuatXu;

    @Column(name = "TrangThai")
    @NotNull(message = "Bạn chưa chọn trạng thái!")
    private int trangThai;
    private Date ngayTao;
    private String nguoiTao;
    private Date ngaySua;
    private String nguoiSua;
}
