package cn.edu.xupt.acat.logs.service.impl;

import cn.edu.xupt.acat.lib.response.SearchPage;
import cn.edu.xupt.acat.logs.domain.entity.TbLogs;
import cn.edu.xupt.acat.logs.domain.vo.LogsSearchVo;
import cn.edu.xupt.acat.logs.library.LogsConstant;
import cn.edu.xupt.acat.logs.service.LogsSearchService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
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
public class LogsSearchServiceImpl implements LogsSearchService {

    private static Logger logger = Logger.getLogger(LogsSearchServiceImpl.class.toString());

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public List<TbLogs> query(TbLogs logs) {
        logger.info("query es index is " + LogsConstant.LOGS_INFO_ES_INDEX + ", query : " + JSON.toJSONString(logs));
        Query query = buildQueryCondition(logs);
        SearchHits<TbLogs> hits = elasticsearchRestTemplate.search(query, TbLogs.class);
        logger.info("query es result :  " + JSON.toJSONString(hits));
        return getLogsList(hits);
    }

    @Override
    public SearchPage<List<TbLogs>> search(LogsSearchVo vo) {
        logger.info("search es index is " + LogsConstant.LOGS_INFO_ES_INDEX + ", search : " + JSON.toJSONString(vo));

        if (vo.getPageNum() <= 0) {
            vo.setPageNum(1);
        }

        if (vo.getPageSize() <= 0) {
            vo.setPageSize(10);
        }

        Query query = buildSearchCondition(vo);
        SearchHits<TbLogs> hits = elasticsearchRestTemplate.search(query, TbLogs.class);
        List<TbLogs> userList = getLogsList(hits);
        SearchPage<List<TbLogs>> res = new SearchPage<>(
                vo.getPageNum(),
                vo.getPageSize(),
                hits.getTotalHits(),
                userList
        );
        logger.info("search es result :  " + JSON.toJSONString(hits) +  ", total count : " + hits.getTotalHits()  + ", data : " + JSON.toJSONString(userList));
        return res;
    }


    /**
     * 构造Query查询条件
     * @param logs
     * @return
     */
    private Query buildQueryCondition(TbLogs logs){
        BoolQueryBuilder queryBuilder = buildULogsQuery(logs);
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
    private Query buildSearchCondition(LogsSearchVo vo) {
        QueryBuilder queryBuilder = buildULogsQuery(vo.getLogs());
        Query query =  new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(vo.getPageNum() - 1, vo.getPageSize()))
                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .build();
        logger.info("search es condition :  " + JSON.toJSONString(query));
        return query;
    }

    /**
     * 构造Logs查询条件
     * @param logs
     * @return
     */
    private BoolQueryBuilder buildULogsQuery(TbLogs logs){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if (logs == null) {
            return null;
        }

        //id
        if (logs.getId() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("id.keyword", logs.getId())
            );
        }
        //nid
        if (!StringUtils.isEmpty(logs.getNid())) {
            queryBuilder.must(
                    QueryBuilders.termQuery("nid.keyword", logs.getNid())
            );
        }
        //username
        if (!StringUtils.isEmpty(logs.getUsername())){
            queryBuilder.must(
                    QueryBuilders.termQuery("username.keyword", logs.getUsername())
            );
        }
        //opUser
        if (!StringUtils.isEmpty(logs.getOpUser())){
            queryBuilder.must(
                    QueryBuilders.termQuery("opUser.keyword", logs.getOpUser())
            );
        }
        //opDetails
        if (!StringUtils.isEmpty(logs.getOpDetails())){
            queryBuilder.must(
                    QueryBuilders.matchQuery("opDetails", logs.getOpDetails())
            );
        }
        //workType
        if (!StringUtils.isEmpty(logs.getWorkType())) {
            queryBuilder.must(
                    QueryBuilders.termQuery("workType.keyword", logs.getWorkType())
            );
        }
        return queryBuilder;
    }

    /**
     * 从ES查询数据中解析LogsList
     * @param hits
     * @return
     */
    private List<TbLogs> getLogsList(SearchHits<TbLogs> hits){
        System.out.println(hits.getSearchHits());
        List<TbLogs> res = new ArrayList<>((int)hits.getTotalHits());
        for (SearchHit<TbLogs> hit : hits.getSearchHits()) {
            res.add(hit.getContent());
        }
        return res;
    }
}
