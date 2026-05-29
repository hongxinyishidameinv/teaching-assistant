-- 清空现有选课数据
TRUNCATE TABLE student_course;

-- 学生选课数据分配方案
-- 共有33个学生(uid:9-41)，12门课程(id:1-12)
-- 每个学生选3-5门课程，每门课程分配8-15个学生

-- ============================================
-- 课程1: Java企业级Web应用技术 (教师2)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(9, 1, 1),  (10, 1, 1), (11, 1, 1), (12, 1, 1), (13, 1, 1),
(14, 1, 1), (15, 1, 1), (16, 1, 1), (17, 1, 1), (18, 1, 1),
(19, 1, 1), (20, 1, 1);

-- ============================================
-- 课程2: 高级编程 (教师3)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(9, 2, 1),  (10, 2, 1), (11, 2, 1), (21, 2, 1), (22, 2, 1),
(23, 2, 1), (24, 2, 1), (25, 2, 1), (26, 2, 1), (27, 2, 1);

-- ============================================
-- 课程3: 计算机图形学 (教师4)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(12, 3, 1), (13, 3, 1), (14, 3, 1), (21, 3, 1), (22, 3, 1),
(28, 3, 1), (29, 3, 1), (30, 3, 1), (31, 3, 1), (32, 3, 1),
(33, 3, 1), (34, 3, 1);

-- ============================================
-- 课程4: 嵌入式系统与物联网应用 (教师5)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(15, 4, 1), (16, 4, 1), (17, 4, 1), (23, 4, 1), (24, 4, 1),
(25, 4, 1), (26, 4, 1), (35, 4, 1), (36, 4, 1), (37, 4, 1),
(38, 4, 1);

-- ============================================
-- 课程5: 软件工程师实践 (教师6)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(9, 5, 1),  (18, 5, 1), (19, 5, 1), (20, 5, 1), (27, 5, 1),
(28, 5, 1), (29, 5, 1), (30, 5, 1), (39, 5, 1), (40, 5, 1),
(41, 5, 1);

-- ============================================
-- 课程6: 信息小程序开发 (教师7)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(10, 6, 1), (11, 6, 1), (12, 6, 1), (13, 6, 1), (31, 6, 1),
(32, 6, 1), (33, 6, 1), (34, 6, 1), (35, 6, 1), (36, 6, 1),
(37, 6, 1);

-- ============================================
-- 课程7: 教学实践论 (教师8)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(14, 7, 1), (15, 7, 1), (16, 7, 1), (17, 7, 1), (21, 7, 1),
(22, 7, 1), (23, 7, 1), (24, 7, 1), (38, 7, 1), (39, 7, 1),
(40, 7, 1), (41, 7, 1);

-- ============================================
-- 课程9: 机器学习 (教师4)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(9, 9, 1),  (10, 9, 1), (18, 9, 1), (19, 9, 1), (20, 9, 1),
(25, 9, 1), (26, 9, 1), (27, 9, 1), (28, 9, 1), (29, 9, 1),
(30, 9, 1);

-- ============================================
-- 课程10: 计算机组成 (教师4)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(11, 10, 1), (12, 10, 1), (13, 10, 1), (14, 10, 1), (15, 10, 1),
(31, 10, 1), (32, 10, 1), (33, 10, 1), (34, 10, 1), (35, 10, 1),
(36, 10, 1), (37, 10, 1);

-- ============================================
-- 课程11: 人工智能 (教师4)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(16, 11, 1), (17, 11, 1), (18, 11, 1), (21, 11, 1), (22, 11, 1),
(23, 11, 1), (24, 11, 1), (38, 11, 1), (39, 11, 1), (40, 11, 1),
(41, 11, 1);

-- ============================================
-- 课程12: 网络 (教师4)
-- ============================================
INSERT INTO student_course (student_id, course_id, status) VALUES
(9, 12, 1),  (19, 12, 1), (20, 12, 1), (25, 12, 1), (26, 12, 1),
(27, 12, 1), (28, 12, 1), (29, 12, 1), (30, 12, 1), (31, 12, 1),
(32, 12, 1);

-- ============================================
-- 为每个学生补充更多课程（确保每个学生至少3门课）
-- ============================================
-- 学生9 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (9, 6, 1), (9, 11, 1);

-- 学生10 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (10, 4, 1), (10, 7, 1);

-- 学生11 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (11, 5, 1), (11, 7, 1);

-- 学生12 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (12, 5, 1), (12, 7, 1);

-- 学生13 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (13, 5, 1), (13, 9, 1);

-- 学生14 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (14, 9, 1), (14, 11, 1);

-- 学生15 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (15, 5, 1), (15, 9, 1);

-- 学生16 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (16, 5, 1), (16, 10, 1);

-- 学生17 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (17, 6, 1), (17, 10, 1);

-- 学生18 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (18, 3, 1), (18, 6, 1);

-- 学生19 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (19, 3, 1), (19, 6, 1);

-- 学生20 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (20, 3, 1), (20, 4, 1);

-- 学生21 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (21, 1, 1), (21, 9, 1);

-- 学生22 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (22, 1, 1), (22, 9, 1);

-- 学生23 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (23, 1, 1), (23, 6, 1);

-- 学生24 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (24, 1, 1), (24, 6, 1);

-- 学生25 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (25, 1, 1), (25, 7, 1);

-- 学生26 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (26, 1, 1), (26, 7, 1);

-- 学生27 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (27, 1, 1), (27, 10, 1);

-- 学生28 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (28, 1, 1), (28, 10, 1);

-- 学生29 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (29, 1, 1), (29, 11, 1);

-- 学生30 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (30, 1, 1), (30, 11, 1);

-- 学生31 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (31, 1, 1), (31, 7, 1);

-- 学生32 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (32, 1, 1), (32, 7, 1);

-- 学生33 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (33, 2, 1), (33, 5, 1);

-- 学生34 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (34, 2, 1), (34, 5, 1);

-- 学生35 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (35, 2, 1), (35, 9, 1);

-- 学生36 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (36, 2, 1), (36, 9, 1);

-- 学生37 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (37, 3, 1), (37, 7, 1);

-- 学生38 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (38, 3, 1), (38, 6, 1);

-- 学生39 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (39, 4, 1), (39, 6, 1);

-- 学生40 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (40, 4, 1), (40, 10, 1);

-- 学生41 补充课程
INSERT INTO student_course (student_id, course_id, status) VALUES (41, 4, 1), (41, 10, 1);

SELECT '选课数据分配完成！' AS result;

-- 验证统计
SELECT 
    '每门课程学生数' AS stat_type,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 1) AS course_1,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 2) AS course_2,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 3) AS course_3,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 4) AS course_4,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 5) AS course_5,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 6) AS course_6,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 7) AS course_7,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 9) AS course_9,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 10) AS course_10,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 11) AS course_11,
    (SELECT COUNT(DISTINCT student_id) FROM student_course WHERE course_id = 12) AS course_12
UNION ALL
SELECT
    '每个学生选课数' AS stat_type,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 9) AS student_9,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 10) AS student_10,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 11) AS student_11,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 12) AS student_12,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 13) AS student_13,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 14) AS student_14,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 15) AS student_15,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 16) AS student_16,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 17) AS student_17,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 18) AS student_18,
    (SELECT COUNT(DISTINCT course_id) FROM student_course WHERE student_id = 19) AS student_19;