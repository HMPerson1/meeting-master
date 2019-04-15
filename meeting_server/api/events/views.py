from django.http import Http404
from django.core.exceptions import ObjectDoesNotExist
from rest_framework import exceptions
from rest_framework.response import Response
from rest_framework import status
from rest_framework.views import APIView
from rest_framework import generics as drf_generics

from api.events.renderers import IcalRenderer
from .models import Event
from .serializers import EventModelSerializer, EventCreateSerializer, EventListQuerySerializer, EventIcalSerializer, EventFileSerializer
from rest_framework.parsers import MultiPartParser, FormParser, FileUploadParser, JSONParser
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi


class MethodSerializerView(object):

    method_serializer_classes = None

    def get_serializer_class(self):
        assert self.method_serializer_classes is not None, (
            'Expected view %s should contain method_serializer_classes '
            'to get right serializer class.' %
            (self.__class__.__name__, )
        )
        for methods, serializer_cls in self.method_serializer_classes.items():
            if self.request.method in methods:
                return serializer_cls

        raise exceptions.MethodNotAllowed(self.request.method)


class EventCreateView(drf_generics.CreateAPIView):
    serializer_class = EventCreateSerializer


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


# class EventDetailView(APIView):
class EventDetailView(MethodSerializerView, drf_generics.RetrieveUpdateDestroyAPIView):

    method_serializer_classes = {
        ('GET', ): EventModelSerializer,
        ('PUT', ): EventCreateSerializer,
        ('DELETE', ): None
    }

    http_method_names = ['get', 'put', 'delete']


class EventFileUploadView(APIView):
    parser_classes = (MultiPartParser, FormParser, FileUploadParser)

    def get_object(self, pk):
        try:
            return Event.objects.get(pk=pk)
        except ObjectDoesNotExist:
            raise Http404

    @swagger_auto_schema(manual_parameters=[
        openapi.Parameter('file_attachment', openapi.IN_FORM, "Optional file upload",
                          type=openapi.TYPE_FILE, required=True)
    ],
        responses={
            201: openapi.Response('File Attachment Added to Event', EventCreateSerializer)
        }
    )
    def patch(self, request, pk, format=None):
        event = self.get_object(pk=pk)

        event.file_attachment = request.data['file_attachment']
        serializer = EventFileSerializer(event)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class IcalView(drf_generics.ListAPIView):
    serializer_class = EventIcalSerializer
    pagination_class = None
    renderer_classes = (IcalRenderer,)
    permission_classes = ()

    def get_queryset(self):
        return Event.objects.filter(invitation__user_id__ical_key=self.kwargs['ical_key'])
