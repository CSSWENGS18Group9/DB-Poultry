-- Create a new Flock record
INSERT INTO
    Flock (Starting_Count, Starting_Date)
VALUES
    (?, ?);

-- View all Flock records and their Flock_Detail Table
SELECT
    f.flock_id,
    f.starting_count,
    f.starting_date,
    fd.flock_details_id,
    fd.fd_date,
    fd.depleted_count
FROM
    Flock f
    INNER JOIN Flock_Details fd ON f.flock_id = fd.flock_id;