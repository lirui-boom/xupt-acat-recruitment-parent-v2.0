package cn.edu.xupt.acat.recruitment.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendNoticeVo implements Serializable {
    private String nid;
    private String realName;
    private String workType;
    private String opUser;
    private String receiveAddress;
    private Integer month;
    private Integer day;
    private Integer start;
    private Integer end;
}
