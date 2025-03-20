package com.example.fsneaker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "Voucher")

public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Size(max = 10, message = "Mã voucher không được dài quá 10 ký tự")
    @NotBlank(message = "Mã voucher không được bỏ trống")
    @Column(name = "MaVoucher")
    private String maVoucher;

    @NotBlank(message = "Tên Voucher không được bỏ trống")
    @Column(name = "TenVoucher")
    private String tenVoucher;

    @NotBlank(message = "Loại voucher không được bỏ trống")
    @Column(name = "LoaiVoucher")
    private String loaiVoucher;

    @Column(name = "MoTa")
    private String moTa;
    
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @NotNull(message = "Số lượng không được bỏ trống")
    @Column(name = "SoLuong")
    private Integer soLuong;

    @Positive(message = "Giá trị phải là số dương")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị phải lớn hơn 0")
    @NotNull(message = "Giá trị không được bỏ trống")
    @Column(name = "GiaTri")
    private BigDecimal giaTri;

    @Positive(message = "Đơn tối thiểu phải là số dương")
    @NotNull(message = "Đơn tối thiểu không được bỏ trống")
    @Column(name = "DonToiThieu")
    private BigDecimal donToiThieu;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Ngày bắt đầu không được bỏ trống")
    @Column(name = "NgayBatDau")
    private Date ngayBatDau;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Ngày kết thúc không được bỏ trống")
    @Column(name = "NgayKetThuc")
    private Date ngayKetThuc;

    @Column(name = "NgayTao")
    private Date ngayTao;


    //    @NotNull(message = "Trang Thai không được bỏ trống")
    @Column(name = "TrangThai")
    private Integer trangThai;
    private String nguoiTao;
    private Date ngaySua;
    private String nguoiSua;
//    @AssertTrue(message = "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc")
//    public boolean isNgayBatDauTruocNgayKetThuc() {
//        return ngayBatDau != null && ngayKetThuc != null && !ngayBatDau.isAfter(ngayKetThuc);
//    }
//
//    @AssertTrue(message = "Đơn tối thiểu phải lớn hơn giá trị giảm")
//    public boolean isDonToiThieuLonHonGiaTriGiam() {
//        return donToiThieu != null && giaTri != null && donToiThieu.compareTo(giaTri) > 0;
//    }


}
