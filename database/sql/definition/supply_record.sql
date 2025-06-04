CREATE TABLE
    Supply_Record (
        Supply_ID SERIAL PRIMARY KEY,
        Supply_Type_ID INT NOT NULL,
        Current_Quantity NUMERIC(9, 4),
        Deleted NUMERIC(9, 4),
        Added NUMERIC(9, 4),
        Unit TEXT,
        SR_Date DATE,
        Retrieved BOOLEAN,
        FOREIGN KEY (Supply_Type_ID) REFERENCES Flock (Supply_ID) ON DELETE CASCADE
    );