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
            // --- 修改开始 ---

            // 1. 定义文件存储的物理路径：项目根目录/uploads/
            // System.getProperty("user.dir") 获取项目根路径
            String projectPath = System.getProperty("user.dir");
            String uploadDir = projectPath + "/uploads/"; // 注意这里不需要 "file:" 前缀，这是给 Java IO 用的物理路径

            // 确保目录存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. 调用工具类保存文件 (工具类不需要改，它只负责把流写入路径)
            String savedName = FileUploadUtil.saveFile(uploadDir, multipartFile);

            // 3. 设置数据库路径
            // 因为我们在 WebMvcConfig 里把 /images/** 映射到了 uploads 目录
            // 所以数据库存 "images/文件名"，前端访问 "/images/文件名" 就能被映射到 uploads 下的文件
            product.setImageUrl("images/" + savedName);

            // --- 修改结束 ---
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
