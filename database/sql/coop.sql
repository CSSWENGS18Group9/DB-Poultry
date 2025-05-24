CREATE TABLE Flock (
    -- Primary Key
    Flock_ID SERIAL PRIMARY KEY,

    -- Table Columns
    Starting_Date UNIQUE DATE
);

CREATE TABLE Flock_Details (
    -- Primary Key
    Flock_Details_ID SERIAL PRIMARY KEY,

    -- Table Columns
    FD_Date DATE,
    Current_Count INTEGER,
    Depleted_Count INTEGER,

    -- Table Constraints
    FOREIGN KEY (Flock_ID) REFERENCES Flock(Flock_ID)
);
