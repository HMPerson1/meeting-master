from collections import namedtuple
from typing import Type, Union

import django.contrib.auth.models as auth_models
import rest_auth.registration.serializers as auth_reg_serializers
import rest_auth.serializers as auth_serializers
from rest_auth.serializers import UserDetailsSerializer
from rest_framework import serializers
from rest_framework.fields import empty, get_attribute
from rest_framework.reverse import reverse

from api import settings
from api.users.models import UserProfile, LiveLocation


# i have no idea what i'm doing but it seems to work
class ModelField(serializers.ModelField):
    def to_representation(self, obj):
        return super(ModelField, self).to_representation(get_attribute(obj, self.source_attrs[:-1]))


# https://django-rest-auth.readthedocs.io/en/latest/faq.html
class UserProfileSerializer(UserDetailsSerializer):
    profile_picture = serializers.ImageField(source='userprofile.profile_picture', required=False)
    phone_number = ModelField(model_field=UserProfile._meta.get_field('phone_number'),
                              validators=UserProfile._meta.get_field('phone_number').validators,
                              source='userprofile.phone_number', required=False)

    class Meta:
        model = auth_models.User
        fields = ('pk', 'username', 'first_name', 'last_name', 'email', 'phone_number', 'profile_picture')
        read_only_fields = ('pk', 'profile_picture')

    def update(self, instance, validated_data):
        profile_data = validated_data.pop('userprofile', {})
        instance = super(UserProfileSerializer, self).update(instance, validated_data)
        profile = instance.userprofile
        if profile_data:
            for attr, value in profile_data.items():
                setattr(profile, attr, value)
            profile.save()
        return instance

    def to_representation(self, instance: Union[UserProfile, auth_models.User]):
        if isinstance(instance, UserProfile):
            instance = instance.django_user
        return super().to_representation(instance)


class RegisterSerializer(serializers.Serializer):
    username = auth_reg_serializers.RegisterSerializer().fields['username']
    first_name = serializers.CharField(allow_blank=True, max_length=30, required=False)
    last_name = serializers.CharField(allow_blank=True, max_length=150, required=False)
    email = auth_reg_serializers.RegisterSerializer().fields['email']
    password1 = auth_reg_serializers.RegisterSerializer().fields['password1']
    password2 = auth_reg_serializers.RegisterSerializer().fields['password2']
    phone_number = serializers.ModelField(model_field=UserProfile._meta.get_field('phone_number'),
                                          validators=UserProfile._meta.get_field('phone_number').validators,
                                          required=False)

    def __init__(self, instance=None, data: Union[Type[empty], dict] = empty, **kwargs):
        super(RegisterSerializer, self).__init__(instance=instance, data=data, **kwargs)
        auth_data = subset_dict(data, {'username', 'email', 'password1', 'password2'}) if data is not empty else empty
        self._auth_serializer = auth_reg_serializers.RegisterSerializer(instance=instance, data=auth_data,
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
        for attr, value in subset_dict(self.validated_data, {'first_name', 'last_name'}).items():
            setattr(django_user, attr, value)
        django_user.save()

        profile_validated_data = subset_dict(self.validated_data, {'phone_number'})
        profile_validated_data['django_user'] = django_user
        user_profile = UserProfile.objects.create(**profile_validated_data)
        return user_profile.django_user

    @property
    def data(self):
        # kinda hack-y
        return self._auth_serializer.data


class PasswordResetSerializer(auth_serializers.PasswordResetSerializer):
    def get_email_options(self):
        return {'email_template_name': 'password_reset_email.html'}


class FirebaseRegTokenSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserProfile
        fields = ('firebase_reg_token',)


class ProfilePictureSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserProfile
        fields = ('profile_picture',)

    def validate_profile_picture(self, value):
        if value.size > settings.MAX_UPLOAD_SIZE:
            raise serializers.ValidationError("File too large")
        return value


class UserIcalUrlSerializer(serializers.Serializer):
    ical_url = serializers.URLField()

    def to_representation(self, instance: UserProfile):
        i = namedtuple('n', 'ical_url')(
            ical_url=reverse('api-event-ical', request=self.context['request'], kwargs={'ical_key': instance.ical_key}))
        return super(UserIcalUrlSerializer, self).to_representation(i)


class LiveLocationPutSerializer(serializers.ModelSerializer):
    class Meta:
        model = LiveLocation
        fields = ('lon', 'lat')


def subset_dict(bigdict, keys):
    return {k: bigdict[k] for k in bigdict.keys() & keys}
