package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Created by Meng Kai
 * @Date 2020/11/21
 */
@SpringBootApplication
@MapperScan("com.address.mapper")
public class AddressTest {
    public static void main(String[] args) {
        SpringApplication.run(AddressTest.class);
    }
}
