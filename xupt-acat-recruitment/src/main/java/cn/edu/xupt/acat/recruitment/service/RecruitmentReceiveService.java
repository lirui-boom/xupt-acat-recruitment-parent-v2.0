package cn.edu.xupt.acat.recruitment.service;

import cn.edu.xupt.acat.lib.model.FlowSendInput;
import cn.edu.xupt.acat.lib.response.R;

public interface RecruitmentReceiveService {
    public abstract R receive(FlowSendInput input);
}
