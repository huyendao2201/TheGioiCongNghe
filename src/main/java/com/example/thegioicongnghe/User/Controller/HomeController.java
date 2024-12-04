package com.example.thegioicongnghe.User.Controller;

import com.example.thegioicongnghe.Admin.Model.*;
import com.example.thegioicongnghe.Admin.Service.*;
import com.example.thegioicongnghe.config.CustomUserDetails;
import com.example.thegioicongnghe.config.SecurityConfig;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.security.Principal;
import java.util.List;

@Controller("userHomeController")  // Chỉ định HomeController là Controller
@RequestMapping("/")  // Chỉ định HomeController là Controller
public class HomeController {
    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PostService postService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Khi user truy cập vào endpoint / thì homepage() được gọi
    @GetMapping("/")
    public String homepage(Model model, Principal p) {

        List<ProductCategory> categories = productCategoryService.findAll();

        List<Product> products = productService.findAll();

        List<Product> latestProducts = productService.getLatestProducts(); // lấy ra 10 sản phẩm mới nhất
        model.addAttribute("latestProducts", latestProducts);

        model.addAttribute("products", products); // hiển thị sản phẩm
        model.addAttribute("categories", categories); // hiển thị danh sách danh mục

        // Dropdown giỏ hàng
        UserDtls user = getLoggedInUserDetails(p);
        List<CartItem> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        model.addAttribute("content", "user/fragments/cart_dropdown"); // Thêm dòng này

        model.addAttribute("content", "user/fragments/explore_product"); // truyền dữ liệu vào view
        model.addAttribute("content", "user/fragments/categories"); // Thêm dòng này
        return "layouts/main_layout"; // Trả về layout với nội dung
    }


    @GetMapping("/product-detail/{productSlug}") // Trang chi tiết sản phẩm
    public String showProductDetail(@PathVariable String productSlug, Model model) {
        Product product = productService.findProductBySlug(productSlug);

        model.addAttribute("product", product);

        List<ProductCategory> categories = productCategoryService.findAll();
        model.addAttribute("categories", categories); // hiển thị danh sách danh mục

        // model.addAttribute("content", "user/fragments/single-product"); // Thêm dòng này
        return "user/fragments/single-product"; // Trả về layout với nội dung
    }

    @GetMapping("/posts") // Trang chi tiết sản phẩm
    public String postPage(Model model) {

        List<BlogPost> posts = postService.findAll();
        model.addAttribute("posts", posts);

        List<BlogPost> latestPosts = postService.getLatestBlogPosts(); // Lấy ra 3 posts mới nhất
        model.addAttribute("latestPosts", latestPosts);


        return "user/post"; // Trả về layout với nội dung
    }

    @GetMapping("/post-details/{slug}")
    public String showPostsDetail(@PathVariable String slug, Model model) {

        BlogPost post = postService.findPostBySlug(slug);

        // Kiểm tra slug để đảm bảo URL chính xác
        if (post == null || !post.getSlug().equals(slug)) {
            return "redirect:/404"; // Điều hướng đến trang lỗi 404 nếu không khớp
        }

        model.addAttribute("post", post);

        List<BlogPost> latestPosts = postService.getLatestBlogPosts(); // Lấy ra 3 posts mới nhất
        model.addAttribute("latestPosts", latestPosts);

        return "user/single-post";
    }

    @GetMapping("/shop") // Trang chi tiết sản phẩm
    public String shopPage(Model model) {

        //Lấy và hiển thị danh sách sản phẩm
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);

        //Lấy và hiển thị danh sách danh mục sản phẩm
        List<ProductCategory> productCategories = productCategoryService.findAll();
        model.addAttribute("productCategories", productCategories);

        return "user/shop"; // Trả về layout với nội dung
    }
    private UserDtls getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        UserDtls userDtls = userService.getUserByEmail(email);
        return userDtls;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/add-to-cart")
    public String addToCart(@RequestParam Integer pid, HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Lấy userId từ CustomUserDetails
        int userId = userDetails.getUserId();

        // Thực hiện thêm sản phẩm vào giỏ hàng
        CartItem savecart = cartService.saveCart(pid, userId);

        if (savecart == null) {
            session.setAttribute("errorMsg", "Product add to cart failed");
        } else {
            session.setAttribute("succMsg", "Product added to cart");
        }

        return "user/shop";
    }

    @GetMapping("/cart-review")
    public String loadCartReviewPage(Principal p, Model m) {

        UserDtls user = getLoggedInUserDetails(p);
        List<CartItem> carts = cartService.getCartsByUser(user.getId());
        m.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalPrice();
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        m.addAttribute("content", "user/fragments/cart_dropdown"); // Thêm dòng này
        return "layouts/main_layout";
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m) {

        UserDtls user = getLoggedInUserDetails(p);
        List<CartItem> carts = cartService.getCartsByUser(user.getId());
        m.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalPrice();
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
        cartService.updateQuantity(sy, cid);
        return "redirect:/cart";
    }


    // Xóa sản phẩm khỏi giỏ
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam int productId, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            return "Cart is empty!";
        }

        session.setAttribute("cart", cart);

        return "Product removed from cart!";
    }

    // Xóa toàn bộ giỏ hàng
    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "Cart cleared!";
    }
    // Xóa toàn bộ giỏ hàng
    @GetMapping("/checkout")
    public String checkOut(HttpSession session) {
        return "user/checkout";
    }


    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDtls());
        return "user/register";
    }

    // Xử lý dữ liệu từ form
    @PostMapping("/register")
    public String saveUser(
            @ModelAttribute UserDtls user,
            @RequestParam("Image") MultipartFile Image,
            HttpSession session) throws IOException {

        // Kiểm tra email đã tồn tại
        if (userService.existsEmail(user.getEmail())) {
            session.setAttribute("errorMsg", "Email already exists");
            return "redirect:/register";
        } else {
            String imageName = "default.jpg";
            if (!Image.isEmpty()) {
                imageName = Image.getOriginalFilename();
                user.setProfileImage(imageName);

                // Thư mục bên ngoài `resources`
                File uploadsDir = new File("uploads/profile_img");

                // Tạo thư mục nếu chưa tồn tại
                if (!uploadsDir.exists()) {
                    uploadsDir.mkdirs();
                }

                // Đường dẫn lưu file
                Path path = Paths.get(uploadsDir.getAbsolutePath() + File.separator + Image.getOriginalFilename());

                // Lưu file
                Files.copy(Image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            userService.saveUser(user);
        }
        return "user/login";
    }


    // Hiển thị form đăng nhập
    @GetMapping("/signin")
    public String showLoginForm() {
        return "user/login";
    }

    // kiểm tra người dùng đã đăng nhập hay chưa
    @GetMapping("/current-user")
    public String getCurrentUser(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
            return "user/user"; // trả về tên của template (VD: user.html)
        }
        return "user/login"; // hoặc một trang mặc định khác
    }


}