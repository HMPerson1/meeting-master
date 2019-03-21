from django.http import Http404
from drf_yasg.utils import swagger_auto_schema
from rest_framework.response import Response
from rest_framework import status
from rest_framework.views import APIView
from rest_framework.generics import ListCreateAPIView, ListAPIView
from django.http import HttpResponseNotAllowed
from .serializers import LocationModelSerializer, LocationListQuerySerializer
from .models import Location


class LocationListView(ListCreateAPIView):
    serializer_class = LocationModelSerializer

    @swagger_auto_schema(
        query_serializer=LocationListQuerySerializer,
        responses={200: LocationModelSerializer(many=True)}
    )
    def get(self, *args, **kwargs):
        resp = super().get(*args, **kwargs)
        return resp

    def get_queryset(self):
        queryset = Location.objects.all()
        state = self.request.query_params.get('state', None)
        city = self.request.query_params.get('city', None)
        street = self.request.query_params.get('street_address', None)

        if state is not None:
            queryset.filter(state=state)
        if city is not None:
            queryset.filter(city=city)
        if street is not None:
            queryset.filter(street_address=street)
        return queryset


# class LocationCreateView(CreateAPIView):
#     serializer_class = LocationModelSerializer
#     queryset = Location.objects.all()
#
#     def create(self, request, *args, **kwargs):
#         state = self.request.query_params.get('state', None)
#         city = self.request.query_params.get('city', None)
#         street = self.request.query_params.get('street_address', None)
#
#         Location.objects.get()
#             serializer = LocationModelSerializer(data=request.data)
#             serializer.is_valid(raise_exception=True)
#             instance = self.perform_create(serializer=serializer)
#             result = LocationModelSerializer(instance=instance)
#             return Response(result.data)


class LocationDetailView(APIView):

    def get_object(self, pk):
        try:
            return Response.objects.get(pk=pk)
        except status.HTTP_404_NOT_FOUND:
            raise Http404

    def get(self, request, pk, format=None):
        location = self.get_object(pk=pk)
        serializer = LocationModelSerializer(location)
        return Response(serializer.data)

    def put(self, request, pk, format=None):
        location = self.get_object(pk=pk)
        serializer = LocationModelSerializer(location, data=request.DATA)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        location = self.get_object(pk=pk)
        location.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
