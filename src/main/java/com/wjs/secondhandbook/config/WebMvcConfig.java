package com.wjs.secondhandbook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取当前项目的根目录 (例如 D:\Code\secondhand-book)
        String projectPath = System.getProperty("user.dir");

        // 拼接上传文件夹路径 (确保以 / 结尾)
        // 这里的 "file:" 前缀告诉 Spring 这是一个文件系统路径，而不是 classpath
        String uploadPath = "file:" + projectPath + "/uploads/";

        // 配置映射：
        // 当访问 http://localhost:8080/images/xxx.jpg 时
        // 自动去项目根目录下的 uploads 文件夹里找 xxx.jpg
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);
    }
}
