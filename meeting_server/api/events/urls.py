from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'(?P<id>.+)/attendees$', views.EventAttendeesView.as_view(), name='api-event-detail'),
    url(r'(?P<id>.+)$', views.EventDetailView.as_view(), name='api-event-detail'),
    url(r'new_event$', views.EventCreateView.as_view(), name='api-event-create'),
    url(r'^ical/(?P<ical_key>[0-9a-zA-Z]{24}).ical$', views.IcalView.as_view(), name='api-event-ical'),
    url(r'^$', views.EventListView.as_view(), name='api-event-list'),
]
