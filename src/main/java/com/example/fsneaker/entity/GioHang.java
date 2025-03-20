package com.example.fsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GioHang {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true)
    private String maGioHang;
    @ManyToOne
    @JoinColumn(name = "idKhachHang")
    private KhachHang khachHang;

    private LocalDate ngayTao;
    private int trangThai;
    @OneToMany(mappedBy = "gioHang", cascade = CascadeType.ALL)
    private List<GioHangChiTiet> gioHangChiTietList = new ArrayList<>();

    public GioHangChiTiet findChiTietBySanPhamId(SanPham sanPham){
        return this.getGioHangChiTietList().stream().filter(item -> item.getSanPhamChiTiet().getSanPham().equals(sanPham)).findFirst().orElse(null);
    }
    public void addChiTiet(GioHangChiTiet chiTietMoi) {
        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        for (GioHangChiTiet chiTietHienTai : this.gioHangChiTietList) {
            if (chiTietHienTai.getSanPhamChiTiet().getSanPham().equals(chiTietMoi.getSanPhamChiTiet().getSanPham())) {
                // Nếu đã tồn tại, tăng số lượng
                chiTietHienTai.setSoLuong(chiTietHienTai.getSoLuong() + chiTietMoi.getSoLuong());
                return; // Kết thúc sau khi cập nhật
            }
        }
        // Nếu chưa tồn tại, thêm sản phẩm mới vào danh sách
        this.gioHangChiTietList.add(chiTietMoi);
        chiTietMoi.setGioHang(this); // Thiết lập quan hệ hai chiều nếu cần
    }
}
