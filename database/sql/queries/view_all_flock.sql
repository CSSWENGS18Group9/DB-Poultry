-- COMPREHENSIVE VIEW
SELECT
    f.flock_id,
    f.starting_date,
    fd.fd_date,
    fd.current_count,
    fd.depleted_count
FROM
    Flock f
        INNER JOIN
    Flock_Details fd
    On
        f.flock_id = fd.flock_id;