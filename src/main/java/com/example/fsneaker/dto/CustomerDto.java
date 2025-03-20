package com.example.fsneaker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CustomerDto {
    @NotEmpty(message = "Tên khách hàng không được để trống!")
    @Size(min = 5, max = 30, message = "Tên khách hàng phải từ 5 đến 30 ký tự!")
    private String ten;

    @NotEmpty(message = "Email không được để trống!")
    @Email(message = "Email không đúng định dạng!")
    private String email;

    @NotEmpty(message = "Số điện thoại không được để trống!")
    @Size(min = 10, max = 10, message = "Số điện thoại phải có đúng 10 chữ số!")
    @Pattern(regexp = "^\\d+$", message = "Số điện thoại chỉ được chứa chữ số!")
    private String sdt;

    @NotEmpty(message = "Địa chỉ không được để trống!")
    @Size(min = 5, max = 100, message = "Địa chỉ phải từ 5 đến 100 ký tự!")
    private String diachi;

    @NotEmpty(message = "Mật khẩu không được để trống!")

    private String matkhau;

    @NotEmpty(message = "Mật khẩu xác nhận không được để trống!")
    private String matkhauxacnhan;
}
