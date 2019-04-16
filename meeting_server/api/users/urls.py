from django.urls import path

from . import views

urlpatterns = [
    path("firebase_reg_token", views.SetFirebaseRegToken.as_view()),
    path("profile_picture", views.ProfilePictureView.as_view()),
    path("share_location", views.UserMapView.as_view()),
    path("ical_url", views.UserIcalUrlView.as_view()),
]
