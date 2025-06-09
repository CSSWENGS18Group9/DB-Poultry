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
        sr.Supply_Type_ID
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

-- View all Supply Records for a specific date and type (id)
-- View all Supply Records for a specific supply type and date
SELECT
        sr.Supply_ID,
        sr.Supply_Type_ID
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
        AND sr.SR_Date = ?;