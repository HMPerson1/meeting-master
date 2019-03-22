from rest_framework import serializers
from rest_framework.validators import UniqueTogetherValidator

from .models import Location


class LocationListQuerySerializer(serializers.Serializer):
    state = serializers.CharField(help_text="Optional Filtering Parameter", required=False)
    city = serializers.CharField(help_text="Optional Filtering Parameter", required=False)
    street_address = serializers.CharField(help_text="Optional Filtering Parameter", required=False)


class LocationModelSerializer(serializers.ModelSerializer):
    number_of_uses = serializers.IntegerField(read_only=True, default=0)

    class Meta:
        model = Location
        validators = [
            UniqueTogetherValidator(
                queryset=Location.objects.all(),
                fields=('street_address', 'city', 'state'),
                message="This location already exists"
            )
        ]
        fields = ('pk', 'street_address', 'city', 'state', 'number_of_uses')

