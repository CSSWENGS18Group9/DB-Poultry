-- Create a Flock Detail record
INSERT INTO
     Flock_Details (Flock_ID, FD_Date, Depleted_Count)
VALUES
     (?, ?, ?);

-- Go to Flock.sql for View query
-- Get Most Recent
SELECT
     fd.*
FROM
     Flock_Details fd
     JOIN Flock f ON fd.Flock_ID = f.Flock_ID
ORDER BY
     fd.FD_Date DESC
LIMIT
     1;