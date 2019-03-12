from .models import Event
from rest_framework import serializers
from invitations.serializers import InvitationModelSerializer
from users.serializers import UserProfileSerializer
from locations.serializers import LocationModelSerializer


class EventCreateSerializer(serializers.ModelSerializer):
    event_admin = UserProfileSerializer(many=False, read_only=True)
    event_location = LocationModelSerializer(many=False, required=True,
                                             help_text="ID of the Location where the Event will be held")

    class Meta:
        model = Event
        fields = (
            "pk",
            "event_admin",
            "event_name",
            "event_date",
            "event_time",
            "event_duration",
            "event_location",
            "notes",
        )

    # Converts pk values of invitation and location into actual objs
    def to_representation(self, instance):
        response = super().to_representation(instance)
        response['event_location'] = LocationModelSerializer(instance.event_location).data
        return response


    def create(self, validated_data):
        location = validated_data.pop('event_location')
        event_date = validated_data.pop('event_date')
        # Todo: Check date, check location, and admin_id

        event_admin = None

        event = Event.objects.create(
            event_admin=event_admin,
            event_location=location,
            event_name=validated_data.pop('event_name'),
            event_date=event_date,
            event_time=validated_data.pop('event_time'),
            event_duration=validated_data.pop('event_duration'),
            permissions=[],
        )
        return event


class EventModelSerializer(serializers.ModelSerializer):
    admin_id = serializers.PrimaryKeyRelatedField(many=False, read_only=True)
    # Todo: Put it back once the invitations are done
    invitations = InvitationModelSerializer(many=True, read_only=True)

    # Todo: Write out fields and whatnot
    class Meta:
        model = Event
        fields = (
            "pk",
            "event_name",
            "event_date",
            "duration",
            "notes",
            "invitations",
            "admin_id"
        )

    def to_representation(self, instance):
        response = super().to_representation(instance)
        response['event_location'] = LocationModelSerializer(instance.event_location).data
        return response
