package com.example.fsneaker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrangKhachHangController {
    @GetMapping("/trangkhachhang")
    public String trangKhachHang(Model model){
        return "templatekhachhang/trangchukhachhang";
    }
}
