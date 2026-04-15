package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.AiConversation;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AiConversationMapper extends BaseMapper<AiConversation> {
    @Select("SELECT * FROM ai_conversations WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY created_at DESC")
    List<AiConversation> getByUserId(Integer userId);
}