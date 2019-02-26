from drf_writable_nested import WritableNestedModelSerializer
from .models import User
from rest_framework import serializers


class UserCreateSerializer(serializers.Serializer):
    # user_uuid = serializers.UUIDField(read_only=True)
    username = serializers.CharField(required=True, allow_null=False, max_length=256)
    password = serializers.HiddenField()
    first_name = serializers.CharField(max_length=50)
    last_name = serializers.CharField(max_length=50)
    email = serializers.EmailField(max_length=100, required=True)

    def create(self, validated_data):
        user = super(UserCreateSerializer, self).create(validated_data)
        user.save()
        return user


class UserModelSerializer(WritableNestedModelSerializer):

    # Todo: Write out fields and whatnot
    class Meta:
        model = User
        fields = [
            "uuid",
            "username",
            "password",
            "first_name",
            "last_name",
            "email",
        ]

    def create(self, validated_data):
        user = super(UserModelSerializer, self).create(validated_data)
        user.persist()
        return user
