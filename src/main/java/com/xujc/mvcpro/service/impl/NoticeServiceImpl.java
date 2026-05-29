package com.xujc.mvcpro.service.impl;

import com.xujc.mvcpro.common.PageResult;
import com.xujc.mvcpro.mapper.NoticeMapper;
import com.xujc.mvcpro.pojo.Notice;
import com.xujc.mvcpro.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public Notice save(Notice notice) {
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        if (notice.getTop() == null) {
            notice.setTop(0);
        }
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }
        noticeMapper.insert(notice);
        return notice;
    }

    @Override
    public Notice update(Notice notice) {
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
        return noticeMapper.selectById(notice.getId());
    }

    @Override
    public void deleteById(Long id) {
        noticeMapper.deleteById(id);
    }

    @Override
    public Notice findById(Long id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public List<Notice> findAll() {
        return noticeMapper.selectAll();
    }

    @Override
    public PageResult<Notice> findByPage(String title, Integer top, Integer status, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Notice> notices = noticeMapper.selectByPage(title, top, status, offset, pageSize);
        int total = noticeMapper.countByCondition(title, top, status);
        int pages = (int) Math.ceil((double) total / pageSize);
        return new PageResult<>(notices, pageNum, pageSize, total, pages);
    }
}