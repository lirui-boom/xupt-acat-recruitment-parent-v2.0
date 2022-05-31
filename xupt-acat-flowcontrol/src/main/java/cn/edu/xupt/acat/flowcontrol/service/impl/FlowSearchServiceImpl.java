package cn.edu.xupt.acat.flowcontrol.service.impl;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.domain.vo.FlowEsEntityVo;
import cn.edu.xupt.acat.flowcontrol.service.library.FlowControlConstant;
import cn.edu.xupt.acat.flowcontrol.service.FlowSearchService;
import cn.edu.xupt.acat.lib.response.SearchPage;
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
public class FlowSearchServiceImpl implements FlowSearchService {

    private static Logger logger = Logger.getLogger(FlowSearchServiceImpl.class.toString());

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public List<FlowEsEntity> query(FlowEsEntity entity) {
        logger.info("query es index is " + FlowControlConstant.FLOW_ES_INDEX + ", query : " + JSON.toJSONString(entity));
        Query query = buildQueryCondition(entity);
        SearchHits<FlowEsEntity> hits = elasticsearchRestTemplate.search(query, FlowEsEntity.class);
        logger.info("query es result :  " + JSON.toJSONString(hits));
        return getFlowEntityList(hits);
    }

    @Override
    public SearchPage<List<FlowEsEntity>> search(FlowEsEntityVo vo) {
        logger.info("search es index is " + FlowControlConstant.FLOW_ES_INDEX + ", search : " + JSON.toJSONString(vo));

        if (vo.getPageNum() <= 0) {
            vo.setPageNum(1);
        }

        if (vo.getPageSize() <= 0) {
            vo.setPageSize(10);
        }

        Query query = buildSearchCondition(vo);
        SearchHits<FlowEsEntity> hits = elasticsearchRestTemplate.search(query, FlowEsEntity.class);
        List<FlowEsEntity> esEntities = getFlowEntityList(hits);
        SearchPage<List<FlowEsEntity>> res = new SearchPage<>(
                vo.getPageNum(),
                vo.getPageSize(),
                hits.getTotalHits(),
                esEntities
        );
        logger.info("search es result :  " + JSON.toJSONString(hits) +  ", total count : " + hits.getTotalHits()  + ", data : " + JSON.toJSONString(esEntities));
        return res;
    }

    /**
     * 构造Query查询条件
     * @param entity entity
     * @return Query
     */
    private Query buildQueryCondition(FlowEsEntity entity){
        BoolQueryBuilder queryBuilder = buildFlowEntityQuery(entity);
        Query query =  new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        logger.info("query es condition :  " + JSON.toJSONString(query));
        return query;
    }

    /**
     * 构造分页搜索查询条件
     * @param vo vo
     * @return  Query
     */
    private Query buildSearchCondition(FlowEsEntityVo vo) {
        QueryBuilder queryBuilder = buildFlowEntityQuery(vo.getEntity());
        Query query =  new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(vo.getPageNum() - 1, vo.getPageSize()))
                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .build();
        logger.info("search es condition :  " + JSON.toJSONString(query));
        return query;
    }

    /**
     * 构造User查询条件
     * @param entity entity
     * @return BoolQueryBuilder
     */
    private BoolQueryBuilder buildFlowEntityQuery(FlowEsEntity entity){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if (entity == null) {
            return null;
        }

        //id
        if (entity.getId() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("id.keyword", entity.getId())
            );
        }
        //nid
        if (!StringUtils.isEmpty(entity.getNid())){
            queryBuilder.must(
                    QueryBuilders.termQuery("nid.keyword", entity.getNid())
            );
        }

        // user info
        //uid
        if (entity.getUserId() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("userId.keyword", entity.getUserId())
            );
        }
        //username
        if (!StringUtils.isEmpty(entity.getUsername())){
            queryBuilder.must(
                    QueryBuilders.termQuery("username.keyword", entity.getUsername())
            );
        }
        //nickName
        if (!StringUtils.isEmpty(entity.getNickName())){
            queryBuilder.must(
                    QueryBuilders.matchQuery("nickName", entity.getNickName())
            );
        }
        //email
        if (!StringUtils.isEmpty(entity.getEmail())){
            queryBuilder.must(
                    QueryBuilders.termQuery("email.keyword", entity.getEmail())
            );
        }
        //userStatus
        if (entity.getUserStatus() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("userStatus.keyword", entity.getUserStatus())
            );
        }

        //apply info
        //snumber;
        if (!StringUtils.isEmpty(entity.getSnumber())){
            queryBuilder.must(
                    QueryBuilders.termQuery("snumber.keyword", entity.getSnumber())
            );
        }
        //realName;
        if (!StringUtils.isEmpty(entity.getRealName())){
            queryBuilder.must(
                    QueryBuilders.matchQuery("realName", entity.getRealName())
            );
        }
        //className;
        if (!StringUtils.isEmpty(entity.getClassName())){
            queryBuilder.must(
                    QueryBuilders.matchQuery("className", entity.getClassName())
            );
        }
        //sex;
        if (entity.getSex() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("sex.keyword", entity.getSex())
            );
        }
        //phone;
        if (!StringUtils.isEmpty(entity.getPhone())){
            queryBuilder.must(
                    QueryBuilders.termQuery("phone.keyword", entity.getPhone())
            );
        }
        //applyContent;
        if (!StringUtils.isEmpty(entity.getApplyContent())){
            queryBuilder.must(
                    QueryBuilders.matchQuery("applyContent", entity.getApplyContent())
            );
        }

        //opUser;
        if (!StringUtils.isEmpty(entity.getOpUser())){
            queryBuilder.must(
                    QueryBuilders.termQuery("opUser.keyword", entity.getOpUser())
            );
        }
        //serviceLine;
        if (!StringUtils.isEmpty(entity.getServiceLine())){
            queryBuilder.must(
                    QueryBuilders.termQuery("serviceLine.keyword", entity.getServiceLine())
            );
        }
        //version;
        if (entity.getVersion() != null){
            queryBuilder.must(
                    QueryBuilders.termQuery("version.keyword", entity.getVersion())
            );
        }
        //turns;
        if (entity.getTurns() != null){
            queryBuilder.must(
                    QueryBuilders.termQuery("turns.keyword", entity.getTurns())
            );
        }
        //workType;
        if (!StringUtils.isEmpty(entity.getWorkType())){
            queryBuilder.must(
                    QueryBuilders.termQuery("workType.keyword", entity.getWorkType())
            );
        }
        //status
        if (entity.getStatus() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("status.keyword", entity.getStatus())
            );
        }
        return queryBuilder;
    }

    /**
     * 从ES查询数据中解析FlowEntityList
     * @param hits hits
     * @return List<FlowEsEntity>
     */
    private List<FlowEsEntity> getFlowEntityList(SearchHits<FlowEsEntity> hits){
        List<FlowEsEntity> res = new ArrayList<>((int)hits.getTotalHits());
        for (SearchHit<FlowEsEntity> hit : hits.getSearchHits()) {
            res.add(hit.getContent());
        }
        return res;
    }
}
