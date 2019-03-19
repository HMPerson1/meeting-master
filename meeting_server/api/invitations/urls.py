from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'$', views.InvitationCreationView.as_view(), name='api-invitation-creation'),
    url(r'(?P<event_id>.+)$', views.InvitationEventView.as_view(), name='api-invitations-event-list'),
    url(r'(?P<user_id>.+)$', views.InvitationUserView.as_view(), name='api-invitations-user-list'),
    url(r'(?P<event_id>.+)/(?P<user_id>.+)$', views.InvitationDetailView.as_view(), name="api-invitations-detail")
]
