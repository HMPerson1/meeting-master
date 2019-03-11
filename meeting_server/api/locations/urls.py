from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'$', views.LocationListView.as_view(), name='api-locations'),
    url(r'$', views.LocationCreateView.as_view(), name='api-locations')
]
