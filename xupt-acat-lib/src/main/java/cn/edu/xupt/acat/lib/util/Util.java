package cn.edu.xupt.acat.lib.util;

import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.model.FlowSendInput;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Util {

    /**
     * 获取系统时间
     * @return
     */
    public static Date getTime() {
        return new Date();
    }

    public static String getFormatTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getTime());
    }

    /**
     * 验证码生成
     * @return
     */
    public static String getCode(){
        return UUID.randomUUID().toString().substring(0, 4).toLowerCase();
    }

    /**
     * 获取nid
     * @param username
     * @param serviceLine
     * @param version
     * @return
     */
    public static String getNid(String username, String serviceLine, Integer version) {
        return username + "@" + serviceLine + "@" + version;
    }

    public static int getTurns(String workType) {
        return workType.split("_").length == 1 ? 1 : Integer.parseInt(workType.split("_")[1]);
    }

    public static String getUsername(String nid) {
        return nid.split("@")[0];
    }

    public static String getServiceLine(String nid) {
        return nid.split("@")[1];
    }

    public static String getVersion(String nid) {
        return nid.split("@")[2];
    }

    public static FlowReceiveInput getFlowReceiveInput(FlowSendInput input) {
        FlowReceiveInput receive = new FlowReceiveInput();
        receive.setApplyInfo(input.getApplyInfo());
        receive.setNid(input.getNid());
        return receive;
    }
}
