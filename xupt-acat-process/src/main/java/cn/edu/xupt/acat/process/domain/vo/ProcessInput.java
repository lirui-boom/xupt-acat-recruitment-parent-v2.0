package cn.edu.xupt.acat.process.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProcessInput implements Serializable {
    private String username;
    private String snumber;
    private String realName;
    private String className;
    private Integer sex;
    private String phone;
    private String serviceLine;
    private Integer version;
    private String content;
}
