CREATE TABLE IF NOT EXISTS Payment
(
    student_id VARCHAR(50) PRIMARY KEY,
    month      ENUM (
        'January','February','March','April','May','June','July','August','September','October','November','December'
        )                   NOT NULL,
    fee        DECIMAL      NOT NULL,
    class_name VARCHAR(200) NOT NULL
);
CREATE TABLE IF NOT EXISTS  Student(
    student_id VARCHAR(50) PRIMARY KEY ,
    name VARCHAR(100) NOT NULL ,
    grade ENUM(
        'ONE','TWO','THREE','FOUR','FIVE','SIX','SEVEN','EIGHT','NINE','TEN','ELEVEN'
        )NOT NULL ,
    medium ENUM('SINHALA','ENGLISH') NOT NULL,
    gender ENUM('MALE','FEMALE')
);

CREATE TABLE IF NOT EXISTS Picture
(
    student_id VARCHAR(20) PRIMARY KEY,
    picture    MEDIUMBLOB NOT NULL,
    CONSTRAINT fk_id_picture FOREIGN KEY (student_id) REFERENCES Student (student_id)
);

CREATE TABLE IF NOT EXISTS Teacher
(
    teacher_name    VARCHAR(200) PRIMARY KEY ,
    teacher_picture MEDIUMBLOB,
    teacher_contact VARCHAR(200) ,
    gender          VARCHAR (200) ,
    grades          ENUM (
        'G -6','G -7','G -8','G -9','G -10','G -11'
        )

);