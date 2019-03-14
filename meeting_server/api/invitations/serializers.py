from rest_framework import serializers
from users.models import UserProfile
from events.models import Event
# from api.users.serializers import UserDetailsSerializer


from .models import Invitation


class InvitationModelSerializer(serializers.ModelSerializer):
    user_id = serializers.PrimaryKeyRelatedField(many=False, queryset=UserProfile.objects.all())
    event_id = serializers.PrimaryKeyRelatedField(many=False, queryset=Event.objects.all())

    class Meta:
        model = Invitation
        fields = ('user_id', 'event_id', 'status')

    def create(self, validated_data):
        uid = validated_data.pop['user_id']
        eid = validated_data.pop['event_id']
        invite = Invitation.objects.create(user_id=uid, event_id=eid, status=0)
        return invite


class InvitationListEventSerializer(serializers.ModelSerializer):
    user_id = serializers.PrimaryKeyRelatedField(many=False, queryset=UserProfile.objects.all())

    class Meta:
        model = Invitation
        fields = ('user_id', 'status')


class InvitationStatusUpdateSerializer(serializers.ModelSerializer):
    user_id = serializers.PrimaryKeyRelatedField(many=False, queryset=UserProfile.objects.all())
    event_id = serializers.PrimaryKeyRelatedField(many=False, queryset=Event.objects.all())
    status = serializers.ChoiceField(required=True, choices=[(1, "PENDING"), (2, "ACCEPTED"), (3, "DECLINED")])

    class Meta:
        model = Invitation
        fields = ('user_id', 'event_id', 'status')
