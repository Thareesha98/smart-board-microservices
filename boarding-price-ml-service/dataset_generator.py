import numpy as np
import pandas as pd
import random


# Town scores based on demand near universities
TOWN_SCORES = {

    # Colombo region
    "Colombo": 10,
    "Bambalapitiya": 10,
    "Wellawatte": 7,
    "Dehiwala": 6,
    "Mount Lavinia": 9,

    # Moratuwa
    "Moratuwa": 8,
    "Katubedda": 7,
    "Panadura": 9,

    # SJP / NSBM
    "Nugegoda": 8,
    "Maharagama": 7,
    "Homagama": 9,

    # SLIIT
    "Malabe": 8,
    "Kaduwela": 7,
    "Battaramulla": 9,

    # Kelaniya
    "Kelaniya": 4,
    "Kiribathgoda": 4,
    "Wattala": 4,
    "Ja-Ela": 3,
    "Kadawatha": 3,

    # Peradeniya
    "Peradeniya": 3,
    "Kandy": 3,

    # Ruhuna
    "Matara": 3,
    "Weligama": 2,
    "Galle": 3,

    # Rajarata
    "Mihintale": 2,
    "Anuradhapura": 2,

    # Eastern
    "Batticaloa": 2,

    # South Eastern
    "Oluvil": 2,

    # Sabaragamuwa
    "Belihuloya": 2,
    "Ratnapura": 2,

    # Uva
    "Badulla": 2,

    # Wayamba
    "Kuliyapitiya": 2,
    "Kurunegala": 2,

    # Jaffna
    "Jaffna": 3,
    "Nallur": 3,

    # Other towns
    "Piliyandala": 3,
    "Negombo": 3
}


def generate_dataset(n=3000):

    towns = list(TOWN_SCORES.keys())
    data = []

    for _ in range(n):

        town = random.choice(towns)
        town_score = TOWN_SCORES[town]

        room_size = np.random.randint(80, 250)
        slots = np.random.randint(1, 6)
        amenities = np.random.randint(1, 6)
        max_occupants = np.random.randint(1, 8)

        distance_to_uni = round(np.random.uniform(0.1, 8), 2)

        base_price = 2000 + town_score * 6500

        price = (
            base_price
            + room_size * 10
            - slots * 800
            + amenities * 300
            + max_occupants * 700
            - distance_to_uni * 700
            + np.random.normal(0, 700)
        )

        price = max(price, 3500)

        data.append([
            town,
            town_score,
            room_size,
            slots,
            amenities,
            distance_to_uni,
            max_occupants,
            price
        ])

    df = pd.DataFrame(data, columns=[
        "town",
        "town_score",
        "room_size",
        "slots",
        "amenities",
        "distance_to_uni",
        "max_occupants",
        "price"
    ])

    return df