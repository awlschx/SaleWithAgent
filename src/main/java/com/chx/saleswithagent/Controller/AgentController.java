package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.AgentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/agent")
public class AgentController {
    @Resource
    private AgentService agentService;

    @PostMapping("/chat")
    public Result agentChat(@RequestParam String message){
        return Result.ok(agentService.agentChat(message));
    }
}
