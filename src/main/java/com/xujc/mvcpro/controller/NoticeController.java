package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.pojo.Notice;
import com.xujc.mvcpro.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public ApiResponse getNoticesByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer top,
            @RequestParam(required = false) Integer status) {
        return ApiResponse.ok("查询成功", noticeService.findByPage(title, top, status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse getNoticeById(@PathVariable Long id) {
        Notice notice = noticeService.findById(id);
        if (notice == null) {
            return ApiResponse.error(404, "公告不存在");
        }
        return ApiResponse.ok("查询成功", notice);
    }

    @PostMapping
    public ApiResponse createNotice(@RequestBody Notice notice) {
        Notice savedNotice = noticeService.save(notice);
        return ApiResponse.ok("创建成功", savedNotice);
    }

    @PutMapping("/{id}")
    public ApiResponse updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        Notice existingNotice = noticeService.findById(id);
        if (existingNotice == null) {
            return ApiResponse.error(404, "公告不存在");
        }
        notice.setId(id);
        Notice updatedNotice = noticeService.update(notice);
        return ApiResponse.ok("更新成功", updatedNotice);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteNotice(@PathVariable Long id) {
        Notice existingNotice = noticeService.findById(id);
        if (existingNotice == null) {
            return ApiResponse.error(404, "公告不存在");
        }
        noticeService.deleteById(id);
        return ApiResponse.ok("删除成功", null);
    }
}