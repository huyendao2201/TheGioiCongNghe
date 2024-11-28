package com.example.thegioicongnghe.Admin.Controller;

import com.example.thegioicongnghe.Admin.Model.Product;
import com.example.thegioicongnghe.Admin.Model.ProductCategory;
import com.example.thegioicongnghe.Admin.Model.ProductImage;
import com.example.thegioicongnghe.Admin.Service.ProductCategoryService;
import com.example.thegioicongnghe.Admin.Service.ProductImageService;
import com.example.thegioicongnghe.Admin.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;
    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductController(ProductService productService, ProductImageService productImageService, ProductCategoryService productCategoryService) {
        this.productService = productService;
        this.productImageService = productImageService;
        this.productCategoryService = productCategoryService;
    }

    // Hiển thị danh sách sản phẩm
    @GetMapping("/index")
    public String listProducts(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("content", "admin/products/index"); // Thêm dòng này
        return "layouts/admin_layout"; // Trả về layout với nội dung
    }

    /**
     * Hiển thị trang thêm sản phẩm
     */
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        // Lấy danh sách danh mục
        List<ProductCategory> categories = productCategoryService.findAll();
        model.addAttribute("categories", categories);

        model.addAttribute("content", "admin/products/add"); // Thêm dòng này
        return "layouts/admin_layout"; // Tên của view form
    }

    // Thêm mới một sản phẩm với nhiều hình ảnh
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("mainImage") MultipartFile mainImage,
                             @RequestParam("extraImages") List<MultipartFile> extraImages) throws IOException {

        // Xử lý hình ảnh chính
        String mainImageName = "default.jpg";
        if (!mainImage.isEmpty()) {
            mainImageName = saveImage(mainImage, "uploads/product_images/main");
            product.setImageUrl(mainImageName);
        }

        // Lưu sản phẩm trước để lấy ID
        Product savedProduct = productService.save(product);

        // Xử lý các hình ảnh phụ
        if (extraImages != null && !extraImages.isEmpty()) {
            for (MultipartFile extraImage : extraImages) {
                if (!extraImage.isEmpty()) {
                    String extraImageName = saveImage(extraImage, "uploads/product_images/extra");
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(extraImageName);
                    productImage.setProduct(savedProduct);
                    productImageService.saveImage(productImage);
                }
            }
        }

        return "redirect:/admin/products/index";
    }


    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable int id, Model model) {
        Product product = productService.findById(id).orElse(null);
        List<ProductCategory> categories = productCategoryService.findAll();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("content", "admin/products/edit");
        return "layouts/admin_layout";
    }
    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable int id,@ModelAttribute Product product,
                              @RequestParam("mainImage") MultipartFile mainImage,
                              @RequestParam("subImages") MultipartFile[] subImages,
                              @RequestParam(value = "removeImages", required = false) List<Integer> removeImageIds) throws IOException {


        // Xử lý thay đổi hình ảnh chính
        if (!mainImage.isEmpty()) {
            String mainImageName = mainImage.getOriginalFilename();
            Path mainImagePath = Paths.get("uploads/product_images/main/" + mainImageName);
            Files.copy(mainImage.getInputStream(), mainImagePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImageUrl(mainImageName);
        }

        // Xóa hình ảnh phụ được chọn
        if (removeImageIds != null) {
            for (Integer imageId : removeImageIds) {
                ProductImage image = productImageService.findImageById(imageId);
                File fileToDelete = new File("uploads/product_images/extra/" + image.getImageUrl());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
                productImageService.deleteImageById(imageId);
            }
        }

        // Thêm hình ảnh phụ mới
        for (MultipartFile subImage : subImages) {
            if (!subImage.isEmpty()) {
                String subImageName = subImage.getOriginalFilename();
                Path subImagePath = Paths.get("uploads/product_images/extra/" + subImageName);
                Files.copy(subImage.getInputStream(), subImagePath, StandardCopyOption.REPLACE_EXISTING);

                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setImageUrl(subImageName);
                productImageService.saveImage(productImage);
            }
        }
        product.setProductId(id);
        // Lưu thông tin sản phẩm
        productService.save(product);
        return "redirect:/admin/products/index";
    }



    /**
     * Phương thức lưu hình ảnh
     *
     * @param file          File hình ảnh tải lên
     * @param uploadDirPath Thư mục lưu hình ảnh
     * @return Tên file được lưu
     * @throws IOException Nếu xảy ra lỗi khi lưu file
     */

    private String saveImage(MultipartFile file, String uploadDirPath) throws IOException {
        // Tạo thư mục nếu chưa tồn tại
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Lưu file vào thư mục
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir.getAbsolutePath() + File.separator + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

}
