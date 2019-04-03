import logging

import firebase_admin
import firebase_admin.messaging as messaging
from firebase_admin import credentials

from api.events.models import Event
from api.invitations.models import Invitation
from api.users.models import UserProfile

logger = logging.getLogger(__name__)

FIREBASE_CREDENTIALS_FILE = 'private/meeting-master-2a1e6-firebase-adminsdk-eivgs-ea5e65b4bc.json'
firebase_enabled = False

try:
    firebase_default_app = firebase_admin.initialize_app(
        credentials.Certificate(FIREBASE_CREDENTIALS_FILE))
    firebase_enabled = True
except FileNotFoundError:
    logger.warning("Firebase key not found; notifications won't work")
    pass


def notify_invite(user: UserProfile, event: Event, dry_run=False):
    if not firebase_enabled:
        return
    if user.firebase_reg_token != "":
        data = {
            'kind': 'invite',
            'event_id': str(event.pk),
            'event_name': event.event_name,
        }
        logger.debug(f'sending fcm message to {user.django_user.username} (id {user.django_user_id}): {data}')
        messaging.send(messaging.Message(
            data=data,
            token=user.firebase_reg_token
        ), dry_run=dry_run)


def notify_edit(event: Event, dry_run=False):
    if not firebase_enabled:
        return
    for user in UserProfile.objects.filter(invitation__event_id=event, invitation__status=Invitation.ACCEPTED):
        if user.firebase_reg_token != "":
            data = {
                'kind': 'edit',
                'event_id': str(event.pk),
                'event_name': event.event_name,
            }
            logger.debug(f'sending fcm message to {user.django_user.username} (id {user.django_user_id}): {data}')
            messaging.send(messaging.Message(
                data=data,
                token=user.firebase_reg_token
            ), dry_run=dry_run)
