package cn.edu.xupt.acat.user.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TbUser implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String nickName;
    private String userPic;
    private String email;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
