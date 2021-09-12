package cn.edu.xupt.acat.recruitment.service;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.domain.entity.TbEvaluate;

public interface RecruitmentSubmitService {
    public abstract R submit(TbEvaluate evaluate);
}
