from django.urls import path
from . import views

urlpatterns = [
    path("firebase_reg_token", views.SetFirebaseRegToken.as_view()),
    path("profile_picture", views.ProfilePictureView.as_view()),
]
