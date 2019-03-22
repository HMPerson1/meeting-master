from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'(?P<event_id>.+)/(?P<user_id>.+)/update_status$', views.InvitationStatusChangeView.as_view(),
        name="api-invitations-status-change"),
    url(r'(?P<event_id>.+)/(?P<user_id>.+)$', views.InvitationDetailView.as_view(), name="api-invitations-detail"),
    url(r'event-invitations$', views.InvitationEventView.as_view(), name='api-invitations-by-event'),
    url(r'user-invitations$', views.InvitationUserView.as_view(), name='api-invitations-by-user'),
    url(r'$', views.InvitationCreationView.as_view(), name='api-invitation-creation'),
]
