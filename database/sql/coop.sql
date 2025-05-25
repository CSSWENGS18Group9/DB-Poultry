CREATE TABLE Flock (
    Flock_ID SERIAL PRIMARY KEY,

    Starting_Count INTEGER CHECK (Starting_Count > 0) NOT NULL,
    Starting_Date DATE UNIQUE NOT NULL
);

CREATE TABLE Flock_Details (
    Flock_Details_ID SERIAL PRIMARY KEY,
    Flock_ID INTEGER NOT NULL,

    FD_Date DATE UNIQUE NOT NULL,
    Depleted_Count INTEGER CHECK (Depleted_Count >= 0),

    FOREIGN KEY (Flock_ID) REFERENCES Flock(Flock_ID) ON DELETE CASCADE
);