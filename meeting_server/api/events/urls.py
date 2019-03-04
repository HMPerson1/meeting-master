from django.conf.urls import url
from . import views

urlpatterns = [
    # url(r'$', , name="events"),
    url(r'(?P<pk>.+)$', views.EventDetailView.as_view(), name="events-detail")
]
