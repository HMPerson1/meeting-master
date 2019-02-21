from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'$', views.UserListCreateView.as_view(), name="users"),
    url(r'(?P<uuid>.+)$', views.UserDetailView.as_view(), name="users-detail")
]
