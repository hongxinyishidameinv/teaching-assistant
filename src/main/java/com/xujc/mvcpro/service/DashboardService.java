package com.xujc.mvcpro.service;

import java.util.Map;

public interface DashboardService {

    /**
     * 获取统计数据
     * @return 包含用户总数、课程总数、教师总数的Map
     */
    Map<String, Object> getStatistics();
}