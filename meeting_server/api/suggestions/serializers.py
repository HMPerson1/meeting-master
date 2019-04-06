from django.http import Http404
from rest_framework import serializers
from api.locations.models import Location
from api.users.models import UserProfile
from api.events.models import Event
from rest_framework.validators import UniqueTogetherValidator


from .models import Suggestion


class SuggestionModelSerializer(serializers.ModelSerializer):
    location_id = serializers.PrimaryKeyRelatedField(many=False, queryset=Location.objects.all())
    event_id = serializers.PrimaryKeyRelatedField(many=False, queryset=Event.objects.all())

    class Meta:
        model = Suggestion
        validators = [
            UniqueTogetherValidator(
                queryset=Suggestion.objects.all(),
                fields=('event_id', 'location_id'),
                message="This suggestion already exists!"
            )
        ]
        fields = ('event_id', 'location_id')

    def create(self, validated_data):
        lid = validated_data.pop('location_id')
        eid = validated_data.pop('event_id')

        suggestion = Suggestion.objects.create(
            location_id=lid,
            event_id=eid,
            created_by=self.context['request'].user.userprofile
        )
        return suggestion
