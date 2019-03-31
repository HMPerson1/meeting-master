from django.http import Http404
from rest_framework import serializers
from api.users.models import UserProfile
from api.events.models import Event
# from api.users.serializers import UserDetailsSerializer
from django.core.exceptions import ObjectDoesNotExist
import api.fcm as fcm


from .models import Invitation


class InvitationModelSerializer(serializers.ModelSerializer):
    user_id = serializers.PrimaryKeyRelatedField(many=False, queryset=UserProfile.objects.all())
    event_id = serializers.PrimaryKeyRelatedField(many=False, queryset=Event.objects.all())

    class Meta:
        model = Invitation
        fields = ('event_id', 'user_id', 'status', 'edit_permission')

    def create(self, validated_data):
        uid = validated_data.pop('user_id')
        eid = validated_data.pop('event_id')
        invite = Invitation.objects.create(user_id=uid, event_id=eid)
        fcm.notify_invite(invite.user_id, invite.event_id)
        return invite


class InvitationListEventSerializer(serializers.ModelSerializer):
    user_id = serializers.PrimaryKeyRelatedField(many=False, queryset=UserProfile.objects.all())

    class Meta:
        model = Invitation
        fields = ('user_id', 'status')


class InvitationStatusUpdateSerializer(serializers.ModelSerializer):
    user_id = serializers.PrimaryKeyRelatedField(many=False, queryset=UserProfile.objects.all())
    event_id = serializers.PrimaryKeyRelatedField(many=False, queryset=Event.objects.all())

    class Meta:
        model = Invitation
        fields = ('event_id', 'user_id', 'status')
