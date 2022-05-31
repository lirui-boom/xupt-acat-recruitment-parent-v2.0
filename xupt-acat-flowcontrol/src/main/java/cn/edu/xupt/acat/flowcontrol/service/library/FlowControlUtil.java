package cn.edu.xupt.acat.flowcontrol.service.library;

import cn.edu.xupt.acat.lib.model.FlowModel;
import cn.edu.xupt.acat.lib.util.Util;
import org.springframework.util.StringUtils;

import java.util.List;

public class FlowControlUtil extends Util {

    /**
     * 获取下一个流程
     * @param currentFlow
     * @param workDetails
     * @return
     */
    public static String getNextFlow(String currentFlow, List<FlowModel> workDetails) {
        //first
        if (StringUtils.isEmpty(currentFlow) || currentFlow.equals(FlowControlConstant.WORK_TYPE_NO_APPLY)) {
            return (workDetails.get(0)).getFlowName();
        }

        // no pass
        if (currentFlow.split("_").length == 2 && currentFlow.split("_")[0].equals(FlowControlConstant.WORK_TYPE_NO_PASS)) {
            return FlowControlConstant.NO_PASS_FLOW_TYPE;
        }

        for (Object o : workDetails) {
            FlowModel flow = (FlowModel) o;
            if (flow.getFlowName().equals(currentFlow)) {
                return flow.getNext();
            }
        }
        //没有匹配到下一个流程
        return FlowControlConstant.NOT_FOUND_NEXT_FLOW;
    }
}
