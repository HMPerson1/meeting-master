# Create your views here.
from django.contrib.auth.models import User
from rest_framework import viewsets, permissions
from rest_framework.filters import SearchFilter

from users.serializers import UserProfileSerializer


class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserProfileSerializer
    permission_classes = (permissions.IsAuthenticated,)
    filter_backends = (SearchFilter,)
    search_fields = ('username', 'first_name', 'last_name', 'email', 'userprofile__phone_number')
