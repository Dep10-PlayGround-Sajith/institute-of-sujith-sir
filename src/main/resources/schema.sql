CREATE TABLE IF NOT EXISTS Payment(
    student_id VARCHAR(50) NOT NULL ,
    month ENUM(
        'JANUARY','FEBRUARY','MARCH','APRIL','MAY','JUNE','JULY','AUGUST','SEPTEMBER','OCTOBER','NOVEMBER','DECEMBER'
        ) NOT NULL ,
    last_payment DATETIME NOT NULL ,
    fee DECIMAL NOT NULL,
    class_name VARCHAR(200) NOT NULL,
    CONSTRAINT pk_payment PRIMARY KEY (student_id,class_name)

);

CREATE TABLE IF NOT EXISTS Class(
                                      student_id VARCHAR(50)  ,
                                      class_name VARCHAR(100) NOT NULL,
                                      class_fee DECIMAL(20) NOT NULL ,
                                      CONSTRAINT fk_student_class FOREIGN KEY (student_id) REFERENCES Student(student_id)

);

CREATE TABLE IF NOT EXISTS Admission(
                                    student_id VARCHAR(50) PRIMARY KEY ,
                                    admission DECIMAL(20) NOT NULL ,
                                    paid_date DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS Student(
                                    student_id VARCHAR(50) PRIMARY KEY ,
                                    student_name VARCHAR(100) NOT NULL
);