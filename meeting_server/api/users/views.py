# Create your views here.
from django.contrib.auth.models import User
from rest_framework import parsers
from rest_framework import viewsets, permissions, generics as drf_generics
from rest_framework.filters import SearchFilter

from api.users import serializers
from api.users.models import UserProfile, LiveLocation


class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = User.objects.all()
    serializer_class = serializers.UserProfileSerializer
    permission_classes = (permissions.IsAuthenticated,)
    filter_backends = (SearchFilter,)
    search_fields = ('username', 'first_name', 'last_name', 'email', 'userprofile__phone_number')


class SetFirebaseRegToken(drf_generics.UpdateAPIView):
    serializer_class = serializers.FirebaseRegTokenSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_object(self):
        return self.request.user.userprofile

    def get_queryset(self):
        return UserProfile.objects.none()


class ProfilePictureView(drf_generics.UpdateAPIView):
    serializer_class = serializers.ProfilePictureSerializer
    permission_classes = (permissions.IsAuthenticated,)
    parser_classes = (parsers.MultiPartParser,)

    def get_object(self):
        return self.request.user.userprofile

    def get_queryset(self):
        return UserProfile.objects.none()


class UserIcalUrlView(drf_generics.RetrieveAPIView):
    serializer_class = serializers.UserIcalUrlSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_object(self):
        return self.request.user.userprofile

    def get_queryset(self):
        return UserProfile.objects.none()


class SetLiveLocation(drf_generics.UpdateAPIView):
    serializer_class = serializers.LiveLocationPutSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_object(self):
        userprofile = self.request.user.userprofile
        if hasattr(userprofile, 'livelocation'):
            return userprofile.livelocation
        else:
            return LiveLocation(user=userprofile)

    def get_queryset(self):
        return LiveLocation.objects.none()
