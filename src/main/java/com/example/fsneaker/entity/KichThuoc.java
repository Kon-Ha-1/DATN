package com.example.fsneaker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@NoArgsConstructor
@AllArgsConstructor


@Entity
@Table(name = "KichThuoc")

public class KichThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;


    @Column(name = "MaKichThuoc")
    @NotBlank(message = "Mã kích thước không được để trống!")
    @Size(min =5 , max= 20, message = "Mã kích thước từ 5 đến 20 ký tự!")
    private String makichThuoc;

    @Column(name = "TenKichThuoc")
    @NotBlank(message =  "Tên kích thước không được để trống!")
    @Size(min =2, max=2, message = "Tên kích thước chỉ 2 ký tự!")
    private String tenKichThuoc;

    @Column(name = "TrangThai")
    @NotNull(message = "Bạn chưa chọn trạng thái!")
    private int TrangThai;
    private Date ngayTao;
    private String nguoiTao;
    private Date ngaySua;
    private String nguoiSua;
}
