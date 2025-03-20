package com.example.fsneaker.service;

import com.example.fsneaker.entity.GioHang;
import com.example.fsneaker.entity.GioHangChiTiet;
import com.example.fsneaker.entity.SanPham;
import com.example.fsneaker.entity.SanPhamChiTiet;
import com.example.fsneaker.repositories.GioHangChiTietRepo;
import com.example.fsneaker.repositories.SanPhamChiTietRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SanPhamChiTietService {
    //Chỗ này là của trướng nhóm code cấm đụng
    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;
    public SanPhamChiTiet themSPCT(SanPhamChiTiet sanPhamChiTiet, String tenNguoiDung){
        Date now = new Date();
        if(sanPhamChiTiet.getId() == 0){
            sanPhamChiTiet.setNgayTao(now);
            sanPhamChiTiet.setNguoiTao(tenNguoiDung);
        }
        sanPhamChiTiet.setNgaySua(now);
        sanPhamChiTiet.setNguoiSua(tenNguoiDung);
        return sanPhamChiTietRepo.save(sanPhamChiTiet);
    }
    public List<SanPhamChiTiet> getSanPhamChiTiet() {
        return sanPhamChiTietRepo.findAll();
    }

    public Page<SanPhamChiTiet> searchPaginated(String keyword, int page, int size) {
        return sanPhamChiTietRepo.searchSanPhamById(keyword, PageRequest.of(page, size));
    }

    public Page<SanPhamChiTiet> findPaginated(int page, int size) {
        return sanPhamChiTietRepo.finSPCTdAll(PageRequest.of(page, size));
    }

    public SanPhamChiTiet getSanPhamChiTietById(Integer id) {
        return sanPhamChiTietRepo.findById(id).orElse(null);
    }

    public void capNhatSanPhamChiTiet(SanPhamChiTiet sanPhamChiTiet) {
        sanPhamChiTietRepo.save(sanPhamChiTiet);
    }

    public Page<Object[]> getThuongHieuTenThuongHieu(Integer id, int page, int size) {
        return sanPhamChiTietRepo.findBySanPhamThuongHieuTenThuongHieu(id, PageRequest.of(page, size));
    }

    public Page<Object[]> getThuongHieuAndMauSac(Integer idThuongHieu, String tenMauSac, int page, int size) {
        return sanPhamChiTietRepo.findByThuongHieuAndMauSac(idThuongHieu, tenMauSac, PageRequest.of(page, size));
    }

    public Page<Object[]> getThuongHieuAndKichThuoc(Integer idThuongHieu, String tenKichThuoc, int page, int size) {
        return sanPhamChiTietRepo.findByThuongHieuAndKichThuoc(idThuongHieu, tenKichThuoc, PageRequest.of(page, size));
    }

    public Page<Object[]> getSanPhamTheoThuongHieuVaGia(int idThuongHieu, int minGia, int maxGia, int page, int size) {
        return sanPhamChiTietRepo.findByThuongHieuAndGiaBanBetween(idThuongHieu, minGia, maxGia, PageRequest.of(page, size));
    }

    public Page<Object[]> getSanPhamNikeSorted(Integer idThuongHieu, Pageable pageable) {
        return sanPhamChiTietRepo.findByThuongHieuId(idThuongHieu, pageable);
    }

    //Sắp xếp theo sản phẩm mới
    LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
    Date date = Date.from(tenDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
    public Page<Object[]> getNikeNyNewest(Integer idThuongHieu, int page, int size) {
        return sanPhamChiTietRepo.findNikeByNewest(idThuongHieu,date, PageRequest.of(page, size));
    }
    public List<Object[]> getSanPhamNewest(){
        return sanPhamChiTietRepo.findSanPhamByNewest(date);
    }

    //Sắp xếp theo giá từ thấp lên cáo
    public Page<Object[]> getNikeByPriceAsc(Integer idThuongHieu, int page, int size) {
        return sanPhamChiTietRepo.findNikeByPriceAsc(idThuongHieu, PageRequest.of(page, size));
    }

    //Sắp xếp theo giá từ cao xuống thấp
    public Page<Object[]> getNikeByPriceDesc(Integer idThuongHieu, int page, int size) {
        return sanPhamChiTietRepo.findNikeByPriceDesc(idThuongHieu, PageRequest.of(page, size));
    }

    //Sắp xếp theo tên
    public Page<Object[]> getNikeByName(Integer idThuongHieu, int page, int size) {
        return sanPhamChiTietRepo.findNikeByName(idThuongHieu, PageRequest.of(page, size));
    }

    public List<Object[]> getNiekByTenSanPham(Integer idThuongHieu){
        return sanPhamChiTietRepo.findBySanPham(idThuongHieu);


    }
    public Page<Object[]> getNikeByTenSanPhamAndThuongHieu(Integer idThuongHieu, String tenSanPham, int page, int size) {
        return sanPhamChiTietRepo.findByTenSanPham(idThuongHieu, tenSanPham, PageRequest.of(page, size));
    }
    //code của luận

    //Sắp xếp theo sản phẩm mới
    public Page<Object[]> getAdidasNyNewest(Integer idThuongHieu, int page, int size) {
        return sanPhamChiTietRepo.findAdidasByNewest(idThuongHieu, PageRequest.of(page, size));
    }

    //Sắp xếp theo giá từ thấp lên cáo
    public Page<Object[]> getAdidasByPriceAsc(Integer idThuongHieu, int page, int size) {
        return sanPhamChiTietRepo.findAdidasByPriceAsc(idThuongHieu, PageRequest.of(page, size));
    }

    //Sắp xếp theo giá từ cao xuống thấp
    public Page<Object[]> getAdidasByPriceDesc(Integer idThuongHieu, int page, int size) {
        return sanPhamChiTietRepo.findAdidasByPriceDesc(idThuongHieu, PageRequest.of(page, size));
    }

    //Sắp xếp theo tên
    public Page<Object[]> getAdidasByName(Integer idThuongHieu, int page, int size) {
        return sanPhamChiTietRepo.findAdidasByName(idThuongHieu, PageRequest.of(page, size));
    }
    public List<Object[]> getAdidasByTenSanPham(Integer idThuongHieu){
        return sanPhamChiTietRepo.findBySanPham(idThuongHieu);
    }

    public Page<Object[]> getAdidasByTenSanPhamAndThuongHieu(Integer idThuongHieu, String tenSanPham, int page, int size) {
        return sanPhamChiTietRepo.findByTenSanPham(idThuongHieu, tenSanPham, PageRequest.of(page, size));
    }

    public List<SanPhamChiTiet> getSanPhamChiTietBySanPhamIdAndMauSacId(int idSanPham, int idMauSac) {
        return sanPhamChiTietRepo.findBySanPhamIdAndMauSacId(idSanPham, idMauSac);
    }

    public SanPhamChiTiet getBySanPhamIdAndKichThuocIdAndMauSacId(Integer idSanPham, Integer idKichThuoc, Integer idMauSac) {
        return sanPhamChiTietRepo.findBySanPhamIdAndKichThuocIdAndMauSacId(idSanPham,idKichThuoc,idMauSac);
    }

    public List<Object[]> searchProducts(String keyword){
        return sanPhamChiTietRepo.searchProducts(keyword);
    }
    public List<Object[]> getSanPhamChiTietKhuyenMai(){
        return sanPhamChiTietRepo.findSanPhamChiTietKhuyenMai();
    }
}
