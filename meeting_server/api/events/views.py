from django.http import Http404
from rest_framework.response import Response
from rest_framework import status
from rest_framework.viewsets import ModelViewSet
from rest_framework.filters import OrderingFilter
from rest_framework.views import APIView
from rest_framework import generics as drf_generics
from .models import Event
from .serializers import EventModelSerializer, EventCreateSerializer
from http import HTTPStatus
from rest_framework.filters import SearchFilter
from django_filters.rest_framework.filterset import FilterSet
from rest_framework.parsers import MultiPartParser, FormParser


class EventCreateView(drf_generics.CreateAPIView):
    serializer_class = EventCreateSerializer
    parser_classes = (MultiPartParser, FormParser)

    def post(self, request, *args, **kwargs):
        return super(EventCreateView, self).post(self, *args, **kwargs)

























# Todo: DON'T USE VIEWSETS - change this out at some point for a more standard approach
# class EventViewSet(ModelViewSet):
#
#     queryset = Event.objects.all()
#     serializer_class = EventModelSerializer
    # permission_classes = (permissions.IsAuthenticated,)
    # filter_backends = (SearchFilter,)
    # search_fields = ()

# class EventModelView(drf_generics.ListCreateAPIView):


# class EventDetailView(APIView):
#     # Helper method
#     def get_object(self, pk):
#         try:
#             return Event.objects.get(pk=pk)
#         except HTTPStatus.NOT_FOUND:
#             raise Http404
#
#     # GET - /event/{pk}
#     def get(self, request, pk, format=None):
#         event = self.get_object(pk)
#         serializer = EventModelSerializer(event)
#         return Response(serializer.data)
#
#     # PUT - /event/{pk}
#     def put(self, request, pk, format=None):
#         event = self.get_object(pk)
#         serializer = EventModelSerializer(event, data=request.DATA)
#         if serializer.is_valid():
#             serializer.save()
#             return Response(serializer.data)
#         return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
#
#     # DELETE - /event/{pk}
#     def delete(self, request, pk, format=None):
#         event = self.get_object(pk)
#         event.delete()
#         return Response(status=status.HTTP_204_NO_CONTENT)
