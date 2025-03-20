package com.example.fsneaker.controller;

import com.example.fsneaker.entity.DonHang;
import com.example.fsneaker.entity.DonHangChiTiet;
import com.example.fsneaker.service.DonHangChiTietService;
import com.example.fsneaker.service.DonHangService;
import com.example.fsneaker.service.KhachHangService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

@Controller
public class TrangAdminController {
    //Trang thông kê
    @Autowired
    private DonHangService donHangService;
    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private DonHangChiTietService donHangChiTietService;
    @GetMapping("/trangchuadmin")
    public String TrangChu(@RequestParam(value = "sortByBD",required = false, defaultValue = "theoThang")String sortByBD,@RequestParam(value="sortBySP",required = false, defaultValue = "banChayNhat")String sortBySP,@RequestParam(value = "sortBy", required = false,defaultValue = "soLanMua")String sortBy,@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDate startDate, @RequestParam(value = "endDate",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate, @RequestParam(value = "page",defaultValue = "0")int page,@RequestParam(value="page1",defaultValue = "0")int page1, Model model, HttpServletRequest request) throws IOException {
        int pageSize = 5;
        int namHienTai = Year.now().getValue();
        Long tongSoKhachHang;
        BigDecimal tongThuNhap = new BigDecimal("0.0");
        Long tongSoDonHang = 0L;
        Long tongSanPham = 0L;
        Page<Object[]> orderStarts = donHangService.thongKeKhachHangTheoTongTien("Đã thanh toán",startDate, endDate, page,pageSize);
        Page<Object[]> thongKeSanPham = donHangChiTietService.getBestSellingProducts("Đã thanh toán",page1,pageSize);
        if(startDate == null && endDate == null){
            tongSoKhachHang = khachHangService.countTotalKhachHang(namHienTai);
            tongThuNhap = donHangService.tinhTongThuNhap(namHienTai);
            tongSoDonHang = donHangService.tongDonHang(namHienTai);
            tongSanPham = (long) donHangService.tongSanPhamDaBan(namHienTai);
            if("tongTien".equals(sortBy)){
                orderStarts = donHangService.thongKeKHTheoTongTien("Đã thanh toán",page,pageSize);
            }else{
                orderStarts = donHangService.thongKeKHTheoSoLanMua("Đã thanh toán",page,pageSize);
            }
            if("banChayNhat".equals(sortBySP)){
                thongKeSanPham = donHangChiTietService.getBestSellingProducts("Đã thanh toán",page1,pageSize);
            }else{
                thongKeSanPham = donHangChiTietService.getSanPhamDoanhThuCaoNhat("Đã thanh toán",page1,pageSize);
            }
        }else{
            // Kiểm tra nếu ngày kết thúc trước ngày bắt đầu
            if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                model.addAttribute("errorMessage", "Ngày kết thúc không được trước ngày bắt đầu.");
                tongThuNhap = BigDecimal.ZERO;
                tongSoDonHang = 0L;
                tongSanPham = 0L;
                tongSoKhachHang = 0L;
                model.addAttribute("tongThuNhap", tongThuNhap);
                model.addAttribute("tongSoKhachHang",tongSoKhachHang);
                model.addAttribute("tongSoDonHang", tongSoDonHang);
                model.addAttribute("tongSanPham", tongSanPham);
                return "templateadmin/trangadmin"; // Trả về template với thông báo lỗi
            }
            if (startDate == null) {
                startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1); //Măc định là ngày 1 tháng 1 năm hiện tại
            }
            if (endDate == null) {
                endDate = LocalDate.now(); // Mặc định là ngày hiện tại
            }

            if("tongTien".equals(sortBy)){
                orderStarts = donHangService.thongKeKhachHangTheoTongTien("Đã thanh toán",startDate, endDate, page,pageSize);
            }else{
                orderStarts = donHangService.thongKeKhachHangTheoSoLanMua("Đã thanh toán",startDate,endDate,page,pageSize);
            }
            if("banChayNhat".equals(sortBySP)){
                thongKeSanPham = donHangChiTietService.getSanPhamBanChayTheoNgay("Đã thanh toán", startDate,endDate,page1,pageSize);
            }else{
                thongKeSanPham = donHangChiTietService.getSanPhamDoanhThuCaoNhatTheoNgay("Đã thanh toán",startDate,endDate,page1,pageSize);
            }
            tongThuNhap = donHangService.tinhTongThuNhapTheoThoiGian(startDate,endDate,"Đã thanh toán");
            tongSoDonHang = donHangService.tinhTongDonHangTheoThoiGian(startDate,endDate,"Đã thanh toán");
            tongSanPham = donHangService.tinhTongSanPhamDaBanTheoThoiGian(startDate,endDate,"Đã thanh toán");
            tongSoKhachHang = donHangService.tinhTongKhachHangTheoThoiGian(startDate,endDate,"Đã thanh toán");
        }

        model.addAttribute("thongKeSanPham",thongKeSanPham);
        //model.addAttribute("sanPhamDoanhThuCaoNhat",sanPhamDoanhThuCaoNhat);
        //Thống kê theo khoảng thời gian

        model.addAttribute("tongThuNhap", tongThuNhap != null ? tongThuNhap : 0);
        model.addAttribute("tongSoKhachHang",tongSoKhachHang);
        model.addAttribute("tongSoDonHang", tongSoDonHang);
        model.addAttribute("tongSanPham", tongSanPham);
        model.addAttribute("sortByBD",sortByBD);
        model.addAttribute("sortBySP",sortBySP);
        model.addAttribute("sortBy",sortBy);
        //Thông kế số lần khách hàng mua trong khoảng thời gian
        // Nếu thiếu startDate hoặc endDate, cung cấp giá trị mặc định
        // Thêm ngày bắt đầu và ngày kết thúc vào model để hiển thị lại
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("orderStarts", orderStarts);
        //Lấy dữ liệu thu nhập và đơn hàng trong tháng hiện tại
        List<Object[]> thuNhapData = donHangService.getThuNhapTheoThang();
        List<Object[]> donHangData = donHangService.getDonHangTheoThang();
        //Lấy dữ liệu thu nhập và đơn hàng trong năm hiện tại
        List<Object[]> thuNhapDataTheoNam = donHangService.getThuNhapTheoNam();
        List<Object[]> donHangDataTheoNam = donHangService.getDonHangTheoNam();

        if("theoThang".equals(sortByBD)){
            //Tạo dataset cho biểu đỏ cột (Thu nhập)
            DefaultCategoryDataset thuNhapDataset = new DefaultCategoryDataset();
            for(Object[] data : thuNhapData){
                Integer day = (Integer) data[0];
                BigDecimal doanhThu = (BigDecimal) data[1];
                thuNhapDataset.addValue(doanhThu, "Thu nhập",  day);
            }
            //Tạo dataset cho biểu đồ đường (Số lượng đơn hàng)
            DefaultCategoryDataset donHangDataSet = new DefaultCategoryDataset();
            for(Object[] data : donHangData){
                Integer day = (Integer) data[0];
                Long donHang = (Long) data[1];
                donHangDataSet.addValue(donHang, "Đơn hàng",day);
            }
            //Tạo biểu đồ cột cho doanh thu
            JFreeChart chart = ChartFactory.createBarChart(
                    "Doanh thu", //Title
                    "Ngày", //X-Axis label
                    "Thu nhập", //Y-Axis label
                    thuNhapDataset, //Dataset
                    PlotOrientation.VERTICAL, //Plot orientation
                    true, //SHow legend
                    true, //Use tooltips
                    false //URLs?
            );

            //Thêm dataset cho biểu đồ đường (Số lượng đơn hàng)
            CategoryPlot plot = chart.getCategoryPlot();
            NumberAxis donHangAxis = new NumberAxis("Số lượng đơn hàng");
            plot.setRangeAxis(1, donHangAxis);
            plot.setDataset(1, donHangDataSet);
            plot.mapDatasetToRangeAxis(1,1);// Map đơn hàng lên trục thứ 2
            // Thiết lập renderer cho đơn hàng (biểu đồ đường)

            LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
            plot.setRenderer(1, lineRenderer);

            // Thiết lập renderer cho doanh thu (biểu đồ cột)
            BarRenderer barRenderer = new BarRenderer();
            plot.setRenderer(0, barRenderer);

            //Định dạng trục Y (Thu thập) với dấu chấm phân cách
            NumberAxis thuNhapAxis = (NumberAxis) plot.getRangeAxis();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.'); // Sử dụng dấu chấm làm phân cách hàng nghìn
            DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
            thuNhapAxis.setNumberFormatOverride(decimalFormat);

            // Đảm bảo biểu đồ đường nằm trên biểu đồ cột
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);        //Tạo file ảnh biểu đồ
            String filePath = request.getServletContext().getRealPath("/charts/") +"/revenue_chart.png";
            File chartFile = new File(filePath);
            //Tạo thư mụ nếu chưa tồn tại
            chartFile.getParentFile().mkdirs();
            //Lưu biểu đồ dưới dạng png
            ChartUtils.saveChartAsPNG(chartFile,chart,800,400);
            //Lấy đường dẫn của file ảnh
            String fileName = "charts/revenue_chart.png";
            model.addAttribute("chartFilename", fileName);
        }else{
            //Tạo dataset cho biểu đồ cột (thu nhập)
            DefaultCategoryDataset thuNhapDataset = new DefaultCategoryDataset();
            for(Object[] data : thuNhapDataTheoNam){
                Integer thang = (Integer) data[0];
                BigDecimal doanhThu = (BigDecimal) data[1];
                thuNhapDataset.addValue(doanhThu, "Thu nhập", "Tháng " + thang);
            }
            //Tạo dataset cho biểu đô đường (Số lượng đơn hàng)
            DefaultCategoryDataset donHangDataset = new DefaultCategoryDataset();
            for(Object[] data : donHangDataTheoNam){
                Integer thang = (Integer) data[0];
                Long donHang = (Long) data[1];
                donHangDataset.addValue(donHang, "Đơn hàng", "Tháng "+thang);
            }
            //Tạo biểu đồ cột cho doanh thu
            JFreeChart chart = ChartFactory.createBarChart(
                    "Doanh thu", //Title
                    "Tháng", //X-Axis label
                    "Thu nhập",//Y-Axis label
                    thuNhapDataset, //Dataset
                    PlotOrientation.VERTICAL, //Plot orientation
                    true, //Show legend
                    true, //use tooltips
                    false //urls?
            );
            //Thêm dataset cho biểu đồ đường (Số lượng đơn hàng)
            CategoryPlot plot = chart.getCategoryPlot();
            NumberAxis donHangAxis = new NumberAxis("Số lượng đơn hàng");
            plot.setRangeAxis(1, donHangAxis);
            plot.setDataset(1,donHangDataset);
            plot.mapDatasetToRangeAxis(1,1);//Map đơn hàng lên trục thứ 2
            // Thiết lập renderer cho đơn hàng (biểu đồ đường)
            LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
            plot.setRenderer(1, lineRenderer);

            // Thiết lập renderer cho doanh thu (biểu đồ cột)
            BarRenderer barRenderer = new BarRenderer();
            plot.setRenderer(0, barRenderer);
            //Định dạng trục Y (Thu thập) với dấu chấm phân cách
            NumberAxis thuNhapAxis = (NumberAxis) plot.getRangeAxis();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.'); // Sử dụng dấu chấm làm phân cách hàng nghìn
            DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
            thuNhapAxis.setNumberFormatOverride(decimalFormat);
            // Đảm bảo biểu đồ đường nằm trên biểu đồ cột
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);        //Tạo file ảnh biểu đồ
            String filePath = request.getServletContext().getRealPath("/charts/") +"/revenue_chart.png";
            File chartFile = new File(filePath);
            //Tạo thư mụ nếu chưa tồn tại
            chartFile.getParentFile().mkdirs();
            //Lưu biểu đồ dưới dạng png
            ChartUtils.saveChartAsPNG(chartFile,chart,800,400);
            //Lấy đường dẫn của file ảnh
            String fileName = "charts/revenue_chart.png";
            model.addAttribute("chartFilename", fileName);
        }

        //Biểu đồ tròn thống kế đơn hàng theo trạng thái
        List<Object[]> donHangTheoTrangThaiData = donHangService.getSoDonHangTheoTrangThai();
        //Tạo dataset cho biểu đồ tròn
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        for(Object[] data : donHangTheoTrangThaiData){
            String trangThai = (String) data[0]; //Trạng thái đơn hàng
            Long soLuong = (Long) data[1]; // Số lượng đon hàng theo trạng thái
            pieDataset.setValue(trangThai, soLuong); //Thêm giá trị vào dataset
        }
        //Tạo biểu đồ tròn cho đơn hàng theo trạng thái
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Đơn hàng theo trạng thái",
                pieDataset,
                true,
                true,
                false
        );
        //Cài đặt hiển thị phần trăm lên biểu đồ tròn
        PiePlot plot1 = (PiePlot) pieChart.getPlot();
        plot1.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} ({2})",new DecimalFormat("0"), new DecimalFormat("0.00%")
        )); //Hiển thị cả tên và %
        //Lưu biểu đồ dưới dạng PNG
        String pieChartFilePath = request.getServletContext().getRealPath("/charts/") + "/order_status_chart.png";
        File pieChartFile = new File(pieChartFilePath);
        pieChartFile.getParentFile().mkdirs(); //Tạo thư mục nếu chưa tồn tại
        ChartUtils.saveChartAsPNG(pieChartFile,pieChart,800,400);
        // Lấy đường đẫn file ảnh để hiển thị trong giao diện
        String pieChartFileName = "charts/order_status_chart.png";
        model.addAttribute("pieChartFilename", pieChartFileName);
        return "templateadmin/trangadmin";
    }
}
