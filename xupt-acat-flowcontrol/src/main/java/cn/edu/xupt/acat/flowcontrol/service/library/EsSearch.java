package cn.edu.xupt.acat.flowcontrol.service.library;

import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.recruitment.service.ServiceLineSearchService;
import cn.edu.xupt.acat.user.domain.entity.TbUser;
import cn.edu.xupt.acat.user.service.UserSearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Component
public class EsSearch {

    private static Logger logger = Logger.getLogger(EsSearch.class.toString());

    @Reference
    private UserSearchService userSearchService;

    @Reference
    private ServiceLineSearchService serviceLineSearchService;

    /**
     * 获取Service_Line信息
     *
     * @param input
     * @return
     */
    public TbServiceLine remoteServiceLine(FlowReceiveInput input) {
        TbServiceLine query = new TbServiceLine();
        query.setServiceLine(input.getServiceLine());
        query.setVersion(input.getVersion());
        List<TbServiceLine> serviceLines = serviceLineSearchService.query(query);
        logger.info("remoteServiceLine info : " + JSON.toJSONString(serviceLines));
        if (serviceLines != null && serviceLines.size() > 0) {
            return serviceLines.get(0);
        }
        return null;
    }

    /**
     * 获取User信息
     *
     * @param input
     * @return
     */
    public TbUser remoteUser(FlowReceiveInput input) {
        TbUser query = new TbUser();
        query.setUsername(input.getUsername());
        List<TbUser> users = userSearchService.query(query);
        logger.info("remoteUser info : " + JSON.toJSONString(users));
        if (users != null && users.size() > 0) {
            return users.get(0);
        }
        return null;
    }
}
