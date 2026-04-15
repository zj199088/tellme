package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.AiConversation;

import java.util.List;

public interface AiConversationService extends IService<AiConversation> {
    List<AiConversation> getByUserId(Integer userId);
}