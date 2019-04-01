from django.http import Http404
from django.core.exceptions import ObjectDoesNotExist
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework import status
from rest_framework.views import APIView
from rest_framework import generics as drf_generics

from api.events.renderers import IcalRenderer
from .models import Event
from .serializers import EventModelSerializer, EventCreateSerializer, EventListQuerySerializer, EventIcalSerializer
from rest_framework.parsers import MultiPartParser, FormParser, FileUploadParser
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi


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


class EventDetailView(APIView):
    parser_classes = (MultiPartParser, FormParser, FileUploadParser)

    def get_object(self, pk):
        try:
            return Event.objects.get(pk=pk)
        except ObjectDoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        event = self.get_object(pk=pk)
        serializer = EventModelSerializer(event)
        return Response(serializer.data)

    @swagger_auto_schema(manual_parameters=[
        openapi.Parameter('event_name', openapi.IN_FORM, "Name of your Event", type=openapi.TYPE_STRING, required=True),
        openapi.Parameter('event_date', openapi.IN_FORM, "YYYY-MM-DD", type=openapi.TYPE_STRING, required=True),
        openapi.Parameter('event_time', openapi.IN_FORM, "HH:MM", type=openapi.TYPE_STRING, required=True),
        openapi.Parameter('event_duration', openapi.IN_FORM, "Optional duration field - HH:MM", type=openapi.TYPE_STRING),
        openapi.Parameter('event_location', openapi.IN_FORM, "ID of your Event's Location", type=openapi.TYPE_INTEGER, required=True),
        openapi.Parameter('notes', openapi.IN_FORM, "Miscellaneous notes about event", type=openapi.TYPE_STRING),
        openapi.Parameter('file_attachment', openapi.IN_FORM, "Optional file upload", type=openapi.TYPE_FILE)
        ],
        responses={
            201: openapi.Response('Event successfuly updated', EventCreateSerializer)
        }
    )
    def put(self, request, pk, format=None):
        event = self.get_object(pk=pk)
        serializer = EventCreateSerializer(event, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        event = self.get_object(pk=pk)
        event.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class IcalView(drf_generics.ListAPIView):
    serializer_class = EventIcalSerializer
    pagination_class = None
    renderer_classes = (IcalRenderer,)
    permission_classes = ()

    def get_queryset(self):
        return Event.objects.filter(invitation__user_id__ical_key=self.kwargs['ical_key'])
