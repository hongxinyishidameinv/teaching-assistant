package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.mapper.CourseMapper;
import com.xujc.mvcpro.pojo.Course;
import com.xujc.mvcpro.pojo.StudentCourse;
import com.xujc.mvcpro.pojo.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师端课程管理控制器
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherCourseController {

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public String test() {
        return "{\"code\":200,\"message\":\"测试成功\",\"data\":\"Hello World\"}";
    }
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/test-db")
    public ApiResponse testDb() {
        try {
            System.out.println("测试数据库连接...");
            // 查询课程表数量
            int courseCount = courseMapper.countCourses();
            System.out.println("课程表记录数: " + courseCount);
            
            // 查询学生选课表数量（测试表是否存在）
            int studentCount = courseMapper.countStudentCourses(1);
            System.out.println("课程1的学生数: " + studentCount);
            
            return ApiResponse.ok("数据库连接成功", courseCount);
        } catch (Exception e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "数据库连接失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师的课程列表
     * GET /api/teacher/courses?teacherId=xxx
     * 
     * 优先从请求参数获取teacherId，如果没有则从Session获取当前登录用户
     */
    @GetMapping("/courses")
    public ApiResponse getTeacherCourses(
            @RequestParam(required = false) Integer teacherId,
            HttpSession session) {
        
        // 优先使用请求参数中的teacherId
        if (teacherId == null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                teacherId = user.getUid();
            } else {
                // 默认使用教师ID=2（苏泽荫）进行测试
                teacherId = 2;
            }
        }
        
        List<Course> courses = courseMapper.findCoursesByTeacherId(teacherId);
        return ApiResponse.ok("获取成功", courses);
    }

    /**
     * 获取课程的学生列表（分页）
     * GET /api/teacher/courses/{courseId}/students?page=1&size=10
     */
    @GetMapping("/courses/{courseId}/students")
    public ApiResponse getCourseStudents(
            @PathVariable Integer courseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            System.out.println("获取课程学生列表 - courseId: " + courseId + ", page: " + page + ", size: " + size);
            
            int offset = (page - 1) * size;
            List<StudentCourse> students = courseMapper.findStudentsByCourseId(courseId, offset, size);
            int total = courseMapper.countStudentsByCourseId(courseId);
            
            System.out.println("查询结果 - students count: " + (students != null ? students.size() : 0) + ", total: " + total);
            
            Map<String, Object> result = new HashMap<>();
            result.put("students", students);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("totalPages", (int) Math.ceil((double) total / size));
            
            return ApiResponse.ok("获取成功", result);
        } catch (Exception e) {
            System.err.println("获取学生列表失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取学生列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出课程学生名单为Excel文件
     * GET /api/teacher/courses/{courseId}/students/export
     */
    @GetMapping("/courses/{courseId}/students/export")
    public void exportCourseStudents(
            @PathVariable Integer courseId,
            HttpServletResponse response) {
        try {
            System.out.println("=== 导出课程学生名单 ===");
            System.out.println("课程ID: " + courseId);
            
            // 获取课程信息
            Course course = courseMapper.findById(courseId);
            if (course == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("课程不存在");
                return;
            }
            
            // 获取所有学生（不分页）
            List<StudentCourse> students = courseMapper.findStudentsByCourseId(courseId, 0, 10000);
            
            if (students == null || students.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("该课程暂无学生");
                return;
            }
            
            System.out.println("学生数量: " + students.size());
            
            // 创建Excel工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("学生名单");
            
            // 创建标题样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // 创建普通单元格样式
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"序号", "学号", "姓名", "邮箱"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据
            int rowNum = 1;
            for (StudentCourse student : students) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(rowNum - 1);
                cell0.setCellStyle(cellStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(student.getUid() != null ? student.getUid().toString() : "");
                cell1.setCellStyle(cellStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(student.getUsername() != null ? student.getUsername() : "");
                cell2.setCellStyle(cellStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(student.getEmail() != null ? student.getEmail() : "");
                cell3.setCellStyle(cellStyle);
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 额外增加一些宽度
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024);
            }
            
            // 设置响应头
            String fileName = course.getCourseName() + "_学生名单.xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            
            // 写入响应流
            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            }
            
            workbook.close();
            
            System.out.println("导出成功: " + fileName);
            System.out.println("=== 导出课程学生名单结束 ===");
            
        } catch (IOException e) {
            System.err.println("导出失败: " + e.getMessage());
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("导出失败: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
