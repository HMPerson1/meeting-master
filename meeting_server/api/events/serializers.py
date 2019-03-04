from drf_writable_nested import WritableNestedModelSerializer
from .models import Event
from rest_framework import serializers
from invitations.serializers import InvitationModelSerializer


class EventModelSerializer(serializers.ModelSerializer):
    # admin_id = serializers.PrimaryKeyRelatedField(many=False, read_only=True)
    # Todo: Put it back once the invitations are done
    invitations = InvitationModelSerializer(many=True, read_only=True)

    # Todo: Write out fields and whatnot
    class Meta:
        model = Event
        fields = [
            "uuid",
            "event_name",
            "date",
            "duration",
            "file_attachment",
            "notes",
            "invitations"
        ]
    #
    # def create(self, validated_data):
    #     event = super(EventModelSerializer, self).create(validated_data)
    #     event.persist()
    #     return event
