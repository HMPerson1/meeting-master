# Create your models here.

from django.db import models
from api.users.models import UserProfile
from api.events.models import Event
from composite_pk import composite

inv_status = {
    "PENDING": 1,
    "ACCEPTED": 2,
    "DECLINED": 3
}


class Invitation(models.Model):
    # Django handles id, username, first_name, last_name, password, email
    user_id = models.ForeignKey(UserProfile, related_name='user_id', on_delete=models.CASCADE)
    event_id = models.ForeignKey(Event, related_name='event_id', on_delete=None)
    status = models.PositiveIntegerField(default=inv_status["PENDING"], null=False, blank=False)
