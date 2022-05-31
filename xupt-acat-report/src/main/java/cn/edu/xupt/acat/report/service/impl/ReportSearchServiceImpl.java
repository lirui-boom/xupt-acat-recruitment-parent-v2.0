package cn.edu.xupt.acat.report.service.impl;

import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.response.SearchPage;
import cn.edu.xupt.acat.report.dao.ReportDao;
import cn.edu.xupt.acat.report.domain.entity.TbReport;
import cn.edu.xupt.acat.report.domain.vo.ReportSearchVo;
import cn.edu.xupt.acat.report.library.ReportCodeEnum;
import cn.edu.xupt.acat.report.service.ReportSearchService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ReportSearchServiceImpl implements ReportSearchService {

    private static Logger logger = Logger.getLogger(ReportSearchServiceImpl.class.toString());


    @Resource
    private ReportDao reportDao;

    @Override
    public SearchPage<List<TbReport>> search(ReportSearchVo input) {

        getParam(input);
        QueryWrapper<TbReport> wrapper = new QueryWrapper<>();
        wrapper.eq("op_user", input.getReport().getOpUser());
        List<TbReport> reports = reportDao.selectList(wrapper);
        int pageNum = input.getPageNum();
        int pageSize = input.getPageSize();
        int start = (pageNum - 1) * pageSize;
        int end = pageNum * pageSize > reports.size() ? reports.size() : pageNum * pageSize;

        List<TbReport> resList = new ArrayList<>(pageSize);
        for (int i = start; i < end; i++) {
            resList.add(reports.get(i));
        }

        return new SearchPage<List<TbReport>>(pageNum,pageSize,(long)reports.size(),resList);
    }

    private void getParam(ReportSearchVo input) {

        if (input.getReport() == null) {
            logger.warning("report info is empty.");
            ExceptionCast.exception(ReportCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), ReportCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getReport().getOpUser())) {
            logger.warning("report operation user is empty.");
            ExceptionCast.exception(ReportCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), ReportCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (input.getPageNum() <= 0) {
            input.setPageNum(1);
        }

        if (input.getPageSize() <= 0) {
            input.setPageSize(10);
        }

        logger.info("receive report search vo: " + JSON.toJSONString(input));
    }
}
