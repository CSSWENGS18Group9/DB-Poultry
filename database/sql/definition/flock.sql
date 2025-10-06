CREATE TABLE
    Flock (
        Flock_ID SERIAL PRIMARY KEY,
        Starting_Count INTEGER CHECK (Starting_Count > 0) NOT NULL,
        Starting_Date DATE UNIQUE NOT NULL,
        Target_Weight DECIMAL(5,2) CHECK (Target_Weight > 0) NOT NULL
    );