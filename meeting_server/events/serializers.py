from drf_writable_nested import WritableNestedModelSerializer
from .models import Event
from rest_framework import serializers


class EventCreateSerializer(serializers.Serializer):
    # event_uuid = serializers.UUIDField(read_only=True)
    event_name = serializers.CharField(required=True, allow_null=False, max_length=256)
    event_date = serializers.DateField(allow_null=False, required=True, format='%d-%m-%Y')
    # event_duration = serializers.DurationField(read_only=True)
    file_upload = serializers.FileField(required=False)
    notes = serializers.CharField(required=False, max_length=2048)

    def create(self, validated_data):
        event = super(EventCreateSerializer, self).create(validated_data)
        event.save()
        return event


class EventModelSerializer(WritableNestedModelSerializer):

    # Todo: Write out fields and whatnot
    class Meta:
        model = Event
        fields = [
            "uuid",
            "name",
            "date",
            "duration",
            "attachment",
            "notes"
        ]

    def create(self, validated_data):
        event = super(EventModelSerializer, self).create(validated_data)
        event.persist()
        return event
