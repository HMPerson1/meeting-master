from django.db import models
from django.utils.timezone import now
import uuid
import datetime

today = datetime.date.today()


class Event(models.Model):
    uuid = models.UUIDField(default=uuid.uuid4, editable=False, unique=True)
    name = models.CharField(null=False, blank=False, max_length=256)
    date = models.DateField(null=False, blank=False, default=now)
    duration = models.DurationField(null=False, blank=False)
    attachment = models.FileField(default=None)
    notes = models.TextField(default=None)
#     Todo: Add in ForeignKey stuff to link with Users
