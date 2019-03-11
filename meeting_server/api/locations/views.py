import coreapi
from rest_framework.response import Response
from rest_framework.schemas import AutoSchema
from rest_framework import status
from rest_framework.generics import CreateAPIView, ListAPIView
from django.http import HttpResponseNotAllowed
from .serializers import LocationModelSerializer
from .models import Location


class LocationListView(ListAPIView):
    serializer_class = LocationModelSerializer
    queryset = Location.objects.all()

    schema = AutoSchema(
        manual_fields=[
            coreapi.Field("state", required=False, location='query',
                          description='Optional parameter to view locations by state'),
            coreapi.Field("city", required=False, location='query',
                          description='Optional parameter to view locations by city'),
            coreapi.Field("street_address", required=False, location='query',
                          description='Optional parameter to view locations by street address')
        ]
    )

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


class LocationCreateView(CreateAPIView):
    serializer_class = LocationModelSerializer
    queryset = Location.objects.all()

    def create(self, request, *args, **kwargs):
        state = self.request.query_params.get('state', None)
        city = self.request.query_params.get('city', None)
        street = self.request.query_params.get('street_address', None)

        try:
            Response.objects.get(state=state, city=city, street_address=street)
            raise HttpResponseNotAllowed
        except status.HTTP_404_NOT_FOUND:
            serializer = LocationModelSerializer(data=request.data)
            serializer.is_valid(raise_exception=True)
            instance = self.perform_create(serializer=serializer)
            result = LocationModelSerializer(instance=instance)
            return Response(result.data)
