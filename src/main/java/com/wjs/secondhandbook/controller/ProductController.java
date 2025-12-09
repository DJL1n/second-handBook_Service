package com.wjs.secondhandbook.controller;

import com.wjs.secondhandbook.model.Product;
import com.wjs.secondhandbook.model.User;
import com.wjs.secondhandbook.repository.ProductRepository;
import com.wjs.secondhandbook.repository.UserRepository;
import com.wjs.secondhandbook.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    /**
     * 处理【新增】或【编辑】商品
     */
    @PostMapping("/save")
    public String saveProduct(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("title") String title,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile multipartFile,
            Authentication auth) throws Exception {

        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Product product;

        // 1. 判断是新增还是修改
        if (id != null) {
            // 修改模式：先查旧数据
            product = productRepository.findById(id).orElseThrow();
            // 安全检查：只能改自己的书
            if (!product.getSellerId().equals(user.getUserId())) {
                throw new RuntimeException("无权修改");
            }
        } else {
            // 新增模式
            product = new Product();
            product.setSellerId(user.getUserId());
            product.setCreatedAt(LocalDateTime.now());
            product.setStatus("ON_SALE"); // 默认上架
        }

        // 2. 更新基本信息
        product.setTitle(title);
        product.setPrice(price);
        product.setDescription(description);

        // 3. 处理图片上传
        if (!multipartFile.isEmpty()) {
            // 获取项目根目录下的 static/images 路径
            // 注意：这里为了演示简单，通过获取 classpath 路径处理
            // 实际生产环境通常配置外部路径映射

            String fileName = multipartFile.getOriginalFilename();
            // 这里我们尝试保存到 target/classes/static/images (运行时目录)
            // 这样不用重启就能看到图片
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            if(!path.exists()) path = new File("");
            File uploadDir = new File(path.getAbsolutePath(), "static/images/");

            if(!uploadDir.exists()) uploadDir.mkdirs();

            String uploadPath = uploadDir.getAbsolutePath();

            // 调用工具类保存
            String savedName = FileUploadUtil.saveFile(uploadPath, multipartFile);

            // 设置数据库路径 (前端访问路径)
            product.setImageUrl("images/" + savedName);
        }

        productRepository.save(product);

        return "redirect:/my-shelf";
    }

    /**
     * API: 处理【下架】或【重新上架】
     */
    @ResponseBody
    @PostMapping("/status/{id}")
    public Map<String, Object> changeStatus(@PathVariable Integer id, @RequestParam String status, Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        try {
            User user = userRepository.findByUsername(auth.getName()).orElseThrow();
            Product product = productRepository.findById(id).orElseThrow();

            if (!product.getSellerId().equals(user.getUserId())) {
                res.put("success", false);
                res.put("message", "无权操作");
                return res;
            }

            // 只有这两种状态允许切换
            if ("ON_SALE".equals(status) || "OFF_SHELF".equals(status)) {
                product.setStatus(status);
                productRepository.save(product);
                res.put("success", true);
            } else {
                res.put("success", false);
                res.put("message", "状态不合法");
            }
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
        }
        return res;
    }
}
