# Create your models here.

from django.contrib.auth import models as auth_models
from django.db import models
from django.utils import crypto
from phonenumber_field.modelfields import PhoneNumberField


def gen_ical_key():
    return crypto.get_random_string(length=24)


class UserProfile(models.Model):
    # Django handles id, username, first_name, last_name, password, email
    django_user = models.OneToOneField(auth_models.User, on_delete=models.CASCADE, primary_key=True)
    phone_number = PhoneNumberField()  # E.164 phone number
    profile_picture = models.ImageField(upload_to='profile_pictures/', null=True)
    firebase_reg_token = models.CharField(max_length=256, blank=True, default="")
    # TODO: one should be able to request a new ical_key if it gets "compromised"
    ical_key = models.CharField(max_length=24, blank=True, default=gen_ical_key, editable=False, unique=True)
    latitude = models.FloatField(blank=True, null=True)
    longitude = models.FloatField(blank=True, null=True)
