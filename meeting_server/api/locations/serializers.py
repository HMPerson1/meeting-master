from rest_framework import serializers

from .models import Location


# class LocationCreationSerializer(serializers.ModelSerializer):
#
#     class Meta:
#         model = Location
#         fields = ('pk', 'street_address', 'city', 'state')


class LocationModelSerializer(serializers.ModelSerializer):

    class Meta:
        model = Location
        fields = ('pk', 'street_address', 'city', 'state', 'number_of_uses')

