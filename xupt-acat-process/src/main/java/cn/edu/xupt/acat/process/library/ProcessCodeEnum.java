package cn.edu.xupt.acat.process.library;


public enum ProcessCodeEnum {

    // 14000
    ILLEGAL_ARGS_EXCEPTION(14000, "参数不合法！"),
    // 14100
    SEND_QUEUE_FAIL(14100, "消息发送失败！"),
    SEND_QUEUE_SUCCESS(14101, "消息发送成功！"),

    // 14900
    MESSAGE_GIVE_UP_ERROR(14900,"消息丢弃！"),

    ;

    private int code;
    private String msg;

    ProcessCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
