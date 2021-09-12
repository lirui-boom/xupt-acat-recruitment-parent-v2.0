package cn.edu.xupt.acat.recruitment.domain.vo;

import cn.edu.xupt.acat.recruitment.domain.entity.TbRecruitment;
import lombok.Data;

import java.io.Serializable;

@Data
public class RecruitmentSearchVo implements Serializable {
    private TbRecruitment recruitment;
    private int pageNum;
    private int pageSize;
}
