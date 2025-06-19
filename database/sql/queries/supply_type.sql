-- Create a new Supply Type
INSERT INTO
    Supply_Type (Supply_Name, Unit)
VALUES
    (?, ?);

-- View all Supply Types
SELECT Supply_Type_ID, Supply_Name, Unit FROM Supply_Type;