package cn.edu.xupt.acat.notices.service;

import cn.edu.xupt.acat.notices.domain.entity.TbNotice;

public interface NoticeSendService {

    public abstract void send(TbNotice tbNotice);
}
