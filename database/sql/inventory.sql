-- Prefixed for composite ID of inventory
-- this ENUM determines what the type of supply
-- this ENUM also ensures only the following supply are in the database
--
-- POTENTIAL ISSUE: 
--     this may be an issue if the client wishes to do other supplies
CREATE Type supply_prefix AS ENUM (
    'ChkF_',  -- Chicken Feed
    'Apog_',  -- Apog
    'Adul_',  -- Adulticide
    'Strg_',  -- String
    'Fuel_',  -- Fuel
    'ChkM_',  -- Chicken Medicine
    'Larv_',  -- Larvicide
    'FlyG_',  -- Fly Glue
    'Dist_',  -- Disinfectant
--  'Misc_',  -- Miscellaneous
)

-- Supply Table
CREATE TABLE Supply (
    -- Structured Primary Key
    Supply_Prefix supply_prefix NOT NULL,
    Supply_ID SERIAL PRIMARY KEY,
    
    -- Table Columns
    Quantity NUMERIC(8,4),                -- arbitrary precision 
    Unit INTEGER,                         -- this does not make sense
);

CREATE TABLE Delivery (
    -- Primary Key
    Delivery_ID SERIAL PRIMARY KEY,

    -- Table Columns
    Date TIMESTAMP
);

CREATE TABLE Delivery_Detail (
    -- Supply Foreign Key
    Supply_Prefix supply_prefix NOT NULL,
    Supply_ID SERIAL NOT NULL,

    -- Delivery Foreign Key
    Delivery_ID SERIAL NOT NULL,

    -- Table Columns
    -- FIXME:   @jazjimenez @OutForMilks is this necessary since
    --          it's already in Supply Table?
    Quantity NUMERIC(8,4),

    -- Table Constraints
    PRIMARY KEY (Supply_ID, Delivery_ID),
    FOREIGN KEY (Supply_ID) REFERENCES Supply(Supply_ID),
    FOREIGN KEY (Delivery_ID) REFERENCES Delivery(Delivery_ID)
);

CREATE TABLE Usage (
    -- Primary Key
    Usage_ID SERIAL PRIMARY KEY,

    -- Supply Foreign Key
    Supply_Prefix supply_prefix NOT NULL 
    Supply_ID SERIAL NOT NULL,

    -- Table Columns
    -- FIXME:   @jazjimenez @OutForMilks is this necessary since
    --          it's already in Supply Table?
    Quantity NUMERIC(8,4),
    Date TIMESTAMP,

    -- Table Constraints
    FOREIGN KEY (Supply_ID) REFERENCES Supply(Supply_ID)
);
