import datetime
from typing import Any

from django.db.models import Model
from django.http import Http404

from .models import Event, ActiveEvent
from rest_framework.response import Response

from rest_framework import status
from rest_framework import serializers
from api.users.serializers import UserProfileSerializer
from api.locations.serializers import LocationModelSerializer
from django.forms import FileField
from api.users.models import UserProfile, LiveLocation
from api.locations.models import Location
import api.fcm as fcm


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

    def to_representation(self, instance):
        response = super().to_representation(instance)
        response['event_location'] = LocationModelSerializer(instance.event_location).data
        # response['event_admin'] = UserProfileSerializer(instance.event_admin).data
        response['current_overall_state'] = instance.current_overall_state().value
        return response


class EventListQuerySerializer(serializers.Serializer):
    event_name = serializers.CharField(required=False)
    event_date = serializers.CharField(required=False, help_text="YYYY-MM-DD")


class EventCreateSerializer(serializers.ModelSerializer):
    # event_admin = UserProfileSerializer(many=False, read_only=True)
    event_location = serializers.PrimaryKeyRelatedField(required=True, queryset=Location.objects.all(),
                                                  help_text="ID of the Location where the Event will be held")
    # file_attachment = serializers.FileField(required=False, use_url=True, allow_empty_file=True, allow_null=True)

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
        )

    def update(self, instance: Model, validated_data: Any) -> Any:
        ret = super().update(instance, validated_data)
        fcm.notify_edit(ret)
        return ret

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
        event_admin = self.context['request'].user.userprofile
        event = Event.objects.create(
            event_admin=event_admin,
            event_name=validated_data.pop('event_name'),
            event_date=event_date,
            event_time=validated_data.pop('event_time'),
            event_location=location,
            event_duration=validated_data.pop('event_duration'),
            file_attachment=None,
            notes=validated_data.pop('notes', None)
        )
        from api.invitations.models import Invitation
        Invitation.objects.create(event_id=event, user_id=event_admin, status=Invitation.ACCEPTED)
        return event


class EventPermissionSerializer(serializers.ModelSerializer):
    # permissions = serializers.PrimaryKeyRelatedField(many=True, read_only=False, queryset=UserProfile.objects.all())

    class Meta:
        model = Event
        fields = ('pk', 'event_admin')  # FIXME: probably broken


class ActiveEventSerializer(serializers.ModelSerializer):
    class Meta:
        model = ActiveEvent
        fields = ('event', 'state')


# only for use with IcalRenderer
class EventIcalSerializer(serializers.Serializer):
    def to_representation(self, instance: Event):
        file_attachment_field = serializers.FileField()
        file_attachment_field.bind('file_attachment', self)
        return {
            'pk': instance.pk,
            'event_name': instance.event_name,
            'event_date': instance.event_date,
            'event_time': instance.event_time,
            'event_duration': datetime.timedelta(
                hours=instance.event_duration.hour,
                minutes=instance.event_duration.minute,
                seconds=instance.event_duration.second,
                microseconds=instance.event_duration.microsecond,
            ),
            'event_location': {
                'street_address': instance.event_location.street_address,
                'city': instance.event_location.city,
                'state': instance.event_location.state,
            },
            'notes': instance.notes,
            'file_attachment': file_attachment_field.to_representation(instance.file_attachment),
            'attendees': [{
                'email': att.user_id.django_user.email,
                'full_name': att.user_id.django_user.get_full_name(),
                'status': att.status,
            } for att in instance.invitation_set.all()],
        }


class EventFileSerializer(serializers.Serializer):

    file_attachment = serializers.FileField(required=True, use_url=True, allow_empty_file=True)

    class Meta:
        model = Event
        fields = ('file_attachment',)


class AttendeeLiveLocationsSerializer(serializers.ModelSerializer):
    user_full_name = serializers.CharField(source='user.django_user.get_full_name')

    class Meta:
        model = LiveLocation
        fields = ('user', 'user_full_name', 'lon', 'lat')
