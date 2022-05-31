package cn.edu.xupt.acat.recruitment.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueueSearchVo implements Serializable {
    private String serviceLine;
    private int turns;
    private int version;
    private int pageNum;
    private int pageSize;
}
