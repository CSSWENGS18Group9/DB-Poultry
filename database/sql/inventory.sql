-- ENUM for unit types
CREATE TYPE UnitType AS ENUM (
                                'weight', 
                                'volume', 
                                'length', 
                                'discrete'
                             );

-- Supply Type Table
CREATE TABLE Supply_Type (
    -- Supply_Type Primary Key
    -- Prefix of Supply_Type
    -- Ex. `ChkF_`, `Apog_`
    Supply_Type_Prefix VARCHAR(5) PRIMARY KEY,      

    -- The unit of measurement for that Supply_Type
    -- for weight: 
    --      grams        (g)
    -- for volume: 
    --      mililiters   (ml)
    -- for length: 
    --      centimeters  (cm)
    -- for discrete number of something (like cans, bottles, sacks):
    --      discrete
    Supply_Type_Unt UnitType
);

-- Supply Table
CREATE TABLE Supply (
    -- Structured Primary Key
    Supply_ID SERIAL PRIMARY KEY,
    Supply_Type_Prefix VARCHAR(5) 
                       NOT NULL 
                       REFERENCES Supply_Type(Supply_Type_Prefix),
    
    -- Table Columns
    Quantity NUMERIC(8,8),  -- arbitrary precision 
    -- Unit INTEGER         -- Removed this line since the Unit 
                            -- is already given by the Supply_Type
                            -- return if needed (@zrygan)
);

-- Delivery Table
CREATE TABLE Delivery (
    -- Primary Key
    Delivery_ID SERIAL PRIMARY KEY,

    -- Table Columns
    Date DATE
);

-- Delivery Detail Table
-- Contains the details/logistics of a delivery
CREATE TABLE Delivery_Detail (
    -- Supply Foreign Key
    Supply_ID INTEGER NOT NULL,

    -- Delivery Foreign Key
    Delivery_ID INTEGER NOT NULL,

    -- Table Columns
    -- FIXME:   @jazjimenez @OutForMilks is this necessary since
    --          it's already in Supply Table?
    Quantity NUMERIC(8,4),

    -- Table Constraints
    PRIMARY KEY (Supply_ID, Delivery_ID),
    FOREIGN KEY (Supply_ID) REFERENCES Supply(Supply_ID),
    FOREIGN KEY (Delivery_ID) REFERENCES Delivery(Delivery_ID)
);

-- (Supply) Usage Table
CREATE TABLE Usage (
    -- Primary Key
    Usage_ID SERIAL PRIMARY KEY,

    -- Supply Foreign Key
    Supply_ID INTEGER NOT NULL,

    -- Table Columns
    -- FIXME:   @jazjimenez @OutForMilks is this necessary since
    --          it's already in Supply Table?
    Quantity NUMERIC(8,4),
    Date DATE,

    -- Table Constraints
    FOREIGN KEY (Supply_ID) REFERENCES Supply(Supply_ID)
);
