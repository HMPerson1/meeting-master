# Create your models here.

from django.db import models

from api.events.models import Event
from api.users.models import UserProfile
from api.locations.models import Location


class Suggestion(models.Model):
    # PENDING = 1
    # ACCEPTED = 2
    # DECLINED = 3
    # STATUS_CHOICES = (
    #     (PENDING, 'PENDING'),
    #     (ACCEPTED, 'ACCEPTED'),
    #     (DECLINED, 'REJECTED'),
    # )
    location_id = models.ForeignKey(Location, on_delete=models.CASCADE)
    event_id = models.ForeignKey(Event, on_delete=models.CASCADE)
    suggested_by = models.ForeignKey(UserProfile, on_delete=models.CASCADE)
    # status = models.PositiveIntegerField(choices=STATUS_CHOICES, default=PENDING)
