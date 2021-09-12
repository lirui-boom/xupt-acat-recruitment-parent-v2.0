package cn.edu.xupt.acat.user.library;

import cn.edu.xupt.acat.lib.util.Util;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtil extends Util {


    /**
     * username生成规则
     * @param email
     * @return
     */
    public static String getUsername(String email) {
        return email.split("@")[0];
    }

    /**
     * 邮箱地址校验
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        String regExp = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    @Test
    public void test(){
        System.out.println(getUsername("openjava@sina.cn"));
    }
}
