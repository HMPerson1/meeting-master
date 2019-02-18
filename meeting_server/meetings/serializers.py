import rest_auth.registration.serializers as auth_serializers
from rest_auth.serializers import UserDetailsSerializer
from rest_framework import serializers
from rest_framework.fields import empty, get_attribute

from .models import UserProfile


# i have no idea what i'm doing but it seems to work
class ModelField(serializers.ModelField):
    def to_representation(self, obj):
        return super(ModelField, self).to_representation(get_attribute(obj, self.source_attrs[:-1]))


# https://django-rest-auth.readthedocs.io/en/latest/faq.html
class UserProfileSerializer(UserDetailsSerializer):
    name = serializers.CharField(source='userprofile.name')
    profile_picture = serializers.ImageField(source='userprofile.profile_picture')
    phone_number = ModelField(model_field=UserProfile._meta.get_field('phone_number'),
                              validators=UserProfile._meta.get_field('phone_number').validators,
                              source='userprofile.phone_number')

    class Meta(UserDetailsSerializer.Meta):
        fields = ('pk', 'username', 'email', 'name', 'phone_number', 'profile_picture')

    def update(self, instance, validated_data):
        profile_data = validated_data.pop('userprofile', {})
        instance = super(UserProfileSerializer, self).update(instance, validated_data)
        profile = instance.userprofile
        if profile_data:
            for attr, value in profile_data.items():
                setattr(profile, attr, value)
            profile.save()
        return instance


class RegisterSerializer(serializers.Serializer):
    username = auth_serializers.RegisterSerializer().fields['username']
    email = auth_serializers.RegisterSerializer().fields['email']
    password1 = auth_serializers.RegisterSerializer().fields['password1']
    password2 = auth_serializers.RegisterSerializer().fields['password2']
    name = serializers.CharField(required=False)
    phone_number = serializers.ModelField(model_field=UserProfile._meta.get_field('phone_number'),
                                          validators=UserProfile._meta.get_field('phone_number').validators,
                                          required=False)
    profile_picture = serializers.ImageField(required=False)

    def __init__(self, instance=None, data=empty, **kwargs):
        super(RegisterSerializer, self).__init__(instance=instance, data=data, **kwargs)
        auth_data = subset_dict(data, {'username', 'email', 'password1', 'password2'}) if data is not empty else empty
        self._auth_serializer = auth_serializers.RegisterSerializer(instance=instance, data=auth_data,
                                                                    **kwargs)

    def validate_username(self, *args, **kwargs):
        return self._auth_serializer.validate_username(*args, **kwargs)

    def validate_email(self, *args, **kwargs):
        return self._auth_serializer.validate_email(*args, **kwargs)

    def validate_password1(self, *args, **kwargs):
        return self._auth_serializer.validate_password1(*args, **kwargs)

    def validate(self, *args, **kwargs):
        return self._auth_serializer.validate(*args, **kwargs)

    def is_valid(self, raise_exception=False):
        if super(RegisterSerializer, self).is_valid(raise_exception):
            return self._auth_serializer.is_valid(raise_exception)
        return False

    def save(self, request):
        django_user = self._auth_serializer.save(request)

        profile_validated_data = subset_dict(self.validated_data, {'name', 'phone_number', 'profile_picture'})
        profile_validated_data['django_user'] = django_user
        user_profile = UserProfile.objects.create(**profile_validated_data)
        return user_profile.django_user

    def data(self):
        # kinda hack-y
        return self._auth_serializer.data


def subset_dict(bigdict, keys):
    return {k: bigdict[k] for k in bigdict.keys() & keys}
