package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.AiConversation;
import com.fitness.app.mapper.AiConversationMapper;
import com.fitness.app.service.AiConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiConversationServiceImpl extends ServiceImpl<AiConversationMapper, AiConversation> implements AiConversationService {

    @Autowired
    private AiConversationMapper aiConversationMapper;

    @Override
    public List<AiConversation> getByUserId(Integer userId) {
        return aiConversationMapper.getByUserId(userId);
    }
}