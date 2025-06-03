-- Create a new Supply record
INSERT INTO
        Supply_Record (
                Supply_Type_ID,
                Current_Quantity,
                Deleted,
                Added,
                Unit,
                SR_Date,
                Retrieved
        )
VALUES
        (?, ?, ?, ?, ?, ?, ?);

-- View all Supply Records for a specific date
SELECT
        st.Supply_Name,
        sr.Current_Quantity,
        sr.Deleted,
        sr.Added,
        sr.Unit,
        sr.SR_Date,
        sr.Retrieved
FROM
        Supply_Record sr
        JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
WHERE
        sr.SR_Date = ?;

-- View all Supply Records for a specific date and type (id)
-- View all Supply Records for a specific supply type and date
SELECT
        st.Supply_Name,
        sr.Current_Quantity,
        sr.Deleted,
        sr.Added,
        sr.Unit,
        sr.SR_Date,
        sr.Retrieved
FROM
        Supply_Record sr
        JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
WHERE
        -- for supply name
        -- sr.Supply_Name = ? 
        sr.Supply_Type_ID = ?
        AND sr.SR_Date = ?;

-- Update Retrieved column of a specific record
UPDATE Supply_Record
SET
        Retrieved = TRUE
WHERE
        Retrieved = FALSE
        AND Supply_Type_ID = ?;