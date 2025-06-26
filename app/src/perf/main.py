from faker import Faker
import psycopg2
from dotenv import load_dotenv
import os
from generate_dummies.flocks import Dummy_Flocks
from generate_dummies.flock_details import Dummy_Flock_Details

load_dotenv()


class Database:
    def __init__(self):
        self.db_name = os.getenv("DATABASE_NAME")
        self.db_pass = os.getenv("DATABASE_PASS")
        self.db_port = os.getenv("DATABASE_PORT")
        self.db_host = "localhost"

        self.db_conn = psycopg2.connect(
            dbname=self.db_name,
            user=self.db_name,
            password=self.db_pass,
            host=self.db_host,
            port=self.db_port,
        )

        print("Connected to PostgreSQL")

    def get_cursor(self):
        return self.db_conn.cursor()

    def close(self):
        self.db_conn.close()


def reset_tables(conn):
    ddl_statements = """
    DROP TABLE IF EXISTS Supply_Record CASCADE;
    DROP TABLE IF EXISTS Supply_Type CASCADE;
    DROP TABLE IF EXISTS Flock_Details CASCADE;
    DROP TABLE IF EXISTS Flock CASCADE;

    CREATE TABLE Flock (
        Flock_ID SERIAL PRIMARY KEY,
        Starting_Count INTEGER CHECK (Starting_Count > 0) NOT NULL,
        Starting_Date DATE UNIQUE NOT NULL
    );

    CREATE TABLE Flock_Details (
        Flock_Details_ID SERIAL PRIMARY KEY,
        Flock_ID INTEGER NOT NULL,
        FD_Date DATE UNIQUE NOT NULL,
        Depleted_Count INTEGER CHECK (Depleted_Count >= 0),
        FOREIGN KEY (Flock_ID) REFERENCES Flock (Flock_ID) ON DELETE CASCADE
    );

    CREATE TABLE Supply_Type (
        Supply_Type_ID SERIAL PRIMARY KEY,
        Supply_Name TEXT UNIQUE NOT NULL CHECK (Supply_Name <> ''),
        Unit VARCHAR(12) NOT NULL CHECK (Unit <> '')
    );

    CREATE TABLE Supply_Record (
        Supply_ID SERIAL PRIMARY KEY,
        Supply_Type_ID INT NOT NULL,
        SR_Date DATE NOT NULL,
        Added NUMERIC(12, 4),
        Consumed NUMERIC(12, 4),
        Retrieved BOOLEAN,
        FOREIGN KEY (Supply_Type_ID) REFERENCES Supply_Type (Supply_Type_ID) ON DELETE CASCADE,
        UNIQUE (Supply_Type_ID, SR_Date)
    );
    """

    with conn.cursor() as cur:
        cur.execute(ddl_statements)
    conn.commit()

def plot_posteriori(title, posteriori):
    import matplotlib.pyplot as plt
    import numpy as np
    # Convert to numpy array for stats convenience
    data = np.array(posteriori)
    
    # Calculate descriptive statistics
    mean = np.mean(data)
    median = np.median(data)
    std = np.std(data)
    minimum = np.min(data)
    maximum = np.max(data)
    
    # Print descriptive stats
    print(f"{title} - Descriptive Statistics:")
    print(f"  Count: {len(data)}")
    print(f"  Mean: {mean:.6f} seconds")
    print(f"  Median: {median:.6f} seconds")
    print(f"  Std Dev: {std:.6f} seconds")
    print(f"  Min: {minimum:.6f} seconds")
    print(f"  Max: {maximum:.6f} seconds")
    print()
    
    # Plot the data
    plt.plot(data)
    plt.title(title)
    plt.xlabel("Insert Number")
    plt.ylabel("Execution Time (seconds)")
    plt.grid(True)
    plt.show()

if __name__ == "__main__":
    dum_count = 500
    database = Database()
    fake = Faker()

    reset_tables(database.db_conn)

    # Create the Dummy Insertion Objects
    dumb_flocks = Dummy_Flocks(dum_count, database)
    # FIXME: add the values here
    dumb_flock_details = Dummy_Flock_Details(dum_count, fake)

    # Insert the Dummy Data
    dumb_flocks_time = dumb_flocks.make_dummies(fake)

    database.close()

    # Plot the posteriori analysis
    plot_posteriori("Flocks", dumb_flocks_time)
