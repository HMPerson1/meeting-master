from copy import copy

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
        # validators = [
        #     UniqueTogetherValidator(
        #         queryset=Location.objects.all(),
        #         fields=('street_address', 'city', 'state'),
        #         message="This location already exists"
        #     )
        # ]
        fields = ('pk', 'street_address', 'city', 'state', 'number_of_uses')

    def run_validators(self, value):
        for validator in copy(self.validators):
            if isinstance(validator, UniqueTogetherValidator):
                self.validators.remove(validator)
            super(LocationModelSerializer, self).run_validators(value)

    def create(self, validated_data):
        location, created = Location.objects.get_or_create(
            street_address=validated_data['street_address'],
            city=validated_data['city'],
            state=validated_data['state'],
            defaults={
                'number_of_uses': validated_data['number_of_uses']
            }
        )
        return location
