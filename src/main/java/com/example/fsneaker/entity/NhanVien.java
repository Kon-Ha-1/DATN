package com.example.fsneaker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NhanVien")
public class NhanVien {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "Id")
        private int id;

        @Size(max = 10, message = "Mã nhân viên không được dài quá 10 ký tự")
        @NotBlank(message = "Mã nhân viên không được bỏ trống !")
        @Column(unique = true, name = "MaNhanVien")  // unique để đảm bảo không trùng mã nhân viên
        private String maNhanVien;

        @NotBlank(message = "Tên nhân viên không được bỏ trống !")
        @Column(name = "TenNhanVien")
        private String tenNhanVien;

        @NotBlank(message = "Email không được bỏ trống !")
        @Email(message = "Email không hợp lệ !")
        @Column(unique = true, name = "Email")
        private String email;

        @Size(max = 10, message = "Số điện thoại không được dài quá 10 ký tự")
        @NotBlank(message = "Số điện thoại không được bỏ trống !")
        @Column(unique = true, name = "SoDienThoai")
        private String soDienThoai;

        @NotNull(message = "Ngày sinh không được bỏ trống !")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Column(name = "NgaySinh")
        private Date ngaySinh;

        @NotNull(message = "Giới tính không được bỏ trống !")
        @Column(name = "GioiTinh")
        private Boolean gioiTinh;

        @NotBlank(message = "Địa chỉ không được bỏ trống !")
        @Column(name = "DiaChi")
        private String diaChi;

        @Column(name = "VaiTro")
        private Integer vaiTro;

        @NotBlank(message = "Mật khẩu không được bỏ trống !")
        @Size(min = 8, max = 20, message = "Mật khẩu không được dài từ 8 - 20 ký tự")
        @Column(name = "MatKhau")
        private String matKhau;

        @Size(min = 12, max = 12, message = "Căm cước công dân phải đúng 12 ký tự!")
        @Column(unique = true, name = "CCCD")
        private String cccd;

        @NotNull(message = "Trạng thái không được bỏ trống !")
        @Column(name = "TrangThai")
        private Boolean trangThai;
        private Date ngayTao;
        private String nguoiTao;
        private Date ngaySua;
        private String nguoiSua;

}