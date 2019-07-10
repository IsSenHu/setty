package com.setty.registry.controller;

import com.setty.commons.vo.registry.AppVO;
import com.setty.discovery.core.infs.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/10 17:46
 */
@RestController
public class TestController {
    private final LookupService<AppVO, Long> lookupService;

    @Autowired
    public TestController(LookupService<AppVO, Long> lookupService) {
        this.lookupService = lookupService;
    }

    @GetMapping("/test")
    public List<Map<Long, AppVO>> test() {
        return lookupService.getApplications();
    }
}
