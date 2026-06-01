package com.xujc.mvcpro.service.impl;

import com.xujc.mvcpro.mapper.AttendanceMapper;
import com.xujc.mvcpro.pojo.Attendance;
import com.xujc.mvcpro.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    @Transactional
    public void saveAttendance(Long courseId, LocalDate date, List<Map<String, Object>> attendanceList) {
        // 先删除当天该课程的考勤记录
        attendanceMapper.deleteByCourseAndDate(courseId, date);
        
        // 逐条插入记录
        for (Map<String, Object> item : attendanceList) {
            Attendance attendance = new Attendance();
            attendance.setCourseId(courseId);
            attendance.setStudentId((Integer) item.get("studentId"));
            attendance.setAttendanceDate(date);
            attendance.setStatus((Integer) item.get("status"));
            attendance.setRemark((String) item.get("remark"));
            attendanceMapper.insert(attendance);
        }
    }

    @Override
    public List<Attendance> getTodayAttendance(Long courseId, LocalDate date) {
        return attendanceMapper.selectByCourseAndDate(courseId, date);
    }

    @Override
    public List<Attendance> getAttendanceList(Long courseId, Integer studentId, String studentName, LocalDate startDate, LocalDate endDate, Integer status, int page, int size) {
        int offset = (page - 1) * size;
        return attendanceMapper.selectPage(courseId, studentId, studentName, startDate, endDate, status, offset, size);
    }

    @Override
    public int getAttendanceCount(Long courseId, Integer studentId, String studentName, LocalDate startDate, LocalDate endDate, Integer status) {
        return attendanceMapper.count(courseId, studentId, studentName, startDate, endDate, status);
    }

    @Override
    public List<Attendance> getStudentAttendanceList(Integer studentId, Long courseId, LocalDate startDate, LocalDate endDate, Integer status, int page, int size) {
        int offset = (page - 1) * size;
        return attendanceMapper.selectByStudentId(studentId, courseId, startDate, endDate, status, offset, size);
    }

    @Override
    public int getStudentAttendanceCount(Integer studentId, Long courseId, LocalDate startDate, LocalDate endDate, Integer status) {
        return attendanceMapper.countByStudentId(studentId, courseId, startDate, endDate, status);
    }

    @Override
    public Map<String, Integer> getStudentAttendanceStats(Integer studentId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("normal", attendanceMapper.countByStudentIdAndStatus(studentId, 1));
        stats.put("late", attendanceMapper.countByStudentIdAndStatus(studentId, 2));
        stats.put("leave", attendanceMapper.countByStudentIdAndStatus(studentId, 3));
        stats.put("absent", attendanceMapper.countByStudentIdAndStatus(studentId, 4));
        return stats;
    }
}
