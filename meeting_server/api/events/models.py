from datetime import datetime, timedelta
from enum import Enum

from django.db import models

from api.locations.models import Location
from api.users.models import UserProfile

start_buffer = timedelta(hours=2)


class Event(models.Model):
    event_admin = models.ForeignKey(UserProfile, related_name='event_admin', on_delete=models.CASCADE)
    event_name = models.CharField(null=False, blank=False, max_length=256, help_text="Name of your Event")
    event_date = models.DateField(null=True, blank=True, help_text="YYYY-MM-DD")
    event_time = models.TimeField(null=True, blank=True, help_text="HH:MM")
    event_duration = models.TimeField(default=None, blank=True, null=True, help_text="Length of Event in HH:MM")
    file_attachment = models.FileField(null=True, blank=True)
    notes = models.TextField(default=None, help_text="Miscellaneous notes or details you wish to include")
    event_location = models.ForeignKey(Location, related_name='location', on_delete=None)

    def __unicode__(self):
        return u"file_upload {0}".format(self.file_attachment.url)

    class OverallState(Enum):
        NOT_STARTED = 0
        STARTING = 1
        ONGOING = 2
        ENDING = 3
        OVER = 4

    def current_overall_state(self) -> OverallState:
        """
        :return: The current overall state of the event based on the current wall-clock time and the individual states
        of all attendees
        """
        from api.invitations.models import Invitation
        now = datetime.now()
        start = datetime.combine(self.event_date, self.event_time)
        duration = timedelta(
            hours=self.event_duration.hour,
            minutes=self.event_duration.minute,
            seconds=self.event_duration.second,
            microseconds=self.event_duration.microsecond,
        )
        if now < start - start_buffer:
            return Event.OverallState.NOT_STARTED
        attendees = UserProfile.objects.filter(invitation__event_id=self, invitation__status=Invitation.ACCEPTED)
        if now < start + duration:
            if any((not hasattr(u, 'activeevent') or u.activeevent.state == ActiveEvent.GOING_TO for u in attendees)):
                return Event.OverallState.STARTING
            else:
                return Event.OverallState.ONGOING
        else:
            if any((hasattr(u, 'activeevent') for u in attendees)):
                return Event.OverallState.ENDING
            else:
                return Event.OverallState.OVER


class ActiveEvent(models.Model):
    """
    Maintains what a user's current state is for an "active" event.
    Note that a user can only be "active" in one event at a time.
    """
    GOING_TO = 1
    CURRENTLY_AT = 2
    LEAVING_FROM = 3
    ACTIVE_EVENT_STATE_CHOICES = (
        (GOING_TO, "Going To"),
        (CURRENTLY_AT, "Currently At"),
        (LEAVING_FROM, "Leaving From"),
    )

    event = models.ForeignKey(Event, on_delete=models.CASCADE)
    user = models.OneToOneField(UserProfile, on_delete=models.CASCADE)
    state = models.PositiveSmallIntegerField(choices=ACTIVE_EVENT_STATE_CHOICES)
