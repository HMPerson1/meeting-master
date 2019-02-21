# Create your models here.

from django.contrib.auth import models as auth_models
from django.db import models
from phonenumber_field.modelfields import PhoneNumberField


class UserProfile(models.Model):
    # Django handles id, username, first_name, last_name, password, email
    django_user = models.OneToOneField(auth_models.User, on_delete=models.CASCADE)
    phone_number = PhoneNumberField()  # E.164 phone number
    profile_picture = models.ImageField(upload_to='profile_pictures/')
