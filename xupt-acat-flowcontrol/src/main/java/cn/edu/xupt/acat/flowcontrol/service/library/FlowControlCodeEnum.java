package cn.edu.xupt.acat.flowcontrol.service.library;

public enum FlowControlCodeEnum {
    // 13000
    ILLEGAL_ARGS_EXCEPTION(13000, "参数不合法！"),
    ILLEGAL_REQUEST_EXCEPTION(13001, "非法请求！"),

    //业务 14000
    ILLEGAL_WORK_FLOW_STATUS_EXCEPTION(72000, "流程配置错误，没有找到下一个流程！"),
    ILLEGAL_SERVICE_LINE_STATUS_EXCEPTION(72001, "报名系统已经关闭！"),

    //DB
    DATA_EXISTED_EXCEPTION(73000, "不可重复报名！"),


    ;
    private long code;
    private String msg;

    FlowControlCodeEnum(long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
