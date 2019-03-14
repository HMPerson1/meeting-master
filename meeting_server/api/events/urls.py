from django.conf import settings
from django.conf.urls import url, static

from . import views

urlpatterns = [
    url(r'^$', views.EventListView.as_view(), name='api-event-list'),
    url(r'new_event$', views.EventCreateView.as_view(), name='api-event-create'),
]

if settings.DEBUG:
  urlpatterns += static.static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
