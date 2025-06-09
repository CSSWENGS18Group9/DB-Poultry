CREATE TABLE
    Supply_Type (
        Supply_Type_ID SERIAL PRIMARY KEY,
        Supply_Name TEXT,
        Unit VARCHAR(12) -- should be enough for any unit of measurement
    );