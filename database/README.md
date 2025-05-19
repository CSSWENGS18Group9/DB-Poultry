# README.md

This is the documentation for the PostgreSQL Database.

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

### Potential Issue

Since the Supply_Prefix is an enum, it will strictly only allow for 
the enumerated supply types to be included.

Potential solution, we may add Misc_ as a prefix. 