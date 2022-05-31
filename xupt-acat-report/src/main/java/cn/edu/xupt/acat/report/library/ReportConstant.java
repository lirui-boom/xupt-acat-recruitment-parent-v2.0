package cn.edu.xupt.acat.report.library;

import cn.edu.xupt.acat.lib.constant.Constant;

import java.io.File;

public class ReportConstant extends Constant {
    public static int REPORT_STATUS_FAIL = -1;
    public static int REPORT_STATUS_NEW = 0;
    public static int REPORT_STATUS_WAIT = 1;
    public static int REPORT_STATUS_RUNNING = 2;
    public static int REPORT_STATUS_SUCCESS = 3;

    public static String SHEET_TITLE = "ACAT面试信息导出";
    public static String[] REPORT_COLUMN = {"nid", "姓名", "学号", "班级", "邮箱", "电话", "组别", "版本", "流程状态", "面试结果"};
    public static String[] REPORT_COLUMN_FILED = {"nid", "realName", "snumber", "className", "email", "phone", "serviceLine", "version", "workType", "status"};


    public static String EXCEL_REPORT_BASE_PATH = "F:" + File.separator + "nginx-1.17.0" + File.separator + "html";
    public static String EXCEL_REPORT_HOST_PORT = "http://www.recruitment.cn:8000";

}
