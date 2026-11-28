package com.chx.saleswithagent.AgentTest;

import com.chx.saleswithagent.Result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/agent")
public class controller {

    @Resource
    private AgentServiceImpl agentService;
    @PostMapping("/ping")
    public Result<Map<String,Object>> ping(@RequestBody Map<String,String> body){
        String message = body.getOrDefault("message","你好");
        long start = System.currentTimeMillis();
        String reply = agentService.ping(message);
        long cost = System.currentTimeMillis() - start;
        log.info("agent ping cost{}ms",cost);
        Map<String,Object> map = new HashMap<>();
        map.put("reply",reply);
        map.put("costMs",cost);
        return Result.ok(map);
    }
}
