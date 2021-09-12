package cn.edu.xupt.acat.recruitment.library;

public enum  RecruitmentCodeEnum {

    // 70000
    ILLEGAL_ARGS_EXCEPTION(70000, "参数不合法！"),

    //DAO
    DATA_EXISTED_EXCEPTION(71000, "数据已存在！"),
    DATA_NOT_EXIST_EXCEPTION(71001, "数据不存在！"),

    //Redis
    REDIS_DATA_EXISTED_EXCEPTION(72000, "该用户已经签过到了！"),
    REDIS_DATA_NOT_EXIST_EXCEPTION(72001, "未签到，无法签退！"),
    QUEUE_DATA_EMPTY_EXCEPTION(72002, "队列中没有可以面试的用户，无法获取任务！"),
    REDIS_LOCK_FAIL_EXCEPTION(72003, "获取任务失败，该用户已在面试中！"),
    REDIS_DATA_ITWING_EXCEPTION(72004, "正在面试，无法签退！"),
    REDIS_OPUSER_DIFF_EXCEPTION(72005, "释放任务用户必须是任务获取者！"),



    ;

    private long code;
    private String msg;

    RecruitmentCodeEnum(long code, String msg) {
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
