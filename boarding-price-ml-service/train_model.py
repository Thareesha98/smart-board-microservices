import pandas as pd
from sklearn.linear_model import LinearRegression
from sklearn.model_selection import train_test_split
from sklearn.metrics import r2_score
import joblib

from dataset_generator import generate_dataset
from db_dataset_loader import prepare_real_dataset


print("Generating synthetic dataset...")
synthetic_df = generate_dataset(5000)

print("Loading real boarding dataset...")
try:
    real_df = prepare_real_dataset()
except Exception as e:
    print("Warning: Could not load real dataset:", e)
    real_df = pd.DataFrame()

print("Synthetic samples:", len(synthetic_df))
print("Real samples:", len(real_df))


if not real_df.empty:
    df = pd.concat([synthetic_df, real_df], ignore_index=True)
else:
    df = synthetic_df

print("Total training samples:", len(df))


df = df.fillna(0)

df = pd.get_dummies(df, columns=["town"])


X = df.drop(columns=["price"])
y = df["price"]


X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.2,
    random_state=42
)


print("Training Linear Regression model...")

model = LinearRegression()
model.fit(X_train, y_train)


predictions = model.predict(X_test)

score = r2_score(y_test, predictions)

print("Model R² Score:", round(score, 4))


joblib.dump(model, "price_model.pkl")
joblib.dump(X.columns.tolist(), "model_features.pkl")

print("Model saved successfully")
print("Feature count:", len(X.columns))