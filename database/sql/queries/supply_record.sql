-- Create a new Supply record
INSERT INTO
  Supply_Record (
    Supply_Type_ID,
    SR_Date,
    Added,
    Consumed,
    Retrieved
  )
VALUES
  (?, ?, ?, ?, ?);

-- View all Supply Records for a specific date
SELECT
  sr.Supply_ID,
  sr.Supply_Type_ID,
  sr.SR_Date,
  st.Supply_Name,
  st.Unit,
  sr.Added,
  sr.Consumed,
  sr.Retrieved
FROM
  Supply_Record sr
  JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
WHERE
  sr.SR_Date = ?;

-- View all Supply Records for a specific supply type name
SELECT
  sr.Supply_ID,
  sr.Supply_Type_ID,
  sr.SR_Date,
  st.Supply_Name,
  st.Unit,
  sr.Added,
  sr.Consumed,
  sr.Retrieved
FROM
  Supply_Record sr
  JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
WHERE
  st.Supply_Name = ?;

-- View a SINGLE Supply Records for a specific supply name and date
SELECT
  sr.Supply_ID,
  sr.Supply_Type_ID,
  sr.SR_Date,
  st.Supply_Name,
  st.Unit,
  sr.Added,
  sr.Consumed,
  sr.Retrieved
FROM
  Supply_Record sr
  JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
WHERE
  st.Supply_Name = ?
  AND sr.SR_Date = ?;

-- Update Retrieved column of a specific record
UPDATE Supply_Record sr
SET
  Retrieved = TRUE
FROM
  Supply_Type st
WHERE
  sr.Supply_Type_ID = st.Supply_Type_ID
  AND sr.Retrieved = FALSE
  AND sr.SR_Date = ?
  AND st.Supply_Name = ?;