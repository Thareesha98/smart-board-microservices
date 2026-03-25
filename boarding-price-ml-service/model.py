import joblib
import pandas as pd

model = joblib.load("price_model.pkl")
features = joblib.load("model_features.pkl")


def predict_price(data):

    df = pd.DataFrame([data])

    df = pd.get_dummies(df)

    df = df.reindex(columns=features, fill_value=0)

    prediction = model.predict(df)[0]

    return round(prediction, 2)