package cn.edu.xupt.acat.report.library;

public enum ReportCodeEnum {
    // 80000
    ILLEGAL_ARGS_EXCEPTION(80000, "参数不合法！"),

    ;
    private long code;
    private String msg;

    ReportCodeEnum(long code, String msg) {
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
