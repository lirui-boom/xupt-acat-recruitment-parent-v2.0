package cn.edu.xupt.acat.lib.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class FlowSendInput implements Serializable {
    private String nid;
    private String username;
    private String email;
    private ApplyInfo applyInfo;
    private String workType;
}
