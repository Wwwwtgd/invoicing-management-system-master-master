package com.px.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 启动器
 */

@SpringBootApplication

@MapperScan(basePackages = {"com.px.**.mapper", "com.baomidou.mybatisplus.core.mapper"})
public class JxcAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(JxcAdminApplication.class,args);
        //System.out.println(new BCryptPasswordEncoder().encode("123456"));

    }
}
