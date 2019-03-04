from django.db import models
from django.utils.timezone import now
from users.models import UserProfile

import uuid
import datetime

today = datetime.date.today()
#
#
# class Event(models.Model):
#     uuid = models.UUIDField(default=uuid.uuid4, editable=False, unique=True)
#     name = models.CharField(null=False, blank=False, max_length=256)
#     date = models.DateField(null=False, blank=False, default=now)
#     duration = models.DurationField(null=False, blank=False)
#     attachment = models.FileField(default=None)
#     notes = models.TextField(default=None)


class Event(models.Model):
    uuid = models.UUIDField(default=uuid.uuid4, editable=False, unique=True)
    event_name = models.CharField(null=False, blank=False, max_length=256)
    date = models.DateField(null=False, blank=False, default=now)
    duration = models.DurationField(default=None)
    file_attachment = models.TextField(default=None)
    notes = models.TextField(default=None)

    admin_id = models.ForeignKey(UserProfile, related_name='admin_id', to_field='pk', on_delete=models.CASCADE)
    # invitations = models.ForeignKey(Invitation, related_name='invitations', )
    # location = models.ForeignKey('locations', on_delete=False)


#     Todo: Add in ForeignKey stuff to link with Users
