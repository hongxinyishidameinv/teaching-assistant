package com.xujc.mvcpro.service;

import com.xujc.mvcpro.pojo.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    void saveAttendance(Long courseId, LocalDate date, List<Map<String, Object>> attendanceList);
    
    List<Attendance> getTodayAttendance(Long courseId, LocalDate date);
    
    List<Attendance> getAttendanceList(Long courseId, Integer studentId, String studentName, LocalDate startDate, LocalDate endDate, Integer status, int page, int size);

    int getAttendanceCount(Long courseId, Integer studentId, String studentName, LocalDate startDate, LocalDate endDate, Integer status);
}
