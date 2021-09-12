package cn.edu.xupt.acat.flowcontrol.service.impl;

import cn.edu.xupt.acat.flowcontrol.library.EsSearch;
import cn.edu.xupt.acat.flowcontrol.library.FlowControlCodeEnum;
import cn.edu.xupt.acat.flowcontrol.service.FlowCallBackService;
import cn.edu.xupt.acat.flowcontrol.service.RunNextFlowService;
import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.logging.Logger;

@Service(async = true)
@Component
public class FlowCallBackServiceImpl implements FlowCallBackService {

    private static Logger logger = Logger.getLogger(FlowCallBackServiceImpl.class.toString());


    @Autowired
    private RunNextFlowService runNextFlowService;

    @Autowired
    private EsSearch esSearch;

    @Override
    public R callback(FlowReceiveInput input) {
        getParam(input);
        TbServiceLine serviceLine = esSearch.remoteServiceLine(input);
        if (serviceLine == null) {
            logger.warning("serviceLine info is empty.");
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_REQUEST_EXCEPTION.getMsg(),FlowControlCodeEnum.ILLEGAL_REQUEST_EXCEPTION.getCode());
        }
        runNextFlowService.runNextFlow(input, serviceLine);
        return R.ok();
    }

    /**
     * 参数校验
     * @param input input
     */
    private void getParam(FlowReceiveInput input) {

        if (input == null) {
            logger.warning("flow call back receive input is empty.");
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getNid())) {
            logger.warning("flow call back receive nid is empty. " + JSON.toJSONString(input));
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getWorkType())) {
            logger.warning("flow call back receive work type is empty. " + JSON.toJSONString(input));
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (input.getModuleStatus() == null) {
            logger.warning("flow call back receive module status is empty. " + JSON.toJSONString(input));
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        logger.info("flow call back receive info is : " + JSON.toJSONString(input));
    }
}
