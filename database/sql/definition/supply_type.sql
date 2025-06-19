CREATE TABLE
    Supply_Type (
        Supply_Type_ID SERIAL PRIMARY KEY,
        Supply_Name TEXT UNIQUE NOT NULL CHECK (Supply_Name <> ''),
        Unit VARCHAR(12) NOT NULL CHECK (Unit <> '') -- should be enough for any unit of measurement
    );