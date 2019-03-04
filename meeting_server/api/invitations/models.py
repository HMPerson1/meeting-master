# Create your models here.

from django.db import models
from users.models import UserProfile
from events.models import Event

inv_status = {
    "PENDING": 1,
    "ACCEPTED": 2,
    "DECLINED": 3
}


class Invitation(models.Model):
    # Django handles id, username, first_name, last_name, password, email
    user_profile = models.ForeignKey(UserProfile, related_name='user_id', to_field='pk', on_delete=models.CASCADE)
    event_id = models.ForeignKey(Event, related_name='event_id', to_field='pk', on_delete=None)
    status = models.PositiveIntegerField(default=inv_status["PENDING"], null=False, blank=False)
