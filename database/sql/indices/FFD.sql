-- Indices for Flock and Flock_Details
------
-- LET
--  F:      Flock
--  FD:     Flock_Details
--  SR:     Supply_Record
--  ST:     Supply_Type
--  a:      Attribute 2-tuple
--  RID:    Record Identifier
-- Defn:        Index [A] (R) = {a, RID}
-- SQL naming:  idx_<R>_R_<A1>_<A2>_..._<An>
------
-- Index [F.Starting_Date] (F) = {a, RID}
CREATE INDEX idx_flock_R_starting_date ON Flock (Starting_Date);

-- Index [FD] (FD.Flock_ID, FD.FD_Date) = {a, RID}
CREATE INDEX idx_flock_details_R_flockid_fddate ON Flock_Details (Flock_ID, FD_Date DESC);

-- Index [FD (FD.Flock_ID) = {a, RID}
CREATE INDEX idx_flock_R_details_flockid ON Flock_Details (Flock_ID);