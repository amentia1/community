package com.community.controller;

import com.community.entity.DiscussPost;
import com.community.service.ElasticsearchService;
import com.community.service.LikeService;
import com.community.service.UserService;
import com.community.util.CommunityConstant;
import com.community.util.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author flunggg
 * @date 2020/8/9 0:13
 * @Email: chaste86@163.com
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 搜索时，要把作者和点赞显示
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    /**
     * 格式：/search?keyword=xxx
     * @param keyword
     * @param page 这个是我们自己的分页
     * @param model
     * @return 搜索的结果
     */
    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        if(StringUtils.isBlank(keyword)) {
            return "redirect:/index";
        }
        // 搜索帖子
        // searchDiscussPost中的分页是从0开始，而自己写的page是从1开始，所以-1
        org.springframework.data.domain.Page<DiscussPost> searchResult
                = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(searchResult != null) {
            for(DiscussPost discussPost : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", discussPost);
                // 作者
                map.put("user", userService.findUserById(discussPost.getUserId()));
                // 帖子点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId()));

                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts", discussPosts);
        // 页面显示关键词方便，传回去
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult != null ? (int) searchResult.getTotalElements() : 0);

        return "/site/search";
    }
}
