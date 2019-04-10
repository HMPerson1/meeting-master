from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'event-suggestions/(?P<event_id>.+)$', views.SuggestionEventView.as_view(), name='api-suggestions-by-event'),
    url(r'(?P<event_id>.+)/(?P<location_id>.+)$', views.SuggestionDetailView.as_view(), name="api-suggestion-detail"),
    url(r'$', views.SuggestionCreationView.as_view(), name='api-suggestion-creation'),
]
