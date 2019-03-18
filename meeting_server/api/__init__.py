import firebase_admin
from firebase_admin import credentials

firebase_default_app = firebase_admin.initialize_app(credentials.Certificate('private/meeting-master-2a1e6-firebase-adminsdk-eivgs-ea5e65b4bc.json'))
