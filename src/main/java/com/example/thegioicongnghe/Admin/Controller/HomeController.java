package com.example.thegioicongnghe.Admin.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")  // Chỉ định HomeController là Controller
public class HomeController {
    // Khi user truy cập vào endpoint / thì homepage() được gọi
    @GetMapping("")
    public String homepage() {
        return "layouts/admin_layout";  // Trả về trang index.html
    }

    // Có thể mapping thêm các endpoint khác nữa...
}