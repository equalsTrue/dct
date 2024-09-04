package com.dct.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component(value="PasswordEncoder")
public class PasswordEncoder {


    /**
     * 装载BCrypt密码编码器
     * @return
     */
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
