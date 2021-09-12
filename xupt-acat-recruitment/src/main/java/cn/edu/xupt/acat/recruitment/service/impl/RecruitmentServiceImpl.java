package cn.edu.xupt.acat.recruitment.service.impl;

import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.dao.RecruitmentDao;
import cn.edu.xupt.acat.recruitment.domain.entity.TbRecruitment;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.recruitment.library.RecruitmentCodeEnum;
import cn.edu.xupt.acat.recruitment.library.RecruitmentUtil;
import cn.edu.xupt.acat.recruitment.service.RecruitmentService;
import cn.edu.xupt.acat.recruitment.service.ServiceLineSearchService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

@Service
public class RecruitmentServiceImpl implements RecruitmentService {


    private static Logger logger = Logger.getLogger(RecruitmentServiceImpl.class.toString());
    @Resource
    private ServiceLineSearchService serviceLineSearchService;

    @Resource
    private RecruitmentDao recruitmentDao;

    @Override
    public String getNid(String username) {

        if (username == null) {
            ExceptionCast.exception("参数不合法");
        }

        QueryWrapper<TbRecruitment> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        List<TbRecruitment> records = recruitmentDao.selectList(wrapper);

        for (TbRecruitment item : records) {
            String nid = item.getNid();
            String serviceLine = RecruitmentUtil.getServiceLine(nid);
            String version = RecruitmentUtil.getVersion(nid);
            TbServiceLine query = new TbServiceLine();
            query.setServiceLine(serviceLine);
            query.setVersion(Integer.parseInt(version));
            query.setStatus(1);
            List<TbServiceLine> lines = serviceLineSearchService.query(query);
            System.out.println(lines);
            if (lines != null && lines.size() > 0) {
                TbServiceLine current = lines.get(0);
                return RecruitmentUtil.getNid(username, current.getServiceLine(), current.getVersion());
            }
        }
        logger.warning("沒有查詢到nid");
        return null;
    }

    @Override
    public TbRecruitment getRecruitment(String nid, String workType) {
        QueryWrapper<TbRecruitment> wrapper = new QueryWrapper<>();
        wrapper.eq("nid", nid);
        wrapper.eq("work_type", workType);
        List<TbRecruitment> recruitments = recruitmentDao.selectList(wrapper);
        if (recruitments == null || recruitments.size() == 0) {
            return null;
        }
        return recruitments.get(0);
    }
}
