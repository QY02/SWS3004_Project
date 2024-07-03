package org.cs304.backend.constant;

// 推荐系统使用的交互类型
//对于一个活动：（5分rating）
//计算优先级：
//有星级评论按星级
//有收藏5分
//参加过4分
//有点赞动态4分、差评2分
//只历史记录3分
public class constant_InteractionType {
    public static final int RECOMMENDATION = 0;
    public static final int STAR = 5;
    public static final int FAVORITE = 4;
    public static final int ATTEND = 3;
    public static final int BLOG = 2;
    public static final int HISTORY = 1;
}
