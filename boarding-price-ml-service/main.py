from fastapi import FastAPI
from pydantic import BaseModel
from model import predict_price

app = FastAPI()


class PredictionRequest(BaseModel):
    town: str
    room_size: int
    slots: int
    amenities: int
    distance_to_uni: float
    max_occupants: int


@app.post("/predict-price")
def predict(req: PredictionRequest):

    data = {
        "town": req.town,
        "room_size": req.room_size,
        "slots": req.slots,
        "amenities": req.amenities,
        "distance_to_uni": req.distance_to_uni,
        "max_occupants": req.max_occupants
    }

    price = predict_price(data)

    return {
        "estimated_price": price,
        "min_price": round(price * 0.9, 2),
        "max_price": round(price * 1.1, 2)
    }