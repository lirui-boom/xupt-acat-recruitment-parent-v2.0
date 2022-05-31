package cn.edu.xupt.acat.notices.library;

public enum NoticeCodeEnum{
    // 50000
    ILLEGAL_ARGS_EXCEPTION(50000, "参数不合法！"),
    ;
    private long code;
    private String msg;

    NoticeCodeEnum(long code, String msg) {
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

