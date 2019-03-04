from rest_framework import serializers

# from api.users.serializers import UserDetailsSerializer


from .models import Invitation


class InvitationModelSerializer(serializers.ModelSerializer):
    user_id = serializers.PrimaryKeyRelatedField(many=False, read_only=True)
    event_id = serializers.PrimaryKeyRelatedField(many=False, read_only=True)

    class Meta:
        model = Invitation
        fields = [
            "user_id",
            "event_id",
            "status"
        ]
