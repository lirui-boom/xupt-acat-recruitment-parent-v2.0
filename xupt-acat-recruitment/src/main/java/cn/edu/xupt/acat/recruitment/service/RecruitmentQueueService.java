package cn.edu.xupt.acat.recruitment.service;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.lib.response.SearchPage;
import cn.edu.xupt.acat.recruitment.domain.vo.QueueNodeVo;
import cn.edu.xupt.acat.recruitment.domain.vo.QueueSearchVo;

import java.util.List;

public interface RecruitmentQueueService {

    public abstract R sign(String nid);

    public abstract R signUp(String nid);

    public abstract R getTask(String nid, String opUser);

    public abstract R release(String nid, String opUser);

    public abstract R getCount(String serviceLine, String version, int turns);

    public abstract SearchPage<List<QueueNodeVo>> getList(QueueSearchVo vo);
}
