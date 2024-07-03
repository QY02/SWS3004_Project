package org.cs304.backend.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.cs304.backend.entity.Comment;
import org.cs304.backend.entity.UserBlogInteraction;
import org.cs304.backend.mapper.CommentMapper;
import org.cs304.backend.mapper.UserBlogInteractionMapper;
import org.cs304.backend.service.IUserBlogInteractionService;
import org.cs304.backend.service.IUserInteractionService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.cs304.backend.constant.constant_InteractionType.BLOG;
import static org.cs304.backend.constant.constant_VoteType.DOWNVOTE;
import static org.cs304.backend.constant.constant_VoteType.UPVOTE;

@Service
public class UserBlogInteractionServiceImpl extends ServiceImpl<UserBlogInteractionMapper, UserBlogInteraction> implements IUserBlogInteractionService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private IUserInteractionService userInteractionService;

    @Override
    public JSONObject getBlog(Integer commentId, String userId) {
        List<UserBlogInteraction> userBlogInteraction = baseMapper.selectList(new QueryWrapper<UserBlogInteraction>().eq("comment_id",commentId).eq("user_id",userId));
        if (userBlogInteraction.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("voteType", 0);
            return jsonObject;
        }
        JSONObject jsonObject = new JSONObject();
        for (UserBlogInteraction blogInteraction : userBlogInteraction) {
            if (blogInteraction.getVoteType()) {
                jsonObject.put("voteType", UPVOTE);
            } else {
                jsonObject.put("voteType", DOWNVOTE);
            }
        }
        return jsonObject;
    }

    @Override
    public void changeVote(Integer commentId, String userId, Integer voteType) {
        UserBlogInteraction userBlogInteraction = baseMapper.selectOne(new QueryWrapper<UserBlogInteraction>().eq("comment_id",commentId).eq("user_id",userId));
        Comment comment = commentMapper.selectById(commentId);
        if (userBlogInteraction == null) {
            userBlogInteraction = new UserBlogInteraction();
            userBlogInteraction.setCommentId(commentId);
            userBlogInteraction.setUserId(userId);
            if (voteType == UPVOTE) {
                userBlogInteraction.setVoteType(true);
                userInteractionService.changeUserInteraction(userId,comment.getEventId(),BLOG,4);
            } else if (voteType == DOWNVOTE) {
                userBlogInteraction.setVoteType(false);
                userInteractionService.changeUserInteraction(userId,comment.getEventId(),BLOG,2);
            } else {
                throw new RuntimeException("voteType error");
            }
            baseMapper.insert(userBlogInteraction);
            if (voteType == UPVOTE) {
                comment.setUpVote(comment.getUpVote() + 1);
            } else {
                comment.setDownVote(comment.getDownVote() + 1);
            }
            commentMapper.updateById(comment);
        } else {
            if (voteType == UPVOTE) {
                if (userBlogInteraction.getVoteType()) {
                    throw new RuntimeException("已经点过赞了");
                }
                userInteractionService.changeUserInteraction(userId,comment.getEventId(),BLOG,4);
                userBlogInteraction.setVoteType(true);
                baseMapper.update(new UpdateWrapper<UserBlogInteraction>().eq("comment_id",commentId).eq("user_id",userId).set("vote_type",true));
                comment.setUpVote(comment.getUpVote() + 1);
                comment.setDownVote(comment.getDownVote() - 1);
                commentMapper.updateById(comment);
            } else if (voteType == DOWNVOTE) {
                if (!userBlogInteraction.getVoteType()) {
                    throw new RuntimeException("已经踩过了");
                }
                userInteractionService.changeUserInteraction(userId,comment.getEventId(),BLOG,2);
                userBlogInteraction.setVoteType(false);
                baseMapper.update(new UpdateWrapper<UserBlogInteraction>().eq("comment_id",commentId).eq("user_id",userId).set("vote_type",false));
                comment.setUpVote(comment.getUpVote() - 1);
                comment.setDownVote(comment.getDownVote() + 1);
                commentMapper.updateById(comment);
            } else {
                userInteractionService.changeUserInteraction(userId,comment.getEventId(),BLOG,3);
                if (userBlogInteraction.getVoteType()) {
                    comment.setUpVote(comment.getUpVote() - 1);
                    baseMapper.delete(new QueryWrapper<UserBlogInteraction>().eq("comment_id",commentId).eq("user_id",userId).eq("vote_type",true));
                } else {
                    comment.setDownVote(comment.getDownVote() - 1);
                    baseMapper.delete(new QueryWrapper<UserBlogInteraction>().eq("comment_id",commentId).eq("user_id",userId).eq("vote_type",false));
                }
                commentMapper.updateById(comment);
            }
        }
    }

}
