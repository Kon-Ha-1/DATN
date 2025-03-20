package com.example.fsneaker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name = "KhachHang")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "MaKhachHang")
    @NotBlank(message = "Mã khách hàng không được để trống!")
    @Size(min = 5, max = 20, message = "Mã khách hàng phải từ 5 đến 20 ký tự!")
    private String maKhachHang;

    @Column(name = "TenKhachHang")
    @NotBlank(message = "Tên khách hàng không được để trống!")
    @Size(min = 5, max= 30, message = "Tên khách hàng phải từ 5 đên 30 ký tự!")
    private String tenKhachHang;

    @Column(name = "Email")
    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không đúng định dạng!")
    private String email;

    @Column(name = "NgaySinh")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;

    @Column(name = "GioiTinh")
    @NotNull(message = "Bạn chưa chọn giới tính!")
    private Boolean gioiTinh;

    @Column(name = "DiaChi")
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(min = 5, max = 100, message = "Địa chỉ phải từ 5 đến 100 ký tự!")
    private String diaChi;

    @Column(name = "SoDienThoai")
    @NotBlank(message = "Số điện thoại không được để trống!")
    @Size(min = 10, max=11, message = "Số điện thoại phải có đúng 10 hoặc 11 số!")
    @Pattern(regexp = "^\\d+$", message = "Số điện thoại chỉ được chứa chữ số!")
    private String soDienThoai;
    private String matKhau;
    @Column(name = "TrangThai")
    @NotNull(message = "Bạn chưa chọn trạng thái!")
    private Boolean trangThai;
    @Column(name ="MaThongBao")
    private String resetToken;
    @Column(name="ThoiGianThongBao")
    private Date tokenExpiration;
    private Date ngayTao;
    private String nguoiTao;
    private Date ngaySua;
    private String nguoiSua;
}
