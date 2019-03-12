from django.db import models
from django.utils.timezone import now
from api.users.models import UserProfile

import uuid
import datetime

today = datetime.datetime.today().strftime('%Y-%m-%d')

class Event(models.Model):
    event_name = models.CharField(null=False, blank=False, max_length=256)
    event_date = models.DateField(null=False, blank=False, default=today, help_text="YYYY-MM-DD")
    duration = models.DurationField(default=None, blank=True, null=True)
    file_attachment = models.FilePathField(default=None, null=True, blank=True)
    notes = models.TextField(default=None)
    # admin = models.ForeignKey(UserProfile, on_delete=models.CASCADE)

    # admin = models.ForeignKey(UserProfile, related_name='admin', on_delete=models.CASCADE)

    # invitations = models.ForeignKey(Invitation, related_name='invitations', )
    # location = models.ForeignKey('locations', on_delete=False)


#     Todo: Add in ForeignKey stuff to link with Users
