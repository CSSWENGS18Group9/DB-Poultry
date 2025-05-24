CREATE TABLE Flock (
    Flock_ID SERIAL PRIMARY KEY,

    Starting_Count INTEGER CHECK (Starting_Count > 0),
    Starting_Date UNIQUE DATE
);

CREATE TABLE Flock_Details (
    Flock_Details_ID SERIAL PRIMARY KEY,

    FD_Date UNIQUE DATE,
    Depleted_Count INTEGER CHECK (Depleted_Count >= 0),

    FOREIGN KEY (Flock_ID) REFERENCES Flock(Flock_ID)
);