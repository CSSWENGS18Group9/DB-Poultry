-- the default supply types for the DBMS
INSERT INTO Supply_Type (Supply_Type_Prefix, Supply_Type_Unt)
VALUES ('ChkF_', 'weight'), -- Chicken Feed
       -- could be discrete (per sack)
       ('Apog_', 'volume'), -- Apog
       ('Adul_', 'volume'), -- Adulticide
       ('Strg_', 'length'), -- String
       ('Fuel_', 'volume'), -- Fuel
       ('ChkM_', 'volume'), -- Chicken Medicine
       ('Larv_', 'volume'), -- Larvicide
       ('FlyG_', 'volume'), -- Fly Glue
       ('Dist_', 'volume'); -- Disinfectant

