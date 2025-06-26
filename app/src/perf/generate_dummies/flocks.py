import random
from .util import generate_unique_date
import time


class Dummy_Flocks:
    def __init__(self, dum_count, database):
        self.used_dates = set()
        self.dum_count = dum_count
        self.database = database

    def make_dummies(self, fake):
        posteriori = []
        cursor = self.database.get_cursor()
        for _ in range(self.dum_count):
            starting_count = random.randint(0, 10_000)
            starting_date = generate_unique_date(
                self.used_dates, fake, "-720d", "today"
            )

            ts = time.perf_counter()

            cursor.execute(
                """
                INSERT INTO Flock (Starting_Count, Starting_Date)
                VALUES (%s, %s);
            """,
                (starting_count, starting_date),
            )

            te = time.perf_counter()
            posteriori.append(te - ts)

        self.database.db_conn.commit()
        cursor.close()

        return posteriori
