package com.example.thegioicongnghe.User.Controller;

import com.example.thegioicongnghe.Admin.Model.Product;
import com.example.thegioicongnghe.Admin.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.List;

@Controller
@RequestMapping("/sitemap")
public class SitemapController {

    @Autowired
    private ProductRepository productRepository;  // Giả sử bạn có một repository để truy vấn sản phẩm

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> generateSitemap() {
        List<Product> products = productRepository.findAll();  // Lấy danh sách sản phẩm từ cơ sở dữ liệu

        StringBuilder sitemap = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // Thêm trang chủ
        sitemap.append("<url>\n")
                .append("<loc>").append("https://example.com/").append("</loc>\n")
                .append("<lastmod>").append(java.time.LocalDate.now()).append("</lastmod>\n")
                .append("<changefreq>daily</changefreq>\n")
                .append("<priority>1.0</priority>\n")
                .append("</url>\n");

        // Thêm các sản phẩm vào sitemap
        for (Product product : products) {
            sitemap.append("<url>\n")
                    .append("<loc>").append("https://example.com/products/").append(product.getProductSlug()).append("</loc>\n")
                    .append("<lastmod>").append(java.time.LocalDate.now()).append("</lastmod>\n")
                    .append("<changefreq>weekly</changefreq>\n")
                    .append("<priority>0.8</priority>\n")
                    .append("</url>\n");
        }

        sitemap.append("</urlset>");

        return ResponseEntity.ok(sitemap.toString());
    }
}
