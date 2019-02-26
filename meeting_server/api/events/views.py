from rest_framework import generics as drf_generics
from rest_framework.mixins import ListModelMixin
from rest_framework.parsers import FormParser, MultiPartParser
from rest_framework.viewsets import ModelViewSet

from . import serializers
from .models import Event


class EventDetailView(ModelViewSet):

    serializer_class = serializers.EventModelSerializer
    lookup_field = 'uuid'
    queryset = Event.objects.all()


class EventListCreateView(drf_generics.ListCreateAPIView, ListModelMixin):

    serializer_class = serializers.EventCreateSerializer
    # filter_fields = ('id', '')
    parser_classes = (FormParser, MultiPartParser)
    lookup_field = 'uuid'

    def get_queryset(self):

        queryset = Event.objects.all()
        name = self.request.query_params.get('name', None)
        if name is not None:
            queryset = queryset.filter(name=name)
        return queryset
        # return Event.objects.all()
