CREATE TABLE Coop (
    -- Primary Key
    Coop_ID SERIAL PRIMARY KEY,
    
    -- Table Columns
    Coop_Name TEXT,
    Capacity INTEGER
);

CREATE TABLE Poultry_Records (
    -- Primary Key
    Poultry_ID SERIAL PRIMARY KEY,

    -- Coop Foreign Key
    Coop_ID SERIAL,

    -- Table Columns
    Current_Count INTEGER,
    Depleted_Count INTEGER,
    Date TIMESTAMP,

    -- Table Constraints
    FOREIGN KEY (Coop_ID) REFERENCES Coop(Coop_ID)
);