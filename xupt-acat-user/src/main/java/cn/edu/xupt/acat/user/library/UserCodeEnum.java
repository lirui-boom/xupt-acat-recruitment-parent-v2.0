package cn.edu.xupt.acat.user.library;

public enum UserCodeEnum {
    // 20000
    ILLEGAL_ARGS_EXCEPTION(20000, "参数不合法！"),
    ILLEGAL_EMAIL_EXCEPTION(20001, "邮箱地址不合法！"),

    // 21000 redis
    USER_CHECK_CODE_ERROR(21000, "用户验证码不正确或已过期！"),


    //22000 dao
    DATA_EXISTED_EXCEPTION(22000, "数据已存在！"),


    ;
    private long code;
    private String msg;

    UserCodeEnum(long code, String msg) {
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
