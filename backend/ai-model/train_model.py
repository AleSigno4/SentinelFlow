import pandas as pd
import joblib
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report
from sklearn.feature_extraction.text import TfidfVectorizer
from scipy.sparse import hstack

CSV_FILE = 'training_dataset.csv'
df = pd.read_csv(CSV_FILE)

category_mapping = {
    'Food': 0, 'Shopping': 1, 'Clothes': 2, 'Beauty': 3, 
    'Entertainment': 4, 'Subscriptions': 5, 'Utilities': 6, 
    'Travel': 7, 'Transport': 8, 'Cyber': 9, 'Insurance': 10, 'Health': 11
}

status_mapping = {'CONFIRMED': 0, 'REJECTED': 1}

df['CATEGORY'] = df['CATEGORY'].map(category_mapping)
df['STATUS'] = df['STATUS'].map(status_mapping)

df['TIMESTAMP_DT'] = pd.to_datetime(df['TIMESTAMP'], format='mixed')
df['HOUR'] = df['TIMESTAMP_DT'].dt.hour


df = df.sort_values(['USER_ID', 'TIMESTAMP_DT']).reset_index()

counts = df.groupby('USER_ID').rolling('3min', on='TIMESTAMP_DT')['AMOUNT'].count()
df['TX_COUNT_3MIN'] = counts.reset_index(0, drop=True).values

df['TIME_DIFF'] = df.groupby('USER_ID')['TIMESTAMP_DT'].diff().dt.total_seconds().fillna(0)

df = df.set_index('index')
df.index.name = None

tfidf = TfidfVectorizer(max_features=100, stop_words='english')
X_text = tfidf.fit_transform(df['DESCRIPTION'].fillna(''))

X_numeric = df[['AMOUNT', 'CATEGORY', 'HOUR', 'TX_COUNT_3MIN', 'TIME_DIFF']].values
y = df['STATUS'].values

X_combined = hstack([X_numeric, X_text])

X_train, X_test, y_train, y_test = train_test_split(X_combined, y, test_size=0.2, random_state=42, stratify=y)

randomForest = RandomForestClassifier(n_estimators=150, max_depth=12, random_state=42, class_weight='balanced')
randomForest.fit(X_train, y_train)

accuracy = randomForest.score(X_test, y_test)
y_pred = randomForest.predict(X_test)

print(f'Accuracy Modello: {accuracy:.2f}')
print("\n--- Report di Classificazione Completo ---")
print(classification_report(y_test, y_pred))


joblib.dump(randomForest, 'fraud_model.joblib')
joblib.dump(tfidf, 'tfidf_vectorizer.joblib')