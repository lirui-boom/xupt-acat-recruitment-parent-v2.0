package cn.edu.xupt.acat.process.task;

import cn.edu.xupt.acat.flowcontrol.service.FlowReceiveService;
import cn.edu.xupt.acat.lib.exception.ExceptionCodeEnum;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.response.R;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;

import java.util.logging.Logger;

@AllArgsConstructor
public class SimpleRetryTask implements Runnable {

    private static Logger logger =  Logger.getLogger(SimpleRetryTask.class.toString());
    private static int LIMIT_UPPER = 200;
    private static int BASE = 10;
    private FlowReceiveService flowReceiveService;
    private FlowReceiveInput input;


    @Override
    public void run() {
        int retryNum = 1;
        R res = null;
        while (res == null || (int)res.get("code") == ExceptionCodeEnum.FLOW_CONTROL_LIMIT_EXCEPTION.getCode()){
            try {
                logger.info("retry number is " + retryNum + " sleep time :" + BASE * retryNum + " ms.");
                Thread.sleep(BASE * retryNum);
                try {
                    res = flowReceiveService.receive(input);
                }catch (Exception e){
                    //RPC调用失败，记录日志
                    logger.warning("retry:" + retryNum + " " + e.getMessage() + ":" + " res = " + JSON.toJSONString(res));
                    if( BASE * retryNum > LIMIT_UPPER){
                        logger.warning("sleep time beyond limit time, rpc fail.");
                        return;
                    }
                }
                retryNum++;
            } catch (InterruptedException e) {
                logger.warning(e.getMessage());
                return;
            }
        }

        logger.info("rpc flow control call success :" + JSON.toJSONString(res));
    }
}
