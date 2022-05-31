package cn.edu.xupt.acat.flowcontrol.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FlowEsEntity implements Serializable {
    private String id;
    private String nid;
    private String username;
    private String nickName;
    private String snumber;
    private String realName;
    private String className;
    private String phone;
    private String opUser;
    private String email;
    private String serviceLine;
    private Integer version;
    private Integer turns;
    //流程状态
    private String workType;
    //流程是否结束
    private Integer status;
    //报名时间
    private Date applyTime;
}
