package cn.edu.xupt.acat.user.service.impl;

import cn.edu.xupt.acat.lib.response.SearchPage;
import cn.edu.xupt.acat.user.domain.entity.TbUser;
import cn.edu.xupt.acat.user.domain.vo.UserSearchVo;
import cn.edu.xupt.acat.user.library.UserConstant;
import cn.edu.xupt.acat.user.service.UserSearchService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
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
public class UserSearchServiceImpl implements UserSearchService {

    private static Logger logger = Logger.getLogger(UserSearchServiceImpl.class.toString());

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public List<TbUser> query(TbUser user) {
        logger.info("query es index is " + UserConstant.USER_INFO_ES_INDEX + ", query : " + JSON.toJSONString(user));
        Query query = buildQueryCondition(user);
        SearchHits<TbUser> hits = elasticsearchRestTemplate.search(query, TbUser.class);
        logger.info("query es result :  " + JSON.toJSONString(hits));
        return getUserList(hits);
    }

    @Override
    public SearchPage<List<TbUser>> search(UserSearchVo vo) {
        logger.info("search es index is " + UserConstant.USER_INFO_ES_INDEX + ", search : " + JSON.toJSONString(vo));

        if (vo.getPageNum() <= 0) {
            vo.setPageNum(1);
        }

        if (vo.getPageSize() <= 0) {
            vo.setPageSize(10);
        }

        Query query = buildSearchCondition(vo);
        SearchHits<TbUser> hits = elasticsearchRestTemplate.search(query, TbUser.class);
        List<TbUser> userList = getUserList(hits);
        SearchPage<List<TbUser>> res = new SearchPage<>(
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
     * @param user
     * @return
     */
    private Query buildQueryCondition(TbUser user){
        BoolQueryBuilder queryBuilder = buildUserQuery(user);
        Query query =  new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        logger.info("query es condition :  " + JSON.toJSONString(query));
        return query;
    }

    /**
     * 构造分页搜索查询条件
     * @param vo
     * @return
     */
    private Query buildSearchCondition(UserSearchVo vo) {
        QueryBuilder queryBuilder = buildUserQuery(vo.getUser());
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
     * @param user
     * @return
     */
    private BoolQueryBuilder buildUserQuery(TbUser user){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if (user == null) {
            return null;
        }

        //id
        if (user.getId() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("id", user.getId())
            );
        }
        //username
        if (!StringUtils.isEmpty(user.getUsername())){
            queryBuilder.must(
                    QueryBuilders.termQuery("username", user.getUsername())
            );
        }
        //nickName
        if (!StringUtils.isEmpty(user.getNickName())){
            queryBuilder.must(
                    QueryBuilders.matchQuery("nickName", user.getNickName())
            );
        }
        //email
        if (!StringUtils.isEmpty(user.getEmail())){
            queryBuilder.must(
                    QueryBuilders.termQuery("email", user.getEmail())
            );
        }
        //status
        if (user.getStatus() != null) {
            queryBuilder.must(
                    QueryBuilders.termQuery("status", user.getStatus())
            );
        }
        return queryBuilder;
    }

    /**
     * 从ES查询数据中解析UserList
     * @param hits
     * @return
     */
    private List<TbUser> getUserList(SearchHits<TbUser> hits){
        System.out.println(hits.getSearchHits());
        List<TbUser> res = new ArrayList<>((int)hits.getTotalHits());
        for (SearchHit<TbUser> hit : hits.getSearchHits()) {
            res.add(hit.getContent());
        }
        return res;
    }
}
