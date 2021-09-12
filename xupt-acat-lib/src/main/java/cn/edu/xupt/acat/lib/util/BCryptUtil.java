package cn.edu.xupt.acat.lib.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


public class BCryptUtil {
    public static String encode(String password){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashPass = passwordEncoder.encode(password);
        return hashPass;
    }
    public static boolean matches(String password,String hashPass){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean f = passwordEncoder.matches(password, hashPass);
        return f;
    }

    public static void main(String[] args) {
        System.out.println(BCryptUtil.matches( "123456","$2a$10$O9B/cr9hYoi1mZGeZ/gtY.5doOMrMJIsrFF2XZSLEehCHyBZkerum"));
    }
}
