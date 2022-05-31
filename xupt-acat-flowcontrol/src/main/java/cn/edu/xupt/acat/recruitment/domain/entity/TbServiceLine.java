package cn.edu.xupt.acat.recruitment.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TbServiceLine implements Serializable {
    private Long id;
    private String serviceLine;
    private Integer qps;
    private String workDetails;
    private Integer turns;
    private String authKey;
    private String desc;
    private Integer status;
    private Integer version;
    private Date createTime;
    private Date updateTime;
}
