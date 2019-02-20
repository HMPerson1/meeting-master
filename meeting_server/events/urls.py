from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'$', views.EventListCreateView.as_view(), name="events"),
    url(r'/{uuid}', views.EventDetailView.as_view(), name="events-detail")
]