# README.md

This is the documentation for the PostgreSQL Database.

## Directory

- The Data Definition Language (DDL) Scripts (`sql/`):
	- `coop.sql`, defines the coop table
	- `inventory.sql`, defines the supply, delivery, usage, and helper tables
- The Data Manipulation Languages (DML) Scripts (`sql/`):
	- `basic_supply_types.sql`, defines the default supply types in the DBMS
- Dummy Data is in the `test/` directory

## Structured ID for Inventory
Primary Key becomes a composite key:
	Supply_Prefix
	Supply_ID

Supply_ID is a SERIAL (32-bit auto incrementing base-10).

Supply_Prefix is a 5 length TEXT which determines the type of supply:
	ChkF_: Chicken Feed
	Apog_: Apog
	Adul_: Adulticide
	Strg_: String
	Fuel_: Fuel
	ChkM_: Chicken Medicine
	Larv_: Larvicide
	FlyG_: Fly Glue
	Dist_: Disinfectant

Example keys are:
	ChkF_986345321
	Dist_32

This was done using:
```sql
CREATE TABLE Supply_Type (
    Supply_Type_Prefix VARCHAR(5) PRIMARY KEY,      
    Supply_Type_Unt UnitType
);

-- ...

CREATE TABLE Supply (
    Supply_ID SERIAL PRIMARY KEY,
    Supply_Type_Prefix VARCHAR(5) 
                       NOT NULL 
                       REFERENCES Supply_Type(Supply_Type_Prefix),
    Quantity NUMERIC(8,8),
);
```