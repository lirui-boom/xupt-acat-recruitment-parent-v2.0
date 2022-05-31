package cn.edu.xupt.acat.recruitment.service;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.domain.entity.TbRecruitment;

public interface RecruitmentService {

    public abstract String getNid(String username);

    public abstract TbRecruitment getRecruitment(String nid, String workType);
}
