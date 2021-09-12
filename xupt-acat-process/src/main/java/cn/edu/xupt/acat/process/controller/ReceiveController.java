package cn.edu.xupt.acat.process.controller;

import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.process.domain.vo.ProcessInput;
import cn.edu.xupt.acat.process.library.ProcessCodeEnum;
import cn.edu.xupt.acat.process.service.ProcessReceive;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/process")
public class ReceiveController {

    private static Logger logger = Logger.getLogger(ReceiveController.class.toString());

    @Autowired
    private ProcessReceive processReceive;

    @PostMapping(value = "/receive")
    public R receive(@RequestBody ProcessInput input){
        getParam(input);
        return processReceive.receive(input);
    }

    /**
     * 参数校验
     * @param input ProcessInput
     */
    private void getParam(@RequestBody ProcessInput input){
        if (input == null || StringUtils.isEmpty(input.getUsername()) || StringUtils.isEmpty(input.getServiceLine()) || input.getVersion() == null) {
            logger.warning(ProcessCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg() + ":" + JSON.toJSONString(input));
            ExceptionCast.exception(ProcessCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), ProcessCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
    }
}
