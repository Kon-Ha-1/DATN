package com.example.fsneaker.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class KhachHangDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;


    private String maKhachHang;

    @Column(name = "TenKhachHang")
    @NotEmpty(message = "Tên khách hàng không được để trống!")
    @Size(min = 5, max= 30, message = "Tên khách hàng phải từ 5 đên 30 ký tự!")
    private String tenKhachHang;

    @Column(name = "Email")
    @NotEmpty(message = "Email không được để trống!")
    @Email(message = "Email không đúng định dạng!")
    private String email;

    @Column(name = "NgaySinh")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;


    private Boolean gioiTinh;

    @Column(name = "DiaChi")
    @NotEmpty(message = "Địa chỉ không được để trống")
    @Size(min = 5, max = 100, message = "Địa chỉ phải từ 5 đến 100 ký tự!")
    private String diaChi;

    @Column(name = "SoDienThoai")
    @NotEmpty(message = "Số điện thoại không được để trống!")
    @Size(min = 10, max=10, message = "Số điện thoại phải có đúng 10 chữ sổ!")
    @Pattern(regexp = "^\\d+$", message = "Số điện thoại chỉ được chứa chữ số!")
    private String soDienThoai;


    @Column(name = "MatKhau")
    @NotEmpty(message = "Mật khẩu không được để trống!")
    @Size(min = 8, max=30, message = "Mật khẩu từ 8 đến 30 kí tự")

    private String matKhau;

    @Transient
    private String matKhauXacNhan;


    private Boolean trangThai;
}
