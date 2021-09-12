package cn.edu.xupt.acat.recruitment.domain.vo;

import cn.edu.xupt.acat.lib.model.ApplyInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class QueueNodeVo implements Serializable {
    private String nid;
    private ApplyInfo applyInfo;
    private String workType;
    private String itwUser;
    private boolean isItwing = false;
}
