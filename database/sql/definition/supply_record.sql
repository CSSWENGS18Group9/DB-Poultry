CREATE TABLE Supply_Record
(
    Supply_ID      SERIAL PRIMARY KEY,
    Supply_Type_ID INT  NOT NULL,
    SR_Date        DATE NOT NULL,
    Added          NUMERIC(12, 4),
    Consumed       NUMERIC(12, 4),
    Current_Count   NUMERIC(12, 4),
    Retrieved      BOOLEAN,
    FOREIGN KEY (Supply_Type_ID) REFERENCES Supply_Type (Supply_Type_ID) ON DELETE CASCADE,
    UNIQUE (Supply_Type_ID, SR_Date)
);