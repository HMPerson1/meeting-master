from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'(?P<pk>.+)$', views.LocationDetailView.as_view(), name='api-locations-detail'),
    url(r'$', views.LocationListView.as_view(), name='api-locations'),
]
