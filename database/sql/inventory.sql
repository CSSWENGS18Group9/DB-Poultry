CREATE TABLE Supply (
    Supply_ID TEXT PRIMARY KEY,
    Quantity INTEGER,
    Unit INTEGER,
    Type INTEGER
);

CREATE TABLE Delivery (
    Delivery_ID TEXT PRIMARY KEY,
    Date TIMESTAMP
);

CREATE TABLE Delivery_Detail (
    Supply_ID TEXT,
    Delivery_ID TEXT,
    Quantity INTEGER,
    PRIMARY KEY (Supply_ID, Delivery_ID),
    FOREIGN KEY (Supply_ID) REFERENCES Supply(Supply_ID),
    FOREIGN KEY (Delivery_ID) REFERENCES Delivery(Delivery_ID)
);

CREATE TABLE Usage (
    Usage_ID TEXT PRIMARY KEY,
    Supply_ID TEXT,
    Quantity INTEGER,
    Date TIMESTAMP,
    FOREIGN KEY (Supply_ID) REFERENCES Supply(Supply_ID)
);
