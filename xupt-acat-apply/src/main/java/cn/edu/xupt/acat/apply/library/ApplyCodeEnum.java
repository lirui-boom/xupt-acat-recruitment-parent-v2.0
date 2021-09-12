package cn.edu.xupt.acat.apply.library;

public enum ApplyCodeEnum {
    // 10000
    ILLEGAL_ARGS_EXCEPTION(10000, "参数不合法！"),

    ;
    private long code;
    private String msg;

    ApplyCodeEnum(long code, String msg) {
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
