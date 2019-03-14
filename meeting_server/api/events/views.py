import coreapi
from rest_framework.response import Response
from rest_framework import status
from rest_framework.viewsets import ModelViewSet
from rest_framework.views import APIView
from rest_framework import generics as drf_generics
from .models import Event
from .serializers import EventModelSerializer, EventCreateSerializer, EventListQuerySerializer
from rest_framework.filters import SearchFilter
from django_filters.rest_framework.filterset import FilterSet
from rest_framework.parsers import MultiPartParser, FormParser, FileUploadParser
from rest_framework.schemas import AutoSchema
from drf_yasg.utils import swagger_auto_schema


class EventCreateView(drf_generics.CreateAPIView):
    serializer_class = EventCreateSerializer
    parser_classes = (MultiPartParser, FormParser, FileUploadParser)


class EventListView(drf_generics.ListAPIView):
    serializer_class = EventModelSerializer
    queryset = Event.objects.all()

    @swagger_auto_schema(
        query_serializer=EventListQuerySerializer,
        responses={200: EventModelSerializer(many=True)}
    )
    def get(self, *args, **kwargs):
        resp = super().get(*args, **kwargs)
        return resp

    def get_queryset(self):
        queryset = Event.objects.all()
        event_date = self.request.query_params.get('event_date', None)
        event_name = self.request.query_params.get('event_name', None)

        if event_name is not None:
            queryset.filter(event_name=event_name)
        if event_date is not None:
            queryset.filter(event_date=event_date)
        return queryset
