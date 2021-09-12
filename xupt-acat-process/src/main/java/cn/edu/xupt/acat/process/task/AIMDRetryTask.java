package cn.edu.xupt.acat.process.task;

import cn.edu.xupt.acat.flowcontrol.service.FlowReceiveService;
import cn.edu.xupt.acat.lib.exception.ExceptionCodeEnum;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.response.R;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;

import java.util.logging.Logger;

@AllArgsConstructor
public class AIMDRetryTask implements Runnable{

    private static Logger logger =  Logger.getLogger(SimpleRetryTask.class.toString());
    private static int MAX_RETRY_NUM = 20;
    private static int LIMIT_UPPER = 200;
    private static int BASE = 16;
    private FlowReceiveService flowReceiveService;
    private FlowReceiveInput input;
    private int baseThresh;
    private int increment;

    @Override
    public void run() {
        int retryNum = 1;
        R res = null;
        int sleepTime = 0;
        while (res == null || (int)res.get("code") == ExceptionCodeEnum.FLOW_CONTROL_LIMIT_EXCEPTION.getCode()){
            try {

                if(retryNum > MAX_RETRY_NUM){
                    logger.warning("retry number beyond max limit. give up retry.");
                    return;
                }

                if(retryNum == 1){
                    sleepTime = BASE;
                }else if (sleepTime < baseThresh) {
                    sleepTime = sleepTime * 2;
                } else {
                    sleepTime = sleepTime + increment;
                }
                if( sleepTime > LIMIT_UPPER){
                    sleepTime = LIMIT_UPPER;
                }

                logger.info("retry number is " + retryNum + " sleep time :" + sleepTime + " ms.");

                Thread.sleep(sleepTime);
                try {
                    res = flowReceiveService.receive(input);
                }catch (Exception e){
                    //RPC调用失败，记录日志
                    logger.warning("retry:" + retryNum + " " + e.getMessage() + ":" + " res = " + JSON.toJSONString(res));
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
