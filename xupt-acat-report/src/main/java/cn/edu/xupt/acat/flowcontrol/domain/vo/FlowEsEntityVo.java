package cn.edu.xupt.acat.flowcontrol.domain.vo;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class FlowEsEntityVo implements Serializable {
    private FlowEsEntity entity;
    private int pageNum;
    private int pageSize;
}
