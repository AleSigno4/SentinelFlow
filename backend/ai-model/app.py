import logging

import joblib
import scipy.sparse as sp
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

logger = logging.getLogger(__name__)

ai_model = joblib.load("fraud_model.joblib")
tfidf_vectorizer = joblib.load("tfidf_vectorizer.joblib")

app = FastAPI(title="SentinelFlow AI Service", version="1.0")

class FraudDetectionInput(BaseModel):
    amount: float
    category: int
    description: str
    hour: int
    tx_count_3min: int
    time_diff: float

@app.post("/predict")
def predict_fraud(data: FraudDetectionInput): 
    try:
        text_features = tfidf_vectorizer.transform([data.description])

        numeric_features = [[
            data.amount,
            data.category,
            data.hour,
            data.tx_count_3min,
            data.time_diff
        ]]

        final_features = sp.hstack([numeric_features, text_features])

        prediction = int(ai_model.predict(final_features)[0])

        return {
            "prediction": prediction,
            "is_fraud": prediction == 1
        }

    except Exception as e:
        logger.error("Prediction failed for input %s: %s", data.model_dump(), e)
        raise HTTPException(status_code=500, detail="Prediction failed due to an internal error")