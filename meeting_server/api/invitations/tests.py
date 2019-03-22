import datetime
import json

import django.contrib.auth.models as auth_models
from django.test import TestCase
from rest_auth.app_settings import create_token
from rest_auth.models import TokenModel
from rest_framework import status
from rest_framework.test import APIClient

from api.events.models import Event
from api.invitations.models import Invitation
from api.locations.models import Location
from api.users.models import UserProfile


# Create your tests here.

class InvitationsByEventTests(TestCase):
    def setUp(self):
        super(InvitationsByEventTests, self).setUp()
        self.alice = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='alice', first_name='Alice', last_name='Q.', email='alice@example.com'))
        self.bob = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='bob', first_name='Bob', last_name='Q.', email='bob@example.com'))
        self.charlie = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='charlie', first_name='Charlie', last_name='Q.', email='charlie@example.com'))

        self.party_place = Location.objects.create(street_address='123 Main St.', city='Anywhere', state='IN')
        self.alice_party = Event.objects.create(
            event_admin=self.alice,
            event_name='Party!',
            event_date=datetime.date.today() + datetime.timedelta(days=3),
            event_time=datetime.time(16, 10),
            event_duration=datetime.time(1, 0),
            event_location=self.party_place,
            notes='PREPARE TO PARTY',
        )
        Invitation.objects.create(user_id=self.bob, event_id=self.alice_party, status=Invitation.ACCEPTED)
        Invitation.objects.create(user_id=self.charlie, event_id=self.alice_party, status=Invitation.ACCEPTED)

        self.client = APIClient()
        self.client.credentials(
            HTTP_AUTHORIZATION='Token ' + create_token(TokenModel, self.alice.django_user, None).key)

    def testGetInvitations(self):
        response = self.client.get("/invitations/event-invitations/{}".format(self.alice_party.pk))
        assert response.status_code == status.HTTP_200_OK
        assert json.loads(response.content) == [
            {'user_id': self.bob.pk, 'event_id': self.alice_party.pk, 'status': Invitation.ACCEPTED,
             'edit_permission': False},
            {'user_id': self.charlie.pk, 'event_id': self.alice_party.pk, 'status': Invitation.ACCEPTED,
             'edit_permission': False},
        ]

    def testGetInvitationsDeclined(self):
        bob_inv = Invitation.objects.get(user_id=self.bob, event_id=self.alice_party)
        bob_inv.status = Invitation.DECLINED
        bob_inv.save()
        response = self.client.get("/invitations/event-invitations/{}".format(self.alice_party.pk))
        assert response.status_code == status.HTTP_200_OK
        assert json.loads(response.content) == [
            {'user_id': self.bob.pk, 'event_id': self.alice_party.pk, 'status': Invitation.DECLINED,
             'edit_permission': False},
            {'user_id': self.charlie.pk, 'event_id': self.alice_party.pk, 'status': Invitation.ACCEPTED,
             'edit_permission': False},
        ]


class CurrentUserInvitationsTest(TestCase):
    def setUp(self):
        super(CurrentUserInvitationsTest, self).setUp()
        self.alice = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='alice', first_name='Alice', last_name='Q.', email='alice@example.com'))
        self.bob = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='bob', first_name='Bob', last_name='Q.', email='bob@example.com'))

        self.party_place = Location.objects.create(street_address='123 Main St.', city='Anywhere', state='IN')
        self.bob_party = Event.objects.create(
            event_admin=self.bob,
            event_name='Party!',
            event_date=datetime.date.today() + datetime.timedelta(days=3),
            event_time=datetime.time(16, 10),
            event_duration=datetime.time(1, 0),
            event_location=self.party_place,
            notes='PREPARE TO PARTY',
        )
        Invitation.objects.create(user_id=self.alice, event_id=self.bob_party, status=Invitation.ACCEPTED)

        self.client = APIClient()
        self.client.credentials(
            HTTP_AUTHORIZATION='Token ' + create_token(TokenModel, self.alice.django_user, None).key)

    def testGetInvitations(self):
        response = self.client.get("/invitations/user-invitations")
        assert response.status_code == status.HTTP_200_OK
        assert json.loads(response.content) == \
               [{'user_id': self.alice.pk, 'event_id': self.bob_party.pk, 'status': Invitation.ACCEPTED,
                 'edit_permission': False}]
