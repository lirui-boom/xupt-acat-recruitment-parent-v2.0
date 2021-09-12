package cn.edu.xupt.acat.recruitment.service;

import cn.edu.xupt.acat.lib.response.R;

public interface RecruitmentResolveService {
    public abstract R resolve(String nid, int status, String opUser);
}
