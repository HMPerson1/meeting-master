from django.db import models


class Location(models.Model):
    street_address = models.CharField(null=False, blank=False, max_length=256)
    state = models.CharField(null=False, blank=False, max_length=20)
    city = models.CharField(null=False, blank=False, max_length=100)
    number_of_uses = models.PositiveIntegerField(auto_created=True, default=0)
    # room_number = models.CharField(null=True, blank=True, default=None)

    class Meta:
        unique_together = ('street_address', 'city', 'state')
