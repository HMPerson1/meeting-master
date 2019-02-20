from drf_writable_nested import WritableNestedModelSerializer
from .models import Event


class EventModelSerializer(WritableNestedModelSerializer):

    # Todo: Write out fields and whatnot

    class Meta:
        model = Event
        fields = [
            "uuid",
            "name",
            "date",
            "duration",
            "attachments",
            "notes"
        ]

    def create(self, validated_data):
        event = super(EventModelSerializer, self).create(validated_data)
        event.persist()
        return event
