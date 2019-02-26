from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'/(?P<pk>.+)$', views.EventDetailView.as_view(), name="api-event-detail"),
    url(r'$', views.EventListCreateView.as_view(), name="api-event")
]
