package com.quanwei.network;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan   //允许@WebServlet、@WebFilter、@WebListener注解自动注册
public class NetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkApplication.class, args);
    }

}

