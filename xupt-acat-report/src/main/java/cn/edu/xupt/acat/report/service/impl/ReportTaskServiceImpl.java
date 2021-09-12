package cn.edu.xupt.acat.report.service.impl;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.service.FlowSearchService;
import cn.edu.xupt.acat.report.dao.ReportDao;
import cn.edu.xupt.acat.report.domain.entity.TbReport;
import cn.edu.xupt.acat.report.library.ReportConstant;
import cn.edu.xupt.acat.report.service.ReportTaskService;
import cn.edu.xupt.acat.report.task.ReportThread;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

@Service
public class ReportTaskServiceImpl implements ReportTaskService {

    private static Logger logger = Logger.getLogger(ReportTaskServiceImpl.class.toString());

    @Resource
    private ReportDao reportDao;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Reference
    private FlowSearchService flowSearchService;

    @Override
    public void execute() {
        //1.扫描数据库，获取待执行任务
        List<TbReport> reports = reportDao.selectList(buildDBCondition());

        if (reports == null || reports.size() == 0) {
            logger.info("there is no task to execute.");
            return;
        }

        //2.生成任务对象
        List<ReportThread> reportThreads = getReportRunnable(reports);
        //3.更改任务状态
        for (TbReport report : reports) {
            report.setStatus(ReportConstant.REPORT_STATUS_WAIT);
            reportDao.updateById(report);
        }
        //4.提交任务
        submitTask(reportThreads);
        logger.info("submit report task with data: " + JSON.toJSONString(reports));
    }

    /**
     * 构造数据库查询条件
     * @return
     */
    private QueryWrapper<TbReport> buildDBCondition() {
        QueryWrapper<TbReport> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        return wrapper;
    }

    /**
     * 生成任务对象
     * @param reports
     * @return
     */
    private List<ReportThread> getReportRunnable(List<TbReport> reports) {
        List<ReportThread> reportThreads = new ArrayList<>(reports.size());
        for (TbReport report : reports) {
            reportThreads.add(new ReportThread(report, reportDao, flowSearchService));
        }
        return reportThreads;
    }

    /**
     * 提交任务
     * @param reportThreads
     * @return
     */
    private void submitTask(List<ReportThread>  reportThreads) {
        for (ReportThread thread : reportThreads) {
            threadPoolExecutor.submit(thread);
        }
    }
}
