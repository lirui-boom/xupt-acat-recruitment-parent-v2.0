package cn.edu.xupt.acat.lib.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class FlowReceiveInput implements Serializable {
    private boolean isRpc;
    private String workType;
    //本次流程返回结果
    private Integer moduleStatus;
    private String username;
    private String email;
    private String serviceLine;
    private Integer version;
    private String nid;
    //生命周期
    private Integer ttl;
    private ApplyInfo applyInfo;
}
