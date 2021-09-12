package cn.edu.xupt.acat.flowcontrol.service.impl;

import cn.edu.xupt.acat.flowcontrol.dao.FlowDao;
import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.domain.entity.TbFlow;
import cn.edu.xupt.acat.flowcontrol.library.FlowControlCodeEnum;
import cn.edu.xupt.acat.flowcontrol.library.FlowControlConstant;
import cn.edu.xupt.acat.flowcontrol.service.FlowEsDumpService;
import cn.edu.xupt.acat.flowcontrol.service.FlowSearchService;
import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.response.R;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

@Service(async = false)
@Component
public class FlowEsDumpServiceImpl implements FlowEsDumpService {

    private static Logger logger = Logger.getLogger(FlowEsDumpServiceImpl.class.toString());

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private FlowSearchService flowSearchService;

    @Resource
    private FlowDao flowDao;

    @Override
    public R dumpToEs(FlowEsEntity input) {
        getParam(input);
        //查询flow es
        FlowEsEntity esEntity = getFlowEsData(input.getNid());

        if (esEntity == null) {
            logger.warning("flow es data doesn't exist. nid is " + input.getNid());
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        input.setId(esEntity.getId());
        UpdateQuery query = buildEsCondition(input);
        IndexCoordinates indexCoordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(FlowEsEntity.class);
        UpdateResponse response = elasticsearchRestTemplate.update(query, indexCoordinates);
        logger.info("update flow es response : " + JSON.toJSONString(response));
        //TODO update DB
        return R.ok();
    }

    private UpdateQuery buildEsCondition(FlowEsEntity input) {
        Document document = buildUpdateEsData(input);
        return UpdateQuery.builder(String.valueOf(input.getId()))
                .withDocument(document)
                .build();
    }

    private void getParam(FlowEsEntity input) {

        if (input == null) {
            logger.warning("input is empty. ");
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getNid())) {
            logger.warning("nid is empty.  input info :" + JSON.toJSONString(input));
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        logger.info("receive flow dump to es entity info is : " + JSON.toJSONString(input));
    }

    private FlowEsEntity getFlowEsData(String nid) {
        FlowEsEntity query = new FlowEsEntity();
        query.setNid(nid);
        List<FlowEsEntity> entities = flowSearchService.query(query);
        if (entities != null && entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }


    /**
     * 构造更新文档
     * @param input input
     * @return  Document
     */
    private Document buildUpdateEsData(FlowEsEntity input) {
        Document document = Document.create();
        //user info
        if (!StringUtils.isEmpty(input.getUserUpdateTime())) {
            document.put("userUpdateTime", input.getUserUpdateTime());
        }
        //nickName
        if (!StringUtils.isEmpty(input.getNickName())) {
            document.put("nickName", input.getNickName());
        }

        //snumber
        if (!StringUtils.isEmpty(input.getSnumber())) {
            document.put("snumber", input.getSnumber());
        }

        //realName
        if (!StringUtils.isEmpty(input.getRealName())) {
            document.put("realName", input.getRealName());
        }

        //className
        if (!StringUtils.isEmpty(input.getClassName())) {
            document.put("className", input.getClassName());
        }

        //sex
        if (input.getSex() != null) {
            document.put("sex", input.getSex());
        }

        //phone
        if (!StringUtils.isEmpty(input.getPhone())) {
            document.put("phone", input.getPhone());
        }

        //work_type
        if (!StringUtils.isEmpty(input.getWorkType())) {
            document.put("workType", input.getWorkType());
        }

        //status
        if (input.getStatus() != null) {
            document.put("status", input.getStatus());
        }

        return document;
    }
}
