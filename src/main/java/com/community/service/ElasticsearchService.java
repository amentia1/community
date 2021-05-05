package com.community.service;

import com.community.dao.elasticsearch.DiscussPostRepository;
import com.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author flunggg
 * @date 2020/8/8 23:36
 * @Email: chaste86@163.com
 */
@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    // 处理高亮显示
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 向ES服务器添加帖子信息
     * 也可以修改的
     * @param discussPost
     */
    public void save(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    /**
     * Page<DiscussPost> 是spring的page
     * 这里页需要分页查询
     * @param keyword
     * @param current 当前页（跟以前不一样）
     * @param limit 每页显示多少条
     * @return
     * QueryBuilders.boolQuery().should(titleQueryBuilder).should(contentQueryBuilder).should(
     * QueryBuilders.multiMatchQuery(keyword, "title", "content")
     */
    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        WildcardQueryBuilder titleQueryBuilder = QueryBuilders.wildcardQuery(
                "title", "*" + keyword + "*");
        WildcardQueryBuilder contentQueryBuilder = QueryBuilders.wildcardQuery(
                "content", "*" + keyword + "*");
        // 设置查询相关信息
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                // 关键字，和要搜索的字段
                .withQuery(QueryBuilders.boolQuery().should(titleQueryBuilder).should(contentQueryBuilder))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))  // 排序
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit)) // 分页
/*                .withHighlightFields( // 高亮显示
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                )*/
                .build();

        // 开始查询并分页
        return elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits(); // 得到多条数据封装在一起
                if(hits.getTotalHits() <= 0) {
                    return null;
                }

                // 否则有数据
                List<DiscussPost> list = new ArrayList<>();
                for(SearchHit hit : hits) {
                    DiscussPost discussPost = new DiscussPost();
                    // hit 其实是一个map
                    String id = hit.getSourceAsMap().get("id").toString();
                    discussPost.setId(Integer.valueOf(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    discussPost.setUserId(Integer.valueOf(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    discussPost.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    discussPost.setContent(content);

                    String status = hit.getSourceAsMap().get("status").toString();
                    discussPost.setStatus(Integer.valueOf(status));


                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    discussPost.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    discussPost.setCommentCount(Integer.valueOf(commentCount));

                    String type = hit.getSourceAsMap().get("type").toString();
                    discussPost.setType(Integer.valueOf(type));

                    String score = hit.getSourceAsMap().get("score").toString();
                    discussPost.setScore(Double.valueOf(score));

                    // 高亮显示
                    // HighlightField titleField = hit.getHighlightFields().get("title");
                    // if(titleField != null) {
                    //     // discussPost.setTitle(titleField.getFragments()[0].toString());
                    //     discussPost.setTitle(titleField.getFragments()[0].toString());
                    // }
                    // HighlightField contentField = hit.getHighlightFields().get("content");
                    // if(contentField != null) {
                    //     // 有很多段，取前面的
                    //     discussPost.setContent(contentField.getFragments()[0].toString());
                    // }

                    list.add(discussPost);
                }

                return new AggregatedPageImpl(list, pageable,
                        hits.getTotalHits(), searchResponse.getAggregations(), searchResponse.getScrollId(), hits.getMaxScore());
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }

        });

    }
}
