package cn.edu.xupt.acat.recruitment.service.impl;

import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.model.FlowModel;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.lib.util.Util;
import cn.edu.xupt.acat.recruitment.dao.ServiceLineDao;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.recruitment.library.RecruitmentCodeEnum;
import cn.edu.xupt.acat.recruitment.library.RecruitmentConstant;
import cn.edu.xupt.acat.recruitment.service.ServiceLineAddService;
import cn.edu.xupt.acat.recruitment.service.ServiceLineSearchService;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ServiceLineAddServiceImpl implements ServiceLineAddService {

    private static Logger logger = Logger.getLogger(ServiceLineAddServiceImpl.class.toString());

    @Resource
    private ServiceLineDao serviceLineDao;

    @Resource
    private ServiceLineSearchService serviceLineSearchService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    @Transactional
    public R addServiceLine(TbServiceLine serviceLine) {
        getParam(serviceLine);
        if (checkExist(serviceLine)) {
            logger.warning("service line info was created, service_line = " + serviceLine.getServiceLine() + " and version = " + serviceLine.getVersion());
            return R.error(RecruitmentCodeEnum.DATA_EXISTED_EXCEPTION.getCode(), RecruitmentCodeEnum.DATA_EXISTED_EXCEPTION.getMsg());
        }
        doAddServiceLine(serviceLine);
        return R.ok().put("data", serviceLine);
    }

    /**
     * 参数校验
     * @param serviceLine
     */
    private void getParam(TbServiceLine serviceLine) {

        if (serviceLine == null) {
            logger.warning("service line info is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(serviceLine.getServiceLine())) {
            logger.warning("service line name is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (serviceLine.getQps() == null) {
            logger.warning("service line qps is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (serviceLine.getQps() <= 0) {
            serviceLine.setQps(3);
            logger.warning("service line qps can not set <= 0, reset 3");
        }

        if (serviceLine.getQps() >= 30) {
            serviceLine.setQps(30);
            logger.warning("service line qps can not beyond 30, reset 30");
        }

        if (serviceLine.getTurns() == null) {
            logger.warning("service line turns is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (serviceLine.getTurns() <= 0) {
            serviceLine.setTurns(1);
            logger.warning("service line turns can not set <= 0, rest 1");
        }

        if (StringUtils.isEmpty(serviceLine.getAuthKey())) {
            logger.warning("service line auth key is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (serviceLine.getVersion() == null) {
            logger.warning("service line version is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        logger.info("add service line with info :" + JSON.toJSONString(serviceLine));
    }

    /**
     * 数据存储
     * @param serviceLine
     */
    private void doAddServiceLine(TbServiceLine serviceLine) {
        //生成流转细节
        serviceLine.setWorkDetails(JSON.toJSONString(getWorkDetails(serviceLine)));
        serviceLine.setStatus(1);
        Date now = Util.getTime();
        serviceLine.setCreateTime(now);
        serviceLine.setUpdateTime(now);
        //insert db & es
        serviceLineDao.insert(serviceLine);
        IndexQuery condition = buildEsCondition(serviceLine);
        IndexCoordinates indexCoordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(TbServiceLine.class);
        elasticsearchRestTemplate.index(condition,indexCoordinates);
        logger.info("insert into db and es whit data : " + JSON.toJSONString(serviceLine));
    }

    /**
     * 数据校验
     * @param serviceLine
     * @return
     */
    private boolean checkExist(TbServiceLine serviceLine) {
        TbServiceLine query = new TbServiceLine();
        query.setServiceLine(serviceLine.getServiceLine());
        query.setVersion(serviceLine.getVersion());
        List<TbServiceLine> serviceLines = serviceLineSearchService.query(query);
        if (serviceLines != null && serviceLines.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 根据面试轮次生成流转细节
     * @param serviceLine
     * @return
     */
    private List<FlowModel> getWorkDetails(TbServiceLine serviceLine) {

        List<FlowModel> work = new ArrayList<>();

        FlowModel apply = new FlowModel(RecruitmentConstant.WORK_NAME_APPLY, RecruitmentConstant.WORK_TYPE_PASS + "_1");
        work.add(apply);

        for (int i = 0; i < serviceLine.getTurns(); i++){

            FlowModel recruit = null;

            if (i == serviceLine.getTurns() - 1) {
                recruit = new FlowModel(RecruitmentConstant.WORK_TYPE_PASS + "_" + (i + 1), null);
            } else {
                recruit = new FlowModel(RecruitmentConstant.WORK_TYPE_PASS + "_" + (i + 1), RecruitmentConstant.WORK_TYPE_PASS + "_" + (i + 2));
            }
            work.add(recruit);
        }
        logger.info("generate work details : " + JSON.toJSONString(work));
        return work;
    }

    private IndexQuery buildEsCondition(TbServiceLine serviceLine) {
        return  new IndexQueryBuilder()
                .withId(Long.toString(serviceLine.getId()))
                .withObject(serviceLine)
                .build();
    }

    @Test
    public void test() {
        TbServiceLine serviceLine = new TbServiceLine();
        serviceLine.setTurns(3);
        getWorkDetails(serviceLine);
    }
}
