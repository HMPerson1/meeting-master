# Create your views here.
from django.contrib.auth.models import User
from rest_framework import parsers
from rest_framework import viewsets, permissions, generics as drf_generics
from rest_framework.filters import SearchFilter

from api.users.models import UserProfile
from api.users.serializers import UserProfileSerializer, FirebaseRegTokenSerializer, ProfilePictureSerializer


class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserProfileSerializer
    permission_classes = (permissions.IsAuthenticated,)
    filter_backends = (SearchFilter,)
    search_fields = ('username', 'first_name', 'last_name', 'email', 'userprofile__phone_number')


class SetFirebaseRegToken(drf_generics.UpdateAPIView):
    serializer_class = FirebaseRegTokenSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_object(self):
        return self.request.user.userprofile

    def get_queryset(self):
        return UserProfile.objects.none()


class ProfilePictureView(drf_generics.UpdateAPIView):
    serializer_class = ProfilePictureSerializer
    permission_classes = (permissions.IsAuthenticated,)
    parser_classes = (parsers.MultiPartParser, )

    def get_object(self):
        return self.request.user.userprofile

    def get_queryset(self):
        return UserProfile.objects.none()
