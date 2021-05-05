package com.community.dao.elasticsearch;

import com.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author flunggg
 * @date 2020/8/8 17:34
 * @Email: chaste86@163.com
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
