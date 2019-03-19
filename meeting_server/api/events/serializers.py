from django.http import Http404

from .models import Event
from rest_framework.response import Response

from rest_framework import status
from rest_framework import serializers
from api.users.serializers import UserProfileSerializer
from api.locations.serializers import LocationModelSerializer
from django.forms import FileField
from api.locations.models import Location


class EventModelSerializer(serializers.ModelSerializer):
    # event_admin = UserProfileSerializer(many=False, read_only=True)

    class Meta:
        model = Event
        fields = (
            "pk",
            "event_name",
            "event_date",
            "event_time",
            "event_duration",
            "event_location",
            "notes",
            "file_attachment"
        )

    # def to_representation(self, instance):
    #     response = super().to_representation(instance)
    #     response['event_location'] = LocationModelSerializer(instance.event_location).data
    #     response['event_admin'] = UserProfileSerializer(instance.event_admin).data
    #     return response


class EventListQuerySerializer(serializers.Serializer):
    event_name = serializers.CharField(required=False)
    event_date = serializers.CharField(required=False, help_text="YYYY-MM-DD")


class EventCreateSerializer(serializers.ModelSerializer):
    # event_admin = UserProfileSerializer(many=False, read_only=True)
    event_location = serializers.PrimaryKeyRelatedField(required=True, queryset=Location.objects.all(),
                                                  help_text="ID of the Location where the Event will be held")
    file_attachment = serializers.FileField(required=False, use_url=True)

    class Meta:
        model = Event
        fields = (
            "pk",
            "event_name",
            "event_date",
            "event_time",
            "event_duration",
            "event_location",
            "notes",
            "file_attachment"
        )

    # Converts pk values of invitation and location into actual objs
    def to_representation(self, instance):
        response = super().to_representation(instance)
        response['event_location'] = LocationModelSerializer(instance.event_location).data
        # response['event_admin'] = UserProfileSerializer(instance.event_admin).data
        return response

    def create(self, validated_data):
        location = validated_data.pop('event_location')
        event_date = validated_data.pop('event_date')
        # Todo: Check date, check location, and admin_id
        # event_admin=event_admin,
        event = Event.objects.create(
            event_name=validated_data.pop('event_name'),
            event_date=event_date,
            event_time=validated_data.pop('event_time'),
            event_location=location,
            event_duration=validated_data.pop('event_duration'),
            file_attachment=validated_data.pop('file_attachment'),
            notes=validated_data.pop('notes')
        )
        return event
