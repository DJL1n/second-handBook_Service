package com.wjs.secondhandbook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根路径 (例如 D:\Code\secondhandBook)
        String projectPath = System.getProperty("user.dir");

        // 拼接上传文件夹路径
        String uploadPath = "file:" + projectPath + "/uploads/";

        // 🔥 核心修改：addResourceLocations 可以传多个路径
        // 逻辑是：先去外面的 uploads 找 -> 找不到就去 classpath 下的 static/images 找
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath, "classpath:/static/images/");
    }
}
