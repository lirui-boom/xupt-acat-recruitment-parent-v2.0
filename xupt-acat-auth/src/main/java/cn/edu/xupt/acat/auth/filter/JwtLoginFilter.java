package cn.edu.xupt.acat.auth.filter;

import cn.edu.xupt.acat.auth.config.RsaConfiguration;
import cn.edu.xupt.acat.lib.exception.RRException;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.lib.util.BCryptUtil;
import cn.edu.xupt.acat.lib.util.JwtUtils;
import cn.edu.xupt.acat.user.domain.entity.TbUser;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

//认证
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private RsaConfiguration prop;

    public JwtLoginFilter(AuthenticationManager authenticationManager, RsaConfiguration prop) {
        this.authenticationManager = authenticationManager;
        this.prop = prop;
    }

    //验证用户名及密码
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            TbUser tbUser = new ObjectMapper().readValue(request.getInputStream(), TbUser.class);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(tbUser.getUsername(), tbUser.getPassword());
            return authenticationManager.authenticate(authRequest);
        } catch (Exception e) {
            try {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();
                R r = R.error(400,"用户名或密码错误！");
                out.write(new ObjectMapper().writeValueAsString(r));
                out.flush();
                out.close();
            } catch (Exception outEx) {
                outEx.printStackTrace();
            }
            e.printStackTrace();
            throw new RRException("用户名或密码错误",400);
        }

    }

    //验证通过 生成并返回token
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //设置要返回的user信息
        TbUser tbUser = new TbUser();
        tbUser.setUsername(authResult.getName());
//        tbUser.setRoleList((List<TbRole>) authResult.getAuthorities());

        System.out.println("json:" + JSON.toJSONString(tbUser));

        //24小时过期
        String token = JwtUtils.generateTokenExpireInMinutes(tbUser, prop.getPrivateKey(), 24 * 60);
        //设置token到响应头信息中
        response.addHeader("Authorization", "Bearer " + token);
        try {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            R r = R.ok().put("msg", "认证通过！").put("code", 200).put("username", tbUser.getUsername());
            out.write(new ObjectMapper().writeValueAsString(r));
            out.flush();
            out.close();
        } catch (Exception outEx) {
            outEx.printStackTrace();
        }
    }


}
