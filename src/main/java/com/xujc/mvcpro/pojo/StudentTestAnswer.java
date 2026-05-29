package com.xujc.mvcpro.pojo;

import java.time.LocalDateTime;

public class StudentTestAnswer {
    private Long id;
    private Integer studentId;
    private Long testId;
    private Integer questionId;
    private String answer;
    private Integer score;
    private Integer status; // 0-待批改, 1-已批改(客观题自动批改), 2-教师批改
    private LocalDateTime submitTime;
    private LocalDateTime gradeTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public Long getTestId() { return testId; }
    public void setTestId(Long testId) { this.testId = testId; }
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public LocalDateTime getGradeTime() { return gradeTime; }
    public void setGradeTime(LocalDateTime gradeTime) { this.gradeTime = gradeTime; }
}
