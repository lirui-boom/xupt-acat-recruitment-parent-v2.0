package cn.edu.xupt.acat.logs.service.impl;

import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.lib.util.Util;
import cn.edu.xupt.acat.logs.dao.LogsDao;
import cn.edu.xupt.acat.logs.domain.entity.TbLogs;
import cn.edu.xupt.acat.logs.library.LogsCodeEnum;
import cn.edu.xupt.acat.logs.service.LogsService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.logging.Logger;

@Service
@Component
public class LogsServiceImpl implements LogsService {

    private static Logger logger = Logger.getLogger(LogsServiceImpl.class.toString());

    @Resource
    private LogsDao logsDao;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    @Transactional
    public R add(TbLogs logs) {
        getAddParam(logs);
        doAddLogs(logs);
        return R.ok();
    }

    @Transactional
    @KafkaListener(topics = {"LOGS_ADD_RECEIVE"})
    public void add(ConsumerRecord<String, String> record) {
        TbLogs logs = JSON.parseObject(record.value(), TbLogs.class);
        logger.info("receive logs from pipe, topic is LOGS_ADD_RECEIVE.");
        add(logs);
    }

    /**
     * 参数校验
     * @param logs
     */
    private void getAddParam(TbLogs logs) {

        if (logs == null) {
            logger.warning(" log info is empty.");
            ExceptionCast.exception(LogsCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), LogsCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(logs.getNid())) {
            logger.warning(" log nid is empty.");
            ExceptionCast.exception(LogsCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), LogsCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(logs.getUsername())) {
            logger.warning(" log user is empty.");
            ExceptionCast.exception(LogsCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), LogsCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(logs.getOpUser())) {
            logger.warning(" log operation user is empty.");
            ExceptionCast.exception(LogsCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), LogsCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(logs.getWorkType())) {
            logger.warning(" log work type is empty.");
            ExceptionCast.exception(LogsCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), LogsCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        logger.info("receive logs param is :" + JSON.toJSONString(logs));
    }

    /**
     * 数据存储
     * @param logs
     */
    private void doAddLogs(TbLogs logs) {
        logs.setOpTime(Util.getTime());
        logsDao.insert(logs);
        IndexQuery condition = buildEsCondition(logs);
        IndexCoordinates indexCoordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(TbLogs.class);
        elasticsearchRestTemplate.index(condition,indexCoordinates);
        logger.info("insert db and es with data : " + JSON.toJSONString(logs));
    }

    /**
     * 构造插入ES条件
     * @param logs
     * @return
     */
    private IndexQuery buildEsCondition(TbLogs logs) {
        return  new IndexQueryBuilder()
                .withId(Long.toString(logs.getId()))
                .withObject(logs)
                .build();
    }

}
