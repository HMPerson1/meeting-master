from django.db import models
from api.users.models import UserProfile
from api.locations.models import Location
from rest_framework.fields import ListField

import datetime

today = datetime.datetime.today().strftime('%Y-%m-%d')
rinnow = datetime.datetime.today().strftime('%H:%M')


class Event(models.Model):
    # event_admin = models.ForeignKey(UserProfile, related_name='event_admin', on_delete=models.CASCADE)
    event_name = models.CharField(null=False, blank=False, max_length=256, help_text="Name of your Event")
    event_date = models.DateField(null=True, blank=True, help_text="YYYY-MM-DD")
    event_time = models.TimeField(null=True, blank=True, help_text="HH:MM")
    event_duration = models.TimeField(default=None, blank=True, null=True, help_text="Length of Event in HH:MM")
    file_attachment = models.FileField(null=True, blank=True)
    notes = models.TextField(default=None, help_text="Miscellaneous notes or details you wish to include")
    event_location = models.ForeignKey(Location, related_name='location', on_delete=None)
    # permissions = ListField(models.IntegerField(default=None, blank=True), allow_empty=True, allow_null=True)

    def __unicode__(self):
        return u"file_upload {0}".format(self.file_attachment.url)
