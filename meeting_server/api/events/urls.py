from django.conf.urls import url
from . import views
#
# urlpatterns = [
#     url(r'$', views.EventViewSet.as_view(), name="api-events"),
#     url(r'(?P<pk>.+)$', views.EventDetailView.as_view(), name="api-events-detail")
# ]
urlpatterns = [
    url(r'$', views.EventCreateView.as_view(), name='api-event-create')
]