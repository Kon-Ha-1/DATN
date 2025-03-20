package com.example.fsneaker.service;

import com.example.fsneaker.dto.GioHangTrongException;
import com.example.fsneaker.entity.*;
import com.example.fsneaker.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DonHangService {
    @Autowired
    private DonHangRepo donHangRepo;
    @Autowired
    private DonHangChiTietRepo donHangChiTietRepo;
    @Autowired
    private GioHangRepo gioHangRepo;
    @Autowired
    private KhachHangRepo khachHangRepo;
    @Autowired
    private GioHangChiTietRepo gioHangChiTietRepo;
    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;
    @Autowired
    private VoucherRepo voucherRepo;
    private static final List<String> noiThanhHaNoi = Arrays.asList(
            "Ba Đình", "Hoàn Kiếm", "Đống Đa", "Hai Bà Trưng",
            "Cầu Giấy", "Tây Hồ", "Thanh Xuân", "Hoàng Mai",
            "Long Biên", "Nam Từ Liêm", "Bắc Từ Liêm", "Hà Đông"
    );
    private static final List<String> ngoaiThanhHaNoi = Arrays.asList(
            "Gia Lâm", "Đông Anh", "Sóc Sơn", "Thanh Trì",
            "Thường Tín", "Phú Xuyên", "Hoài Đức", "Đan Phượng",
            "Thạch Thất", "Chương Mỹ", "Ứng Hòa", "Mỹ Đức",
            "Ba Vì", "Phúc Thọ", "Mê Linh", "Quốc Oai"
    );
    public List<DonHang> getDonHangByTrangThai(){
        return donHangRepo.findByTrangThai("Ðang chờ");
    }
    public DonHang getDonHangById(int id){
        return donHangRepo.findById(id).orElse(null);
    }
    public void capNhatDonHang(DonHang donHang) {
        donHangRepo.save(donHang);
    }
    public Boolean themHoaDon(DonHang donHang){
        try{
            donHangRepo.save(donHang);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public DonHangChiTiet kiemTraSanPhamTrongDonHang(int idDonHang, int idSanPhamChiTiet){
        return donHangChiTietRepo.findByDonHangIdAndSanPhamChiTietId(idDonHang, idSanPhamChiTiet);
    }
    public List<DonHang> getDonHangs(){
        return donHangRepo.findAll();
    }
    public String taoMaDonHang(){
        String format = "00000";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String part1 = now.format(formatter);
        String part2 = String.format("%05d", new Random().nextInt(100000));
        return "HD" + part1 + part2;
    }
    public BigDecimal tinhTongThuNhap(int nam) {
        List<DonHang> donHangs = donHangRepo.findByTrangThaiAndNam("Đã thanh toán",nam);
        return donHangs.stream()
                .map(donHang -> donHang.getGiamGia() != null ? donHang.getTongTienGiamGia() : donHang.getTongTien())
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Cộng dồn tổng tiền
    }
    public Long tongDonHang(int nam){
        return donHangRepo.countByTrangThaiAndNam("Đã thanh toán",nam);
    }

    public int tongSanPhamDaBan(int nam){
        List<DonHang> donHangs = donHangRepo.findByTrangThaiAndNam("Đã thanh toán", nam);

        //Tính tổng số lượng sản phẩm từ chi tiết đơn hàng
        return donHangs.stream()
                .flatMap(donHang -> donHang.getDonHangChiTiets().stream())
                .mapToInt(DonHangChiTiet::getSoLuong)
                .sum();
    }
    public int calculateToTalSoldProductsInLastWeek(){
        //Lấy ngày hôm nay và ngày cách đây 1 tuần
        LocalDate endDate =LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(1);

        //Lấy tất cả các đơn hàng đã thanh toán trong tuần qua
        List<DonHang> donHangs = donHangRepo.findByNgayMuaBetweenAndTrangThaiEquals(startDate,endDate,"Đã thanh toán");

        //Tính tổng số lượng sản phẩm từ chi tiết đơn hàng
        return donHangs.stream()
                .flatMap(donHang -> donHang.getDonHangChiTiets().stream())
                .mapToInt(DonHangChiTiet::getSoLuong)
                .sum();
    }

    public Map<String , Long> getDonHangTheoTrangThai(){
        List<Object[]> results = donHangRepo.demDonHangTheoTrangThai();
        Map<String, Long> donHangTT = new HashMap<>();
        for(Object[] result :results){
            String trangThai = (String) result[0];
            Long count = (Long) result[1];
            donHangTT.put(trangThai,count);
        }
        return donHangTT;
    }
    //Lấy tổng thu nhập theo ngày trong tháng hiện tại
    public List<Object[]> getThuNhapTheoThang(){
        return donHangRepo.findThuNhapTheoThang("Đã thanh toán");
    }
    //Lấy số đơn hàng theo ngày trong tháng hiện tại
    public List<Object[]> getDonHangTheoThang(){
        return donHangRepo.findDonHangTheoThang("Đã thanh toán");
    }
    //Lấy tổng thu nhập theo tháng trong năm hiện tại
    public List<Object[]> getThuNhapTheoNam(){
        return donHangRepo.findThuNhapTheoNam();
    }
    //Lấy số đơn hàng theo tháng trong năm hiện tại
    public List<Object[]> getDonHangTheoNam(){
        return donHangRepo.findDonHangTheoNam();
    }
    //    public Map<String, Long> thongKeDonHangTheoTrangThai(){
//        Map<String, Long> result = new HashMap<>();
//        result.put("Đang chờ", donHangRepo.countByTrangThai("Đang chờ"));
//        result.put("Đang xử lý", donHangRepo.countByTrangThai("Đang xử lý"));
//        result.put("Đã giao", donHangRepo.countByTrangThai("Đã giao"));
//        result.put("Đã thanh toán", donHangRepo.countByTrangThai("Đã thanh toán"));
//        result.put("Đã hủy", donHangRepo.countByTrangThai("Đã hủy"));
//        return result;
//    }
    public List<Object[]> getSoDonHangTheoTrangThai(){
        return donHangRepo.demDonHangTheoTrangThai();
    }
    public Page<Object[]> thongKeKhachHangTheoTongTien(String trangThai,LocalDate startDate, LocalDate endDate, int page, int size){
        return donHangRepo.thongKeKhachHangTheoTongTien(trangThai,startDate,endDate, PageRequest.of(page,size));
    }

    public Page<Object[]> thongKeKhachHangTheoSoLanMua(String trangThai, LocalDate startDate, LocalDate endDate, int page, int size){
        return donHangRepo.thongKeKhachHangTheoSoLanMua(trangThai,startDate,endDate, PageRequest.of(page,size));
    }
    public Page<Object[]> thongKeKHTheoTongTien(String trangThai, int page, int size){
        return donHangRepo.thongKeKHTheoTongTien(trangThai,PageRequest.of(page,size));
    }

    public Page<Object[]> thongKeKHTheoSoLanMua(String trangThai, int page, int size){
        return donHangRepo.thongKeKHTheoSoLanMua(trangThai, PageRequest.of(page,size));
    }
    public DonHang getByKhachHangId(Integer idKhachHang){
        return donHangRepo.findByKhachHangId(idKhachHang);
    }
    //Hủy đơn hàng
    public DonHang getDonHangByMa(String maDonHang){
        return donHangRepo.findByMaDonHang(maDonHang);
    }
    public Boolean capNhatTrangThai(DonHang donHang){
        try{
            donHangRepo.save(donHang);//Save cập nhật trạng thái
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public BigDecimal tinhTongThuNhapTheoThoiGian(LocalDate startDate, LocalDate endDate, String trangThai){
        BigDecimal tongThuNhap = donHangRepo.tinhTongThuNhap("Đã thanh toán",startDate, endDate);

        if(tongThuNhap == null || !"Đã thanh toán".equals(trangThai)){
            return BigDecimal.ZERO;
        }

        return tongThuNhap;
    }
    public Long tinhTongKhachHangTheoThoiGian(LocalDate startDate, LocalDate endDate,String trangThai){
        return donHangRepo.tinhTongKhachHang(startDate,endDate,trangThai);
    }
    public Long tinhTongDonHangTheoThoiGian(LocalDate startDate,LocalDate endDate, String trangThai){
        return donHangRepo.tinhTongDonHang(startDate, endDate,trangThai);
    }
    public Long tinhTongSanPhamDaBanTheoThoiGian(LocalDate startDate, LocalDate endDate, String trangThai){
        Long tongSanPham = donHangRepo.tinhTongSanPhamDaBan(startDate, endDate,trangThai);
        if(tongSanPham == null || !"Đã thanh toán".equals(trangThai)){
            return 0L;
        }
        return tongSanPham;
    }
    //    public void updateSanPhamQuantity(Integer idDonHang, Integer idSanPhamChiTiet, Integer soLuong){
//        DonHangChiTiet chiTiet = donHangChiTietRepo.findByDonHangIdAndSanPhamChiTietId(idDonHang,idSanPhamChiTiet);
//        if(chiTiet != null){
//            chiTiet.setSoLuong(soLuong); //Cập nhật số lượng
//            chiTiet.setThanhTien(soLuong * chiTiet.getGia());
//            donHangChiTietRepo.save(chiTiet); //Lưu vào database
//        }
//    }
    @Transactional
    public DonHang chuyenGioHangSangDonHang(int idGioHang,int idKhachHang){
        //Lấy thông tin giỏ hàng
        GioHang gioHang= gioHangRepo.findById(idGioHang).orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng!"));
        //Kiểm tra nếu giỏ hàng trống
        if(gioHang.getGioHangChiTietList() == null || gioHang.getGioHangChiTietList().isEmpty()){
            throw new GioHangTrongException("Giỏ hàng trống! Không thể tới trang thanh toán!");
        }
        //Lấy thông tin khách hàng
        KhachHang khachHang = khachHangRepo.findById(idKhachHang).orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));
        String format = "00000";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String part1 = now.format(formatter);
        String part2 = String.format("%05d", new Random().nextInt(100000));
        //Tạo đối tượng Đơn hàng mới
        DonHang donHang = new DonHang();
        donHang.setMaDonHang("DH"+part1+part2);
        donHang.setKhachHang(khachHang);
        donHang.setNgayMua(LocalDate.now());
        donHang.setNgayTao(LocalDateTime.now());
        donHang.setTrangThai("Đang xử lý");
        donHang.setLoaiDonHang("Online");
        donHang.setDiaChiGiaoHang(khachHang.getDiaChi());
        donHang.setTenNguoiNhan(khachHang.getTenKhachHang());
        donHang.setEmailGiaoHang(khachHang.getEmail());
        donHang.setSoDienThoaiGiaoHang(khachHang.getSoDienThoai());

        //Chuyển sản phẩm từ giỏ hàng chi tiết sang đơn hàng chi tiết
        List<DonHangChiTiet> donHangChiTietList = new ArrayList<>();
        BigDecimal tongTien = BigDecimal.ZERO;

        for(GioHangChiTiet gioHangChiTiet : gioHang.getGioHangChiTietList()){
            DonHangChiTiet donHangChiTiet = new DonHangChiTiet();
            donHangChiTiet.setMaDonHangChiTiet("DHCT"+part1+part2);
            donHangChiTiet.setSanPhamChiTiet(gioHangChiTiet.getSanPhamChiTiet());
            donHangChiTiet.setSoLuong(gioHangChiTiet.getSoLuong());
            donHangChiTiet.setGia(gioHangChiTiet.getGia());
            BigDecimal thanhTien = BigDecimal.valueOf(gioHangChiTiet.getSoLuong()).multiply(gioHangChiTiet.getGia());
            donHangChiTiet.setThanhTien(thanhTien);
            //Cộng đòn tổng tiền bằng BigDecimal
            tongTien = tongTien.add(thanhTien);
            donHangChiTiet.setDonHang(donHang);
            donHangChiTietList.add(donHangChiTiet);
        }

        donHang.setDonHangChiTiets(donHangChiTietList);
        donHang.setTongTien(tongTien);
        // Lưu đơn hàng vào cơ sở dữ liệu
        DonHang saveDonHang = donHangRepo.save(donHang);
        donHangChiTietRepo.saveAll(donHangChiTietList);
        //Lưu đơn hàng vào cơ sở dữ liệu
        return saveDonHang;
    }
    public void capNhatTrangThaiDonHang(int idDonHang, String trangThai){
        DonHang donHang = donHangRepo.findById(idDonHang).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        donHang.setTrangThai(trangThai);
        donHangRepo.save(donHang);
    }
    public DonHang getDonHangByKhachHangAndTrangThaiAndLoaiDonHang(int idKhachHang){
        return donHangRepo.findByKhachHangAndTrangThaiAndLoaiDonHang(idKhachHang,"Đang xử lý","Online");
    }
    @Transactional
    public void capNhatDonHangTuGioHang(DonHang donHang, GioHang gioHang){
        //Bước 1: lấy danh sách sản phẩm chi tiết từ giỏ hàng
        List<GioHangChiTiet> gioHangChiTietList = gioHangChiTietRepo.findByGioHangId(gioHang.getId());

        //Nếu giỏ hàng trống, không làm gì
        if(gioHangChiTietList.isEmpty()){
            throw new GioHangTrongException("Giỏ hàng trống. Không thể tới trang thanh toán!");
        }
        //Bước 2: Xóa chi tiết cũ trong đơn hàng
        donHangChiTietRepo.deleteDonHangChiTietByDonHangId(donHang.getId());

        //Bước 3: Thêm chi tiết mới từ giỏ hàng vào đơn hàng
        BigDecimal tongTienMoi = BigDecimal.ZERO;
        for(GioHangChiTiet gioHangChiTiet : gioHangChiTietList){
            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.findById(gioHangChiTiet.getSanPhamChiTiet().getId());

            if(sanPhamChiTiet == null){
                throw new IllegalStateException("Sản phẩm chi tiết không tồn tại: "+gioHangChiTiet.getSanPhamChiTiet().getId());
            }
            //Tính thành tiền cho mỗi sản phẩm
            BigDecimal giaBan = sanPhamChiTiet.getSanPham().getKhuyenMai() != null ? sanPhamChiTiet.getGiaBanGiamGia() : sanPhamChiTiet.getGiaBan();
            BigDecimal soLuong = BigDecimal.valueOf(gioHangChiTiet.getSoLuong());
            BigDecimal thanhTien = giaBan.multiply(soLuong);

            //Cập nhật tổng tiền
            tongTienMoi = tongTienMoi.add(thanhTien);
            //Tạo chi tiết đơn hàng
            DonHangChiTiet donHangChiTiet = new DonHangChiTiet();
            donHangChiTiet.setDonHang(donHang);
            donHangChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
            donHangChiTiet.setSoLuong(gioHangChiTiet.getSoLuong());
            donHangChiTiet.setGia(sanPhamChiTiet.getSanPham().getKhuyenMai() != null ? sanPhamChiTiet.getGiaBanGiamGia() : sanPhamChiTiet.getGiaBan());
            donHangChiTiet.setThanhTien(thanhTien);

            //Lưu chi tiết đơn hàng
            donHangChiTietRepo.save(donHangChiTiet);
        }
        //Bước 4: Cập nhật tổng tiền vào đơn hàng
        donHang.setTongTien(tongTienMoi);

        //Bước 5: Tính lại tổng tiền sau khi giảm giá nếu có voucher
        if(donHang.getGiamGia() != null){
            BigDecimal tongTienGiamGia = tinhTongTienGiamGia(donHang);
            donHang.setTongTienGiamGia(tongTienGiamGia);
        }
        //Bước 6: Lưu lại đơn hàng
        donHangRepo.save(donHang);
    }
    public BigDecimal tinhTongTienGiamGia(DonHang donHang){
        if(donHang == null){
            throw new IllegalArgumentException("Đơn hàng không được null!");
        }
        //Lấy tổng tiên trước khi giảm gia
        BigDecimal tongTien = donHang.getTongTien();
        //Kiểm tra xem đơn hàng có voucher giảm giá không
        Voucher voucher = voucherRepo.findById(donHang.getGiamGia().getId()).orElse(null);
        if(voucher ==  null){
            return tongTien;//Không có voucher, trả về tổng tiền ban đầu
        }
        // Áp dụng giảm giá
        BigDecimal tongTienGiamGia;
        if(voucher.getLoaiVoucher().equalsIgnoreCase("Giảm giá %")){
            //Giảm giá phăn trăm
            BigDecimal giaTriGiamGia = voucher.getGiaTri();
            tongTienGiamGia = tongTien.subtract(tongTien.multiply(giaTriGiamGia).divide(BigDecimal.valueOf(100)));
        }else if(voucher.getLoaiVoucher().equalsIgnoreCase("Giảm giá số tiền")){
            //Giảm giá theo số tiền cố định
            BigDecimal giaTriVoucher = voucher.getGiaTri();
            tongTienGiamGia = tongTien.subtract(giaTriVoucher);
        }else{
            throw new IllegalArgumentException("Loại giảm giá không hợp lệ: "+ voucher.getLoaiVoucher());
        }
        //Đảm bảo tổng tiền không âm
        return tongTienGiamGia.max(BigDecimal.ZERO);
    }
    public void capNhatThongTinNguoiDung(int idDonHang, String tenNguoiNhan, String diaChiGiaoHang, String soDienThoaiGiaoHang, String emailGiaoHang){
        DonHang donHang = donHangRepo.findById(idDonHang).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        donHang.setTenNguoiNhan(tenNguoiNhan);
        donHang.setDiaChiGiaoHang(diaChiGiaoHang);
        donHang.setSoDienThoaiGiaoHang(soDienThoaiGiaoHang);
        donHang.setEmailGiaoHang(emailGiaoHang);
        donHangRepo.save(donHang);
    }
    private boolean noiThanh(String khuVuc) {
        if (khuVuc == null) {
            return false;
        }
        String normalizedKhuVuc = khuVuc.trim().toLowerCase(); // Loại bỏ khoảng trắng và chuyển về chữ thường
        return noiThanhHaNoi.stream()
                .map(String::toLowerCase) // Chuyển danh sách về chữ thường
                .anyMatch(normalizedKhuVuc::contains); // So sánh không phân biệt hoa/thường
    }

    private boolean ngoaiThanh(String khuVuc) {
        if (khuVuc == null) {
            return false;
        }
        String normalizedKhuVuc = khuVuc.trim().toLowerCase();
        return ngoaiThanhHaNoi.stream()
                .map(String::toLowerCase)
                .anyMatch(normalizedKhuVuc::contains);
    }
    public BigDecimal tinhPhiShip(String khuVuc, List<DonHangChiTiet> donHangChiTiets) {
        BigDecimal phiShip;
        if (noiThanh(khuVuc)) {
            phiShip = BigDecimal.valueOf(15000); // Phí ship nội thành
        } else if (ngoaiThanh(khuVuc)) {
            phiShip = BigDecimal.valueOf(18000); // Phí ship ngoại thành
        } else {
            phiShip = BigDecimal.valueOf(25000); // Phí ship ngoại tỉnh
        }
        return phiShip;
    }
//    public BigDecimal tinhTongTrongLuong(List<DonHangChiTiet> donHangChiTiets){
//        BigDecimal trongLuong= BigDecimal.valueOf(0.5); // 1 đôi giày là 0,5kg
//        return donHangChiTiets.stream()
//                .map(chiTiet -> trongLuong.multiply(BigDecimal.valueOf(chiTiet.getSoLuong())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//    private BigDecimal tinhPhiShipHaNoi(BigDecimal tongTrongLuong, String khuVuc){
//        BigDecimal phiCoBan = tongTrongLuong.compareTo(BigDecimal.ONE) <= 0
//                ? BigDecimal.valueOf(15000) : BigDecimal.valueOf(18000);
//        if(tongTrongLuong.compareTo(BigDecimal.valueOf(2)) > 0){
//            BigDecimal trongLuongThem = tongTrongLuong.subtract(BigDecimal.valueOf(2));
//            BigDecimal phiThem = trongLuongThem.divide(BigDecimal.valueOf(0.5), RoundingMode.CEILING)
//                    .multiply(BigDecimal.valueOf(1000));
//            phiCoBan = phiCoBan.add(phiThem);
//        }
//        return phiCoBan;
//    }
//    private BigDecimal tinhPhiShipTinhKhac(BigDecimal tongTrongLuong){
//        BigDecimal phiCoBan = tongTrongLuong.compareTo(BigDecimal.ONE) <= 0
//                ? BigDecimal.valueOf(21000)
//                : BigDecimal.valueOf(25000);
//        if(tongTrongLuong.compareTo(BigDecimal.valueOf(2)) > 0){
//            BigDecimal trongLuongThem = tongTrongLuong.subtract(BigDecimal.valueOf(2));
//            BigDecimal phiThem = trongLuongThem.divide(BigDecimal.valueOf(0.5),RoundingMode.CEILING)
//                    .multiply(BigDecimal.valueOf(2500));
//            phiCoBan = phiCoBan.add(phiThem);
//        }
//        return phiCoBan;
//    }
}
