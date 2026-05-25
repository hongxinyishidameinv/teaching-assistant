package com.xujc.mvcpro;

import com.xujc.mvcpro.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MvcProApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    public void checkLoginUser(){
        System.out.println(userService.login("admin", "123456"));
    }

}
