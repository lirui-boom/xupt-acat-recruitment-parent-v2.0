package cn.edu.xupt.acat.report.task.handler;

import cn.edu.xupt.acat.report.dao.ReportDao;
import cn.edu.xupt.acat.report.domain.entity.TbReport;
import cn.edu.xupt.acat.report.library.ReportConstant;
import cn.edu.xupt.acat.report.task.ReportThread;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

@Component
public class ReportTaskFailHandler implements RejectedExecutionHandler {

    private static Logger logger = Logger.getLogger(ReportTaskFailHandler.class.toString());

    @Resource
    private ReportDao reportDao;

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.warning("task execute fail, because too many task.");
        //更改任务状态为fail
        ReportThread reportThread = (ReportThread) r;
        TbReport task = reportThread.getReport();
        task.setStatus(ReportConstant.REPORT_STATUS_FAIL);
        task.setDetail("当前任务数过多，请稍后重试...");
        reportDao.updateById(task);
    }
}
