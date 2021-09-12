package cn.edu.xupt.acat.notices.service.impl;

import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.notices.dao.NoticeDao;
import cn.edu.xupt.acat.notices.domain.entity.TbNotice;
import cn.edu.xupt.acat.notices.library.AliyunEmailUtil;
import cn.edu.xupt.acat.notices.library.NoticeCodeEnum;
import cn.edu.xupt.acat.notices.library.NoticeUtil;
import cn.edu.xupt.acat.notices.service.NoticeSendService;
import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.logging.Logger;

@Service
public class NoticeSendServiceImpl implements NoticeSendService {

    private static Logger logger = Logger.getLogger(NoticeSendServiceImpl.class.toString());

    @Resource
    private NoticeDao noticeDao;

    @Override
    public void send(TbNotice tbNotice) {
        getParam(tbNotice);
        //1.send
        boolean b = AliyunEmailUtil.sendSampleEmail(tbNotice.getReceiveAddress(), tbNotice.getTitle(), tbNotice.getContent());
        //2.insert db
        tbNotice.setSendTime(NoticeUtil.getTime());
        int status = b ? 1 : -1;
        tbNotice.setStatus(status);
        noticeDao.insert(tbNotice);
    }

    @KafkaListener(topics = {"NOTICE_RECEIVE_TOPIC"})
    public void send(ConsumerRecord<String, String> record) {
        TbNotice notice = JSON.parseObject(record.value(), TbNotice.class);
        logger.info("receive notice from pipe, topic is NOTICE_RECEIVE_TOPIC.");
        send(notice);
    }

    /**
     * 参数校验
     * @param tbNotice tbNotice
     */
    private void getParam(TbNotice tbNotice) {

        if (tbNotice == null) {
            logger.warning("notice info is empty.");
            ExceptionCast.exception(NoticeCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), NoticeCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(tbNotice.getSendUser())) {
            logger.warning("notice send user is empty.");
            ExceptionCast.exception(NoticeCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), NoticeCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(tbNotice.getReceiveAddress())) {
            logger.warning("notice receive user address is empty.");
            ExceptionCast.exception(NoticeCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), NoticeCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(tbNotice.getReceiveUser())) {
            logger.warning("notice receive user is empty.");
            ExceptionCast.exception(NoticeCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), NoticeCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        logger.info("receive notice info is " + JSON.toJSONString(tbNotice));
    }
}
