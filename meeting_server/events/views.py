from rest_framework import generics as drf_generics
from .models import Event
from . import serializers


class EventDetailView(drf_generics.RetrieveUpdateDestroyAPIView):

    serializer_class = serializers.EventModelSerializer
    lookup_field = 'uuid'
    queryset = Event.objects.all()


class EventListCreateView(drf_generics.ListCreateAPIView):

    serializer_class = serializers.EventModelSerializer
    # filter_fields = ('id', '')
    lookup_field = 'uuid'

    def get_queryset(self):

        queryset = Event.objects.all()
        name = self.request.query_params.get('name', None)
        if name is not None:
            queryset = queryset.filter(name=name)
        return queryset
        # return Event.objects.all()
