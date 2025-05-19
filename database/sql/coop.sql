CREATE TABLE Coop (
    Coop_ID TEXT PRIMARY KEY,
    Name TEXT,
    Capacity INTEGER
);

CREATE TABLE Poultry_Records (
    Poultry_ID TEXT PRIMARY KEY,
    Coop_ID TEXT,
    Current_Count INTEGER,
    Depleted_Count INTEGER,
    Date TIMESTAMP,
    FOREIGN KEY (Coop_ID) REFERENCES Coop(Coop_ID)
);