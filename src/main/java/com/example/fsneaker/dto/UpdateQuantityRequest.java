package com.example.fsneaker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateQuantityRequest {
    private Integer idDonHangChiTiet;
    private Integer soLuong;
    private Integer idSanPhamChiTiet;
}
