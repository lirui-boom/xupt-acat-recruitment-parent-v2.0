package cn.edu.xupt.acat.lib.exception;

/**
 * 10000 服务端错误
 * 10100 客户端错误
 * 10200 RPC错误
 */
public enum ExceptionCodeEnum {

    //10000
    UNKNOW_EXCEPTION(10000,"未知异常！"),

    //10100
    ILLEGAL_HTTP_TYPE_EXCEPTION(10100, "http请求类型错误！"),
    FLOW_CONTROL_LIMIT_EXCEPTION(10101, "流量限流！"),
    //10200
    SERVICE_CALL_EXCEPTION(10200,"微服务调用失败！");

    private long code;
    private String msg;

    ExceptionCodeEnum(long code, String msg) {
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
