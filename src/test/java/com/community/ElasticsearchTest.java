package com.community;

import com.community.dao.DiscussPostMapper;
import com.community.dao.elasticsearch.DiscussPostRepository;
import com.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author flunggg
 * @date 2020/8/8 12:04
 * @Email: chaste86@163.com
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 以CommunityApplication.class配置的启动测试
public class ElasticsearchTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testInset() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    /**
     * 插入多条
     */
    @Test
    public void testInsetAll() {
        // 后面重构代码，添加一个orderMode：0表示按原来的排序
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100, 0));
    }

    /**
     * 修改
     */
    @Test
    public void testUpdate() {
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(265);
        discussPost.setTitle("互联网校招啦！");
        discussPostRepository.save(discussPost);
    }

    /**
     * 删除
     */
    @Test
    public void testDel() {
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(265);
        discussPostRepository.delete(discussPost);
    }

    /**
     * 删除所有数据，很危险，慎重
     */
    @Test
    public void testDelAll() {
        discussPostRepository.deleteAll();
    }

    /*-----------------搜索---------------*/

    /*高亮显示：就是在关键字加上标签，使得变色*/
    @Test
    public void searchQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content")) // 条件
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))  // 排序
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10)) // 分页
                .withHighlightFields( // 高亮显示
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        // 底层调用：elasticsearchTemplate.queryForPage(searchQuery, class, new SearchResultMapper(){})
        // 底层获取高亮显示的值，但是没有返回
        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        // 一共有多少条匹配
        System.out.println(page.getTotalElements());
        // 一共有多少页
        System.out.println(page.getTotalPages());
        // 当前在第几页
        System.out.println(page.getNumber());
        // 每页有多少条数据
        System.out.println(page.getSize());
        for(DiscussPost discussPost : page) {
            System.out.println(discussPost);
        }
    }

    /**
     * 真高亮显示
     */
    @Test
    public void searchQueryHight() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content")) // 条件
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))  // 排序
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10)) // 分页
                .withHighlightFields( // 高亮显示
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        Page<DiscussPost> page = elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
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
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if(titleField != null) {
                        // 有很多段，取前面的，因为高亮显示肯定不会全文给给你高亮，具体参考百度等
                        discussPost.setTitle(titleField.getFragments()[0].toString());
                    }
                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if(contentField != null) {
                        System.out.println();
                        // 有很多段，取前面的
                        discussPost.setTitle(contentField.getFragments()[0].toString());
                    }

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

        // 一共有多少条匹配
        System.out.println(page.getTotalElements());
        // 一共有多少页
        System.out.println(page.getTotalPages());
        // 当前在第几页
        System.out.println(page.getNumber());
        // 每页有多少条数据
        System.out.println(page.getSize());
        for(DiscussPost discussPost : page) {
            System.out.println(discussPost);
        }
    }
}
