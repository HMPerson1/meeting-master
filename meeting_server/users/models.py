from django.db import models
from django.utils.timezone import now
import uuid
import datetime

today = datetime.date.today()


class User(models.Model):
    uuid = models.UUIDField(default=uuid.uuid4, editable=False, unique=True)
    first_name = models.CharField(null=False, blank=False, max_length=50)
    last_name = models.CharField(null=False, blank=False, max_length=50)
    username = models.CharField(null=False, blank=False, max_length=50)
    email = models.EmailField(null=False, max_length=100)
#     Todo: Add in ForeignKey stuff to link with Users
