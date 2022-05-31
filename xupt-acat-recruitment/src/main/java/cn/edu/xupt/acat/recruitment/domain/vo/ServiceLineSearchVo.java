package cn.edu.xupt.acat.recruitment.domain.vo;

import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceLineSearchVo implements Serializable {
    private TbServiceLine serviceLine;
    private int pageNum;
    private int pageSize;
}
