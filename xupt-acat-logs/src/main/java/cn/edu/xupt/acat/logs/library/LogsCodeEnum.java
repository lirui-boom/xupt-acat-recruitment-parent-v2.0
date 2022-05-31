package cn.edu.xupt.acat.logs.library;

public enum LogsCodeEnum {

    // 40000
    ILLEGAL_ARGS_EXCEPTION(14000, "参数不合法！"),


    ;

    private int code;
    private String msg;

    LogsCodeEnum(int code, String msg) {
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
