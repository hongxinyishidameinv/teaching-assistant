-- 插入更多选课记录（一个学生可以选多门课程，一门课程可以有多个学生）

-- 假设学生ID：9(陈昊)、10(白浩辰)、11(陈维骏)、12-15(新增学生)
-- 假设课程ID：1-10

-- 先插入新学生（如果user表中有更多学生的话）
-- INSERT INTO user (uid, username, password, email, type, status) VALUES
-- (12, '新学生1', '123456', 'student12@xujc.edu.cn', 2, 1),
-- (13, '新学生2', '123456', 'student13@xujc.edu.cn', 2, 1),
-- (14, '新学生3', '123456', 'student14@xujc.edu.cn', 2, 1),
-- (15, '新学生4', '123456', 'student15@xujc.edu.cn', 2, 1);

-- 清空现有选课数据
TRUNCATE TABLE student_course;

-- 插入新的选课记录
-- 课程1：Java企业级Web应用技术（教师2-苏泽荫）
INSERT INTO student_course (student_id, course_id, status) VALUES
(9, 1, 1),   -- 陈昊
(10, 1, 1),  -- 白浩辰
(11, 1, 1),  -- 陈维骏
(12, 1, 1);  -- 学生12

-- 课程2：计算机图形学（教师2-苏泽荫）
INSERT INTO student_course (student_id, course_id, status) VALUES
(9, 2, 1),   -- 陈昊
(10, 2, 1),  -- 白浩辰
(13, 2, 1),  -- 学生13
(14, 2, 1);  -- 学生14

-- 课程3：机器学习（教师2-苏泽荫）
INSERT INTO student_course (student_id, course_id, status) VALUES
(9, 3, 1),   -- 陈昊
(11, 3, 1),  -- 陈维骏
(12, 3, 1),  -- 学生12
(15, 3, 1);  -- 学生15

-- 课程4：计算机组成（教师2-苏泽荫）
INSERT INTO student_course (student_id, course_id, status) VALUES
(10, 4, 1),  -- 白浩辰
(11, 4, 1),  -- 陈维骏
(13, 4, 1),  -- 学生13
(15, 4, 1);  -- 学生15

-- 课程5：人工智能（教师2-苏泽荫）
INSERT INTO student_course (student_id, course_id, status) VALUES
(9, 5, 1),   -- 陈昊
(10, 5, 1),  -- 白浩辰
(12, 5, 1),  -- 学生12
(14, 5, 1);  -- 学生14

-- 课程6：数据结构与算法（教师2-苏泽荫）
INSERT INTO student_course (student_id, course_id, status) VALUES
(11, 6, 1),  -- 陈维骏
(12, 6, 1),  -- 学生12
(13, 6, 1),  -- 学生13
(14, 6, 1);  -- 学生14

-- 课程7：数据库原理（教师2-苏泽荫）
INSERT INTO student_course (student_id, course_id, status) VALUES
(9, 7, 1),   -- 陈昊
(10, 7, 1),  -- 白浩辰
(15, 7, 1);  -- 学生15

-- 课程8：操作系统（教师2-苏泽荫）
INSERT INTO student_course (student_id, course_id, status) VALUES
(11, 8, 1),  -- 陈维骏
(12, 8, 1),  -- 学生12
(14, 8, 1),  -- 学生14
(15, 8, 1);  -- 学生15

SELECT '选课数据插入完成' AS result;