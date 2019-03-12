from django.db import models
from users.models import UserProfile
from locations.models import Location
from rest_framework.fields import ListField

import datetime

today = datetime.datetime.today().strftime('%Y-%m-%d')
rinnow = datetime.datetime.today().strftime('%H:%M')

class Event(models.Model):
    event_admin = models.ForeignKey(UserProfile, related_name='event_admin', on_delete=models.CASCADE)
    event_name = models.CharField(null=False, blank=False, max_length=256, help_text="Name of your Event", many=True)
    event_date = models.DateField(null=False, blank=False, default=today, help_text="YYYY-MM-DD")
    event_time = models.TimeField(null=False, blank=False, default=rinnow, help_text="HH:MM")
    event_duration = models.TimeField(default=None, blank=True, null=True, help_text="Length of Event in HH:MM")
    # file_attachment = models.FilePathField(default=None, null=True, blank=True)
    notes = models.TextField(default=None, help_text="Miscellaneous notes or details you wish to include")
    event_location = models.ForeignKey(Location, related_name='location', on_delete=None)
    permissions = ListField(models.IntegerField(default=None, blank=True), allow_empty=True, allow_null=True)
