package cn.edu.xupt.acat.process.library;

import cn.edu.xupt.acat.lib.constant.Constant;
import cn.edu.xupt.acat.lib.model.ApplyInfo;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.process.domain.vo.ProcessInput;

public class ProcessUtil {

    /**
     * 构造MQ调用方式参数
     * @param input ProcessInput
     * @return FlowReceiveInput
     */
    public static FlowReceiveInput buildMqCondition(ProcessInput input){
        FlowReceiveInput flowReceiveInput = new FlowReceiveInput();
        flowReceiveInput.setRpc(false);
        flowReceiveInput.setUsername(input.getUsername());
        flowReceiveInput.setServiceLine(input.getServiceLine());
        flowReceiveInput.setVersion(input.getVersion());
        flowReceiveInput.setWorkType(Constant.WORK_TYPE_NO_APPLY);
        flowReceiveInput.setModuleStatus(Constant.MODULE_STATUS_PASS);
        //apply info
        ApplyInfo applyInfo = new ApplyInfo();
        applyInfo.setRealName(input.getRealName());
        applyInfo.setClassName(input.getClassName());
        applyInfo.setSnumber(input.getSnumber());
        applyInfo.setSex(input.getSex());
        applyInfo.setPhone(input.getPhone());
        applyInfo.setContent(input.getContent());
        flowReceiveInput.setApplyInfo(applyInfo);
        return flowReceiveInput;
    }

    /**
     * 构造rpc调用需要的参数
     * @param input FlowReceiveInput
     * @return FlowReceiveInput
     */
    public static FlowReceiveInput buildRpcCondition(FlowReceiveInput input){
        input.setRpc(true);
        return input;
    }
}
