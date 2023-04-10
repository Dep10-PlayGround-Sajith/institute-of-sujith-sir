CREATE TABLE IF NOT EXISTS Payment(
    student_id VARCHAR(50) PRIMARY KEY ,
    month ENUM(
        'January','February','March','April','May','June','July','August','September','October','November','December'
        ) NOT NULL ,
    fee DECIMAL NOT NULL,
    class_name VARCHAR(200) NOT NULL
);