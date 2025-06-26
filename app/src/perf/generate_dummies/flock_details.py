from .util import generate_unique_date


class Dummy_Flock_Details:
    def __init__(self, dum_count, fake):
        self.used_dates = set()
        self.dum_count = dum_count

    def make_dummies(self, fake):
        for _ in range(self.dum_count):
            pass
