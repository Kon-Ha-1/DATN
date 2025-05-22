package com.example.fsneaker.controller;

public class GHController {

    // // //

/*
              order.setTongTienGiamGia(tongTienGiamGia);
}
//        else {
//            // Nếu không có voucher, đặt tổng tiền giảm giá là 0
//            order.setTongTienGiamGia(BigDecimal.ZERO);
//        }

        donHangRepo.save(order);
        redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm vào đơn hàng thành công.");
        return "redirect:/donhangadmin-detail/" + orderId;
    }


// xoá don hang chi tiet
@PostMapping("/donhangadmin-detail/{orderId}/remove-product/{chiTietId}")
public String removeProductFromOrder(@PathVariable Integer orderId, @PathVariable Integer chiTietId, RedirectAttributes redirectAttributes) {
    // Tìm đơn hàng
    DonHang order = donHangRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

    // Xóa chi tiết đơn hàng khỏi danh sách
    donHangChiTietRepo.deleteById(chiTietId);
    // Tính lại tổng giá trị đơn hàng
    BigDecimal newTongGiaTri = order.getDonHangChiTiets().stream()
            .map(DonHangChiTiet::getThanhTien) // Lấy giá trị thành tiền (BigDecimal)
            .reduce(BigDecimal.ZERO, BigDecimal::add); // Tính tổng bằng phương thức add

    order.setTongTien(newTongGiaTri);

    if (order.getGiamGia() != null) {
        Voucher voucher = order.getGiamGia();

        // Tính tổng tiền giảm giá
        BigDecimal tongTienGiamGia;
        if ("Giảm giá %".equals(voucher.getLoaiVoucher())) {
            // Voucher giảm giá theo phần trăm
            tongTienGiamGia = newTongGiaTri.subtract(newTongGiaTri.multiply(voucher.getGiaTri()).divide(BigDecimal.valueOf(100)));
        } else if ("Giảm giá số tiền".equals(voucher.getLoaiVoucher())) {
            // Voucher giảm giá theo số tiền
            tongTienGiamGia = newTongGiaTri.subtract(voucher.getGiaTri());
        } else {
            tongTienGiamGia = BigDecimal.ZERO; // Loại voucher không hợp lệ
        }

        // Đảm bảo tổng tiền giảm giá không vượt quá tổng giá trị đơn hàng
        if (tongTienGiamGia.compareTo(newTongGiaTri) > 0) {
            tongTienGiamGia = newTongGiaTri;
        }

        // Lưu tổng tiền giảm giá vào đơn hàng
        order.setTongTienGiamGia(tongTienGiamGia);
    }
    redirectAttributes.addFlashAttribute("message", "Xoá sản phẩm đơn hàng thành công.");
    donHangRepo.save(order);

    return "redirect:/donhangadmin-detail/" + orderId;
}

//cập nhật số lượng
@PostMapping("/donhangadmin-detail/{orderId}/update-product-quantity/{chiTietId}")
public String updateProductQuantity(@PathVariable Integer orderId,
                                    @PathVariable Integer chiTietId,
                                    @RequestParam Integer newQuantity,
                                    RedirectAttributes redirectAttributes) {
    // Tìm đơn hàng
    DonHang order = donHangRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

    // Kiểm tra danh sách chi tiết đơn hàng
    if (order.getDonHangChiTiets() == null || order.getDonHangChiTiets().isEmpty()) {
        throw new RuntimeException("Không có chi tiết đơn hàng nào trong đơn hàng này");
    }

    // Tìm chi tiết đơn hàng cần cập nhật
    DonHangChiTiet chiTietToUpdate = order.getDonHangChiTiets().stream()
            .filter(chiTiet -> chiTiet.getId() == chiTietId )
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng với ID: " + chiTietId));

    // Kiểm tra số lượng mới (phải lớn hơn 0)
    if (newQuantity <= 0) {
        throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0");
    }

    // Tính toán và cập nhật số lượng và thành tiền
    BigDecimal pricePerUnit = chiTietToUpdate.getSanPhamChiTiet().getGiaBan(); // Giá mỗi sản phẩm
    chiTietToUpdate.setSoLuong(newQuantity); // Cập nhật số lượng
    chiTietToUpdate.setThanhTien(pricePerUnit.multiply(BigDecimal.valueOf(newQuantity))); // Cập nhật thành tiền

    // Cập nhật tổng giá trị đơn hàng
    BigDecimal newTongGiaTri = order.getDonHangChiTiets().stream()
            .map(DonHangChiTiet::getThanhTien)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    order.setTongTien(newTongGiaTri);

    if (order.getGiamGia() != null) {
        Voucher voucher = order.getGiamGia();

        // Tính tổng tiền giảm giá
        BigDecimal tongTienGiamGia;
        if ("Giảm giá %".equals(voucher.getLoaiVoucher())) {
            // Voucher giảm giá theo phần trăm
            tongTienGiamGia = newTongGiaTri.subtract(newTongGiaTri.multiply(voucher.getGiaTri()).divide(BigDecimal.valueOf(100)));
        } else if ("Giảm giá số tiền".equals(voucher.getLoaiVoucher())) {
            // Voucher giảm giá theo số tiền
            tongTienGiamGia = newTongGiaTri.subtract(voucher.getGiaTri());
        } else {
            tongTienGiamGia = BigDecimal.ZERO; // Loại voucher không hợp lệ
        }

        // Đảm bảo tổng tiền giảm giá không vượt quá tổng giá trị đơn hàng
        if (tongTienGiamGia.compareTo(newTongGiaTri) > 0) {
            tongTienGiamGia = newTongGiaTri;
        }

        // Lưu tổng tiền giảm giá vào đơn hàng
        order.setTongTienGiamGia(tongTienGiamGia);
    }

    // Lưu thay đổi
    donHangRepo.save(order);
    redirectAttributes.addFlashAttribute("message", "Cập nhật số lượng đơn hàng thành công.");
    return "redirect:/donhangadmin-detail/" + orderId;
}

@GetMapping("/donhang/view-inphieugiao/{orderId}")
public ResponseEntity<Resource> inPhieuGiao(@PathVariable Integer orderId) {
    phieuGiaoHangService.inPhieuGiao(orderId);
    String fileName = "in_phieu_giao_" + orderId + ".pdf";
    File file = new File(fileName);

    if (!file.exists()) {
        throw new RuntimeException("Phiếu giao hàng không tồn tại.");
    }

    Resource resource = new FileSystemResource(file);
    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
            .body(resource);
}

@GetMapping("/donhang/view-inhoadon/{orderId}")
public ResponseEntity<Resource> inHoaDon(@PathVariable Integer orderId) {
    phieuGiaoHangService.inHoaDon(orderId);
    String fileName = "in_hoa_don_" + orderId + ".pdf";
    File file = new File(fileName);

    if (!file.exists()) {
        throw new RuntimeException("Hoá đơn không tồn tại.");
    }

    Resource resource = new FileSystemResource(file);
    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
            .body(resource);
}

     //*/
}
