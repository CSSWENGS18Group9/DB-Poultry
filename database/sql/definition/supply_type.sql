CREATE TABLE
    Supply_Type (
        Supply_Type_ID SERIAL PRIMARY KEY,
        Supply_Name VARCHAR(36) UNIQUE NOT NULL CHECK (Supply_Name <> ''), -- should be enough for any supply_name
        Unit VARCHAR(12) NOT NULL CHECK (Unit <> ''), -- should be enough for any unit of measurement
        Image_File_Path VARCHAR(255)
    );