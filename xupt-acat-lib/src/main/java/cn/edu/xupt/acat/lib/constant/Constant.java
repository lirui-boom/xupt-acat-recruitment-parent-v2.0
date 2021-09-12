package cn.edu.xupt.acat.lib.constant;

public class Constant {

    //流程状态
    public static String WORK_TYPE_NO_APPLY = "process";     //未报名
    public static String WORK_TYPE_APPLY    = "apply";       //已报名
    public static String WORK_TYPE_NOT_SIGN = "nosign";      //等待未签
    public static String WORK_TYPE_SIGN_IN  = "sign";        //等待已签
    public static String WORK_TYPE_DOING    = "doing";       //面试中
    public static String WORK_TYPE_COMPLETE = "complete";    //完成，等待结果
    public static String WORK_TYPE_PASS     = "pass";        //该轮结束pass
    public static String WORK_TYPE_NO_PASS  = "nopass";      //该轮结束no pass


    // 模块返回状态
    public static int MODULE_STATUS_REJECT  = 0; //拒绝
    public static int MODULE_STATUS_PASS    = 1; //通过

    public static String WORK_NAME_APPLY = "apply";
    public static String WORK_NAME_RECRUIT = "recruitment";

    public static int RESPONSE_SUCCESS = 200;

    //LOGS topic
    public static String TOPIC_LOGS = "LOGS_ADD_RECEIVE";
    //NOTICE topic
    public static String NOTICE_RECEIVE_TOPIC = "NOTICE_RECEIVE_TOPIC";
    //FLOW topic
    public static String FLOW_RECEIVE_TOPIC = "FLOW_CONTROL_RECEIVE";

}
