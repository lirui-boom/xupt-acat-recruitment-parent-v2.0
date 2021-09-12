package cn.edu.xupt.acat.recruitment.service.impl;

import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.dao.ServiceLineDao;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.recruitment.library.RecruitmentCodeEnum;
import cn.edu.xupt.acat.recruitment.service.ServiceLineSearchService;
import cn.edu.xupt.acat.recruitment.service.ServiceLineUpdateService;
import com.alibaba.fastjson.JSON;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ServiceLineUpdateServiceImpl implements ServiceLineUpdateService {

    private static Logger logger = Logger.getLogger(ServiceLineUpdateServiceImpl.class.toString());

    @Resource
    private ServiceLineDao serviceLineDao;

    @Resource
    private ServiceLineSearchService serviceLineSearchService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public R update(TbServiceLine input) {
        getParam(input);
        //查询serviceLine数据
        TbServiceLine serviceLine = getServiceLine(input.getId());
        if (serviceLine == null) {
            logger.warning("service line info doesn't exist, query service line id is " + serviceLine.getId());
            ExceptionCast.exception(RecruitmentCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg(), RecruitmentCodeEnum.DATA_NOT_EXIST_EXCEPTION.getCode());
        }
        //构造数据
        TbServiceLine updateData = buildUpdateDate(serviceLine,input);
        //修改数据DB and ES
        doUpdate(updateData);
        return R.ok();
    }

    /**
     * 参数校验
     * @param serviceLine serviceLine
     */
    private void getParam(TbServiceLine serviceLine) {

        if (serviceLine == null) {
            logger.warning("service line info is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (serviceLine.getId() == null) {
            logger.warning("service line id is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        logger.info("receive service line info is : " + JSON.toJSONString(serviceLine));
    }

    private TbServiceLine getServiceLine(Long id) {
        TbServiceLine query = new TbServiceLine();
        query.setId(id);
        List<TbServiceLine> serviceLines = serviceLineSearchService.query(query);
        if (serviceLines != null && serviceLines.size() > 0) {
            return serviceLines.get(0);
        }
        return null;
    }

    private TbServiceLine buildUpdateDate(TbServiceLine serviceLine, TbServiceLine input) {

        if (input.getQps() != null) {
            serviceLine.setQps(input.getQps());
        }

        if (input.getStatus() != null) {
            serviceLine.setStatus(input.getStatus());
        }

        return serviceLine;
    }

    private void doUpdate(TbServiceLine serviceLine) {
        //改db
        serviceLineDao.updateById(serviceLine);
        //改es
        IndexQuery condition = buildEsCondition(serviceLine);
        IndexCoordinates indexCoordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(TbServiceLine.class);
        elasticsearchRestTemplate.index(condition,indexCoordinates);

    }

    private IndexQuery buildEsCondition(TbServiceLine serviceLine) {
        return  new IndexQueryBuilder()
                .withId(Long.toString(serviceLine.getId()))
                .withObject(serviceLine)
                .build();
    }


}
