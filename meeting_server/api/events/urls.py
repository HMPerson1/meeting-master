from django.conf import settings
from django.conf.urls import url, static

from . import views

urlpatterns = [
    url(r'^$', views.EventListView.as_view(), name='api-event-list'),
    url(r'^ical/(?P<ical_key>[0-9a-zA-Z]{24}).ical$', views.IcalView.as_view(), name='api-event-ical'),
    url(r'new_event$', views.EventCreateView.as_view(), name='api-event-create'),
    url(r'(?P<id>.+)/attendees$', views.EventAttendeesView.as_view(), name='api-event-detail'),
    url(r'(?P<id>.+)$', views.EventDetailView.as_view(), name='api-event-detail'),
]

if settings.DEBUG:
  urlpatterns += static.static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
