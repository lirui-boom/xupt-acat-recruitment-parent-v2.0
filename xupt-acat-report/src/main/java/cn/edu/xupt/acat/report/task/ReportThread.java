package cn.edu.xupt.acat.report.task;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.service.FlowSearchService;
import cn.edu.xupt.acat.report.dao.ReportDao;
import cn.edu.xupt.acat.report.domain.entity.TbReport;
import cn.edu.xupt.acat.report.library.ExcelExportUtil;
import cn.edu.xupt.acat.report.library.ReportConstant;
import cn.edu.xupt.acat.report.library.ReportUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Data
public class ReportThread implements Runnable {

    private Logger logger = Logger.getLogger(ReportThread.class.toString());

    private TbReport report;
    private ReportDao reportDao;
    private FlowSearchService flowSearchService;


    public ReportThread(TbReport report, ReportDao reportDao, FlowSearchService flowSearchService) {
        this.report = report;
        this.reportDao = reportDao;
        this.flowSearchService = flowSearchService;
    }

    @Override
    public void run() {
        logger.info(Thread.currentThread().getName() + " running, report task is " + JSON.toJSONString(report));
        //1.修改任务状态为 running
        report.setStatus(ReportConstant.REPORT_STATUS_RUNNING);
        doUpdateReportStatus(report);
        //2.查询任务数据
        List<FlowEsEntity> esEntities = doSearchTaskData();
        //3.生成Excel
        String url = null;
        try {
            url = doCreateExcel(esEntities);
        } catch (Exception e) {
            report.setDetail(e.getMessage());
        }
        int status = url != null ? ReportConstant.REPORT_STATUS_SUCCESS : ReportConstant.REPORT_STATUS_FAIL;
        report.setStatus(status);
        report.setDownload(url);
        //4.修改任务状态为 end
        doUpdateReportStatus(report);
        logger.info(Thread.currentThread().getName() + " end, work status is " + status);
    }

    /**
     * 修改任务运行状态
     * @param report report
     */
    private void doUpdateReportStatus(TbReport report) {
        report.setStatus(report.getStatus());
        report.setUpdateTime(ReportUtil.getTime());
        reportDao.updateById(report);
        logger.info("update task run status : " + JSON.toJSONString(report));
    }

    private List<FlowEsEntity> doSearchTaskData() {
        //1.获取查询条件
        String conds = report.getConds();
        //2.查询条件转查询对象
        FlowEsEntity esEntity = JSON.parseObject(conds, FlowEsEntity.class);
        //3.ES查询
        List<FlowEsEntity> reportData =  flowSearchService.query(esEntity);
        logger.info(Thread.currentThread().getName() + " report data : " + JSON.toJSONString(reportData));
        return reportData;
    }

    /**
     * 生成Excel
     * @param esEntities esEntities
     */
    private String doCreateExcel(List<FlowEsEntity> esEntities) {

        String sheetTitle = ReportConstant.SHEET_TITLE;
        String[] title = ReportConstant.REPORT_COLUMN;               //设置表格表头字段
        String[] properties = ReportConstant.REPORT_COLUMN_FILED;    // 查询对应的字段
        ExcelExportUtil util = new ExcelExportUtil();
        util.setData(buildExcelCondition(esEntities));
        util.setHeardKey(properties);
        util.setFontSize(14);
        util.setSheetName(sheetTitle);
        util.setTitle(sheetTitle);
        util.setHeardList(title);
        String url  = null;
        try {
            url = util.exportExport();
            logger.info("report task execute success.");
        } catch (IOException e) {
            logger.warning("report task execute fail, exception info : " + e.getMessage());
        }
        return url;
    }

    /**
     * 构造导出数据
     * @param esEntities
     * @return
     */
    private List<Map> buildExcelCondition(List<FlowEsEntity> esEntities){
        List<Map> res = new ArrayList<>(esEntities.size());
        for (FlowEsEntity esEntity : esEntities) {
            Map map = new HashMap<>();
            //{"nid", "realName", "snumber", "className", "email", "phone", "serviceLine", "version", "workType", "status"};
            map.put("nid", esEntity.getNid());
            map.put("realName", esEntity.getRealName());
            map.put("snumber",esEntity.getSnumber());
            map.put("className", esEntity.getClassName());
            map.put("email", esEntity.getEmail());
            map.put("phone", esEntity.getPhone());
            map.put("serviceLine", esEntity.getServiceLine());
            map.put("version", esEntity.getVersion());
            map.put("workType", esEntity.getWorkType());
            if (esEntity.getStatus() == null) {
                map.put("status", "未知");
            } else if (esEntity.getStatus() == 0) {
                map.put("status", "流程未结束");
            } else if (esEntity.getStatus() == 1) {
                map.put("status", "面试通过");
            } else {
                map.put("status", "面试未通过");
            }
            res.add(map);
        }
        return res;
    }
}
