package com.chx.saleswithagent.AgentTest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AgentServiceImpl implements AgentService {
    private final ChatClient chatClient;

    public AgentServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String ping(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
