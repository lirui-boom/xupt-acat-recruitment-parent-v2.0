package cn.edu.xupt.acat.recruitment.service.impl;

import cn.edu.xupt.acat.lib.response.SearchPage;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.recruitment.domain.vo.ServiceLineSearchVo;
import cn.edu.xupt.acat.recruitment.library.RecruitmentConstant;
import cn.edu.xupt.acat.recruitment.service.ServiceLineSearchService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
@Component
public class ServiceLineSearchServiceImpl implements ServiceLineSearchService {

    private static Logger logger = Logger.getLogger(ServiceLineSearchServiceImpl.class.toString());

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public List<TbServiceLine> query(TbServiceLine serviceLine) {
        logger.info("query es index is " + RecruitmentConstant.SERVICE_LINE_INFO_ES_INDEX + ", query : " + JSON.toJSONString(serviceLine));
        Query query = buildQueryCondition(serviceLine);
        SearchHits<TbServiceLine> hits = elasticsearchRestTemplate.search(query, TbServiceLine.class);
        logger.info("query es result :  " + JSON.toJSONString(hits));
        return getServiceLineList(hits);
    }

    @Override
    public SearchPage<List<TbServiceLine>> search(ServiceLineSearchVo vo) {
        logger.info("search es index is " + RecruitmentConstant.SERVICE_LINE_INFO_ES_INDEX + ", search : " + JSON.toJSONString(vo));

        if (vo.getPageNum() <= 0) {
            vo.setPageNum(1);
        }

        if (vo.getPageSize() <= 0) {
            vo.setPageSize(10);
        }

        Query query = buildSearchCondition(vo);
        SearchHits<TbServiceLine> hits = elasticsearchRestTemplate.search(query, TbServiceLine.class);
        List<TbServiceLine> serviceLines = getServiceLineList(hits);
        SearchPage<List<TbServiceLine>> res = new SearchPage<>(
                vo.getPageNum(),
                vo.getPageSize(),
                hits.getTotalHits(),
                serviceLines
        );
        logger.info("search es result :  " + JSON.toJSONString(hits) +  ", total count : " + hits.getTotalHits()  + ", data : " + JSON.toJSONString(serviceLines));
        return res;
    }

    /**
     * 构造Query查询条件
     * @param serviceLine
     * @return
     */
    private Query buildQueryCondition(TbServiceLine serviceLine){
        BoolQueryBuilder queryBuilder = buildServiceLineQuery(serviceLine);
        Query query =  new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .build();
        logger.info("query es condition :  " + JSON.toJSONString(query));
        return query;
    }

    /**
     * 构造分页搜索查询条件
     * @param vo
     * @return
     */
    private Query buildSearchCondition(ServiceLineSearchVo vo) {
        BoolQueryBuilder queryBuilder = buildServiceLineQuery(vo.getServiceLine());
        Query query =  new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .withPageable(PageRequest.of(vo.getPageNum() - 1, vo.getPageSize()))
                .build();
        logger.info("search es condition :  " + JSON.toJSONString(query));
        return query;
    }

    /**
     * 构造ServiceLine查询条件
     * @param serviceLine
     * @return
     */
    private BoolQueryBuilder buildServiceLineQuery(TbServiceLine serviceLine){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if (serviceLine == null) {
            return null;
        }

        //id
        if (serviceLine.getId() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("id", serviceLine.getId())
            );
        }
        //serviceLine
        if (!StringUtils.isEmpty(serviceLine.getServiceLine())) {
            queryBuilder.must(
                    QueryBuilders.termQuery("serviceLine", serviceLine.getServiceLine())
            );
        }
        //turns
        if (serviceLine.getTurns() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("turns", serviceLine.getTurns())
            );
        }
        //authKey
        if (!StringUtils.isEmpty(serviceLine.getAuthKey())){
            queryBuilder.must(
                    QueryBuilders.termQuery("authKey", serviceLine.getAuthKey())
            );
        }
        //status
        if (serviceLine.getStatus() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("status", serviceLine.getStatus())
            );
        }
        //version
        if (serviceLine.getVersion() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("version", serviceLine.getVersion())
            );
        }

        return queryBuilder;
    }

    /**
     * 从ES查询数据中解析ServiceLineList
     * @param hits
     * @return
     */
    private List<TbServiceLine> getServiceLineList(SearchHits<TbServiceLine> hits){
        System.out.println(hits.getSearchHits());
        List<TbServiceLine> res = new ArrayList<>((int)hits.getTotalHits());
        for (SearchHit<TbServiceLine> hit : hits.getSearchHits()) {
            res.add(hit.getContent());
        }
        return res;
    }
}
