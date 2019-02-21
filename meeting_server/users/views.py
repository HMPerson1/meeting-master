from rest_framework.mixins import ListModelMixin
from rest_framework.viewsets import ModelViewSet
from rest_framework import generics as drf_generics
from rest_framework.parsers import FormParser, MultiPartParser
from .models import User
from . import serializers


class UserDetailView(drf_generics.RetrieveAPIView):

    serializer_class = serializers.UserModelSerializer
    lookup_field = 'uuid'
    queryset = User.objects.all()


class UserListCreateView(drf_generics.ListCreateAPIView, ListModelMixin):

    serializer_class = serializers.UserCreateSerializer
    # filter_fields = ('id', '')
    parser_classes = (FormParser, MultiPartParser)
    lookup_field = 'uuid'

    def get_queryset(self):

        queryset = User.objects.all()
        name = self.request.query_params.get('name', None)
        if name is not None:
            queryset = queryset.filter(name=name)
        return queryset
        # return User.objects.all()
