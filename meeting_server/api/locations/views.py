from django.http import Http404
from drf_yasg.utils import swagger_auto_schema
from rest_framework.response import Response
from rest_framework import status
from rest_framework.views import APIView
from rest_framework.generics import ListCreateAPIView
from .serializers import LocationModelSerializer, LocationListQuerySerializer
from .models import Location
from django.core.exceptions import ObjectDoesNotExist


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


class LocationDetailView(APIView):
    serializer_class = LocationModelSerializer

    def get_object(self, pk):
        try:
            return Location.objects.get(pk=pk)
        except ObjectDoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        location = self.get_object(pk=pk)
        serializer = LocationModelSerializer(location)
        return Response(serializer.data)

    def put(self, request, pk, format=None):
        location = self.get_object(pk=pk)
        serializer = LocationModelSerializer(location, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        location = self.get_object(pk=pk)
        location.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
