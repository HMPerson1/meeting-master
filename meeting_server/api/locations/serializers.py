from rest_framework import serializers
from rest_framework.validators import UniqueTogetherValidator

from .models import Location


class LocationListQuerySerializer(serializers.Serializer):
    state = serializers.CharField(help_text="this field is generated from a query_serializer", required=False)
    city = serializers.CharField(help_text="this one too!", required=False)
    street_address = serializers.CharField(help_text="and this one is fancy!", required=False)


class LocationModelSerializer(serializers.ModelSerializer):
    number_of_uses = serializers.IntegerField(read_only=True, default=0)

    class Meta:
        model = Location
        validators = [
            UniqueTogetherValidator(
                queryset=Location,
                fields=('street_address', 'city', 'state'),
                message="This location already exists"
            )
        ]
        fields = ('pk', 'street_address', 'city', 'state', 'number_of_uses')

