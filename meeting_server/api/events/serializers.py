import datetime

from django.http import Http404

from .models import Event
from rest_framework.response import Response

from rest_framework import status
from rest_framework import serializers
from api.users.serializers import UserProfileSerializer
from api.locations.serializers import LocationModelSerializer
from django.forms import FileField
from api.users.models import UserProfile
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

    def update(self, instance, validated_data):
        ret = super().update(instance, validated_data)
        fcm.notify_edit(ret)
        return ret

    def to_representation(self, instance):
        response = super().to_representation(instance)
        response['event_location'] = LocationModelSerializer(instance.event_location).data
        # response['event_admin'] = UserProfileSerializer(instance.event_admin).data
        return response

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
    file_attachment = serializers.FileField(required=False, use_url=True, allow_empty_file=True, allow_null=True)

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
            event_admin=self.context['request'].user.userprofile,
            event_name=validated_data.pop('event_name'),
            event_date=event_date,
            event_time=validated_data.pop('event_time'),
            event_location=location,
            event_duration=validated_data.pop('event_duration'),
            file_attachment=validated_data.pop('file_attachment'),
            notes=validated_data.pop('notes')
        )
        return event


class EventPermissionSerializer(serializers.ModelSerializer):
    # permissions = serializers.PrimaryKeyRelatedField(many=True, read_only=False, queryset=UserProfile.objects.all())

    class Meta:
        model = Event
        fields = ('pk', 'event_admin')  # FIXME: probably broken


# only for use with IcalRenderer
class EventIcalSerializer(serializers.Serializer):
    file_attachment = serializers.FileField()

    def to_representation(self, instance: Event):
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
            'file_attachment': self.fields['file_attachment'].to_representation(instance.file_attachment),
            'attendees': [{
                'email': att.user_id.django_user.email,
                'full_name': att.user_id.django_user.get_full_name(),
                'status': att.status,
            } for att in instance.invitation.all()],
        }
