from rest_framework import serializers

from .models import Location


class LocationListQuerySerializer(serializers.Serializer):
    state = serializers.CharField(help_text="this field is generated from a query_serializer", required=False)
    city = serializers.CharField(help_text="this one too!", required=False)
    street_address = serializers.CharField(help_text="and this one is fancy!", required=False)


class LocationModelSerializer(serializers.ModelSerializer):

    class Meta:
        model = Location
        fields = ('pk', 'street_address', 'city', 'state', 'number_of_uses')

