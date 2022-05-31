package cn.edu.xupt.acat.lib.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApplyInfo implements Serializable {
    private String nid;
    private String username;
    private String snumber;
    private String realName;
    private String className;
    private Integer sex;
    private String phone;
    private String content;
}
