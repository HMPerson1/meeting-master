from django.db import models
import uuid
import datetime

today = datetime.date.today()


class Event(models.Model):
    uuid = models.UUIDField(default=uuid.uuid4, editable=False, unique=True)
    name = models.CharField(null=False, blank=False, required=True)
    date = models.DateField(null=False, blank=False, default=today)
    duration = models.DurationField(null=False, blank=False)
    attachment = models.FileField(default=None, required=False)
    notes = models.TextField(default=None)
#     Todo: Add in ForeignKey stuff to link with Users
