package cn.edu.xupt.acat.flowcontrol.domain.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

@Data
public class FlowEsEntity implements Serializable {
    private Long id;
    private String nid;
    private Long userId;
    //user info
    private String username;
    private String password;
    private String nickName;
    private String userPic;
    private String email;
    private Integer userStatus;
    private Date userCreateTime;
    private Date userUpdateTime;
    //apply info
    private String snumber;
    private String realName;
    private String className;
    private Integer sex;
    private String phone;
    private String applyContent;
    private Date applyCreateTime;
    private Date applyUpdateTime;

    private String opUser;
    private String serviceLine;
    private Integer version;
    private Integer turns;
    //流程状态
    private String workType;
    //流程是否结束
    private Integer status;
}
