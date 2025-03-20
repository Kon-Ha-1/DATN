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

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "MauSac")

public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "MaMauSac")
    @NotEmpty(message = "Mã màu sắc không được để trống!")
    @Size(min= 5, max = 20, message = "Mã màu sắc từ 5 đến 20 ký tự!")
    private String maMauSac;

    @Column(name = "TenMauSac")
    @NotEmpty(message = "Tên màu sắc không được để trống!")
    @Size(min = 2, max= 30, message = "Tên màu sắc từ 2 đến 30 ký tự!")
    private String tenMauSac;

    @Column(name = "TrangThai")
    @NotNull(message = "Bạn chưa chọn trạng thái!")
    private int trangThai;
    private Date ngayTao;
    private String nguoiTao;
    private Date ngaySua;
    private String nguoiSua;
}
