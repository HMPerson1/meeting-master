import firebase_admin
import firebase_admin.messaging as messaging
from firebase_admin import credentials

from api.events.models import Event
from api.users.models import UserProfile

firebase_default_app = firebase_admin.initialize_app(
    credentials.Certificate('private/meeting-master-2a1e6-firebase-adminsdk-eivgs-ea5e65b4bc.json'))


def notify_invite(user: UserProfile, event: Event):
    if user.firebase_reg_token != "":
        messaging.send(messaging.Message(
            data={
                'kind': 'invite',
                'event_id': event.pk,
                'event_name': event.event_name,
            },
            token=user.firebase_reg_token
        ))


def notify_edit(event: Event):
    for user in UserProfile.objects.all():  # TODO: only for invited
        if user.firebase_reg_token != "":
            messaging.send(messaging.Message(
                data={
                    'kind': 'edit',
                    'event_id': event.pk,
                    'event_name': event.event_name,
                },
                token=user.firebase_reg_token
            ))
