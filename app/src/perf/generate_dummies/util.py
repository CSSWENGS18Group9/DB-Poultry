def generate_unique_date(used_dates, fake, start_date, end_date):
    while True:
        date = fake.date_between(start_date=start_date, end_date=end_date)
        if date not in used_dates:
            used_dates.add(date)
            return date
