package cn.edu.xupt.acat.report.service.impl;

import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.report.dao.ReportDao;
import cn.edu.xupt.acat.report.domain.entity.TbReport;
import cn.edu.xupt.acat.report.library.ReportCodeEnum;
import cn.edu.xupt.acat.report.library.ReportConstant;
import cn.edu.xupt.acat.report.library.ReportUtil;
import cn.edu.xupt.acat.report.service.ReportSubmitService;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.logging.Logger;

@Service
public class ReportSubmitServiceImpl implements ReportSubmitService {

    private static Logger logger = Logger.getLogger(ReportSubmitServiceImpl.class.toString());

    @Resource
    private ReportDao reportDao;

    @Override

    public R submit(TbReport report) {
        getParam(report);
        Date now = ReportUtil.getTime();
        report.setUpdateTime(now);
        report.setCreateTime(now);
        report.setStatus(ReportConstant.REPORT_STATUS_NEW);
        reportDao.insert(report);
        logger.info("insert db with report task info : " + JSON.toJSONString(report));
        return R.ok();
    }

    /**
     * 参数校验
     * @param report report
     */
    private void getParam(TbReport report){

        if (report == null) {
            logger.warning("report task info is empty.");
            ExceptionCast.exception(ReportCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), ReportCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(report.getConds())) {
            logger.warning("report task condition is empty.");
            ExceptionCast.exception(ReportCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), ReportCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(report.getOpUser())) {
            logger.warning("report task operation user is empty.");
            ExceptionCast.exception(ReportCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), ReportCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        logger.info("receive report task with : " + JSON.toJSONString(report));
    }
}
