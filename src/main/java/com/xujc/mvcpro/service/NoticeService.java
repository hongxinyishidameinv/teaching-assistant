package com.xujc.mvcpro.service;

import com.xujc.mvcpro.common.PageResult;
import com.xujc.mvcpro.pojo.Notice;

import java.util.List;

public interface NoticeService {
    Notice save(Notice notice);
    Notice update(Notice notice);
    void deleteById(Long id);
    Notice findById(Long id);
    List<Notice> findAll();
    PageResult<Notice> findByPage(String title, Integer top, Integer status, int pageNum, int pageSize);
}