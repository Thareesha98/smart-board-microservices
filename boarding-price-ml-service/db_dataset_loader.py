import os
import psycopg2
import pandas as pd

from dataset_generator import TOWN_SCORES


def get_connection():
    return psycopg2.connect(
        host=os.getenv("DB_HOST"),
        database=os.getenv("DB_NAME"),
        user=os.getenv("DB_USER"),
        password=os.getenv("DB_PASSWORD"),
        port=os.getenv("DB_PORT")
    )


def load_real_dataset():

    conn = get_connection()

    query = """
    SELECT
        b.id,
        b.address,
        b.available_slots,
        b.max_occupants,
        b.price_per_month,
        b.distance_to_uni,
        b.size,
        COUNT(a.amenity) AS amenities
    FROM boardings b
    LEFT JOIN boarding_amenities a
        ON b.id = a.boarding_id
    WHERE b.status = 'APPROVED'
    GROUP BY
        b.id,
        b.address,
        b.available_slots,
        b.max_occupants,
        b.price_per_month,
        b.distance_to_uni,
        b.size
    """

    df = pd.read_sql(query, conn)

    conn.close()

    return df


def extract_town(address):

    if not address:
        return "Unknown"

    parts = address.split(",")

    return parts[-1].strip()


def prepare_real_dataset():

    df = load_real_dataset()

    df["town"] = df["address"].apply(extract_town)

    df.rename(columns={
        "available_slots": "slots",
        "price_per_month": "price",
        "size": "room_size"
    }, inplace=True)

    df["amenities"] = df["amenities"].fillna(0)
    df["distance_to_uni"] = df["distance_to_uni"].fillna(1.5)

    # add town score
    df["town_score"] = df["town"].map(TOWN_SCORES).fillna(2)

    df = df[[
        "town",
        "town_score",
        "room_size",
        "slots",
        "amenities",
        "distance_to_uni",
        "max_occupants",
        "price"
    ]]

    df = df.fillna(0)

    df = df[df["price"] > 0]
    df = df[df["room_size"] > 0]

    return df


if __name__ == "__main__":

    df = prepare_real_dataset()

    print("Real dataset loaded")
    print("Total records:", len(df))
    print(df.head())