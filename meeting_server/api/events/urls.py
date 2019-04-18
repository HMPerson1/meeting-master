from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^current-user-active-event$', views.EventActive.as_view(), name='api-event-active'),
    url(r'new_event$', views.EventCreateView.as_view(), name='api-event-create'),
    url(r'^ical/(?P<ical_key>[0-9a-zA-Z]{24}).ical$', views.IcalView.as_view(), name='api-event-ical'),
    url(r'^file-attachment/(?P<pk>.+)$', views.EventFileUploadView.as_view(), name='api-event-attachments'),
    url(r'^(?P<pk>[^/.]+)$', views.EventDetailView.as_view(), name='api-event-detail'),
    url(r'^(?P<event_id>[^/.]+)/attendee-locations$', views.AttendeeLiveLocationsView.as_view(), name='api-event-livelocation'),
    url(r'^$', views.EventListView.as_view(), name='api-event-list'),
]
