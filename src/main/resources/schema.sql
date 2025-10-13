CREATE TABLE IF NOT EXISTS COURSE (
    canvas_id       VARCHAR(10)     PRIMARY KEY,
    title           VARCHAR,
    course_code     VARCHAR,
    start_date      DATE,
    end_date        DATE
);