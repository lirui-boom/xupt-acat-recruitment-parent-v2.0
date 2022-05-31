package cn.edu.xupt.acat.recruitment.library;

import cn.edu.xupt.acat.lib.constant.Constant;

public class RecruitmentConstant extends Constant {

    public static String RECRUITMENT_SYSTEM_OP_USER = "recruitment_system";

    public static String SERVICE_LINE_INFO_ES_INDEX = "xupt-acat-recruitment-service_line";

    //Redis 加锁过期时间 30 min
    public static long REDIS_LOCK_EXPIRE = 30;
}
