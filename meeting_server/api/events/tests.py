import datetime
import json

import django.contrib.auth.models as auth_models
from django.test import TestCase
from rest_auth.app_settings import create_token
from rest_auth.models import TokenModel
from rest_framework import status
from rest_framework.test import APIClient

from api.invitations.models import Invitation
from api.locations.models import Location
from api.users.models import UserProfile
from .models import Event, ActiveEvent


# Create your tests here.

class ActiveEventLeavingTests(TestCase):
    def setUp(self):
        super(ActiveEventLeavingTests, self).setUp()
        self.alice = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='alice', first_name='Alice', last_name='Q.', email='alice@example.com'))
        self.bob = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='bob', first_name='Bob', last_name='Q.', email='bob@example.com'))
        self.charlie = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='charlie', first_name='Charlie', last_name='Q.', email='charlie@example.com'))

        self.party_place = Location.objects.create(street_address='123 Main St.', city='Anywhere', state='IN')
        start = datetime.datetime.now() - datetime.timedelta(hours=1)
        self.alice_party = Event.objects.create(
            event_admin=self.alice,
            event_name='Party!',
            event_date=start.date(),
            event_time=start.time(),
            event_duration=datetime.time(0, 50),
            event_location=self.party_place,
            notes='PREPARE TO PARTY',
        )
        Invitation.objects.create(user_id=self.bob, event_id=self.alice_party, status=Invitation.ACCEPTED)
        Invitation.objects.create(user_id=self.charlie, event_id=self.alice_party, status=Invitation.ACCEPTED)
        ActiveEvent.objects.create(event=self.alice_party, user=self.bob, state=ActiveEvent.LEAVING_FROM)
        ActiveEvent.objects.create(event=self.alice_party, user=self.charlie, state=ActiveEvent.LEAVING_FROM)

        self.client = APIClient()

    def test_leave_one(self):
        self.client.credentials(
            HTTP_AUTHORIZATION='Token ' + create_token(TokenModel, self.bob.django_user, None).key)
        response = self.client.delete('/events/current-user-active-event')
        assert response.status_code == status.HTTP_204_NO_CONTENT

        response = self.client.get(f'/events/{self.alice_party.id}')
        assert response.status_code == status.HTTP_200_OK

        assert json.loads(response.content)['current_overall_state'] == Event.OverallState.ENDING.value

    def test_leave_all(self):
        self.client.credentials(
            HTTP_AUTHORIZATION='Token ' + create_token(TokenModel, self.bob.django_user, None).key)
        response = self.client.delete('/events/current-user-active-event')
        assert response.status_code == status.HTTP_204_NO_CONTENT

        self.client.credentials(
            HTTP_AUTHORIZATION='Token ' + create_token(TokenModel, self.charlie.django_user, None).key)
        response = self.client.delete('/events/current-user-active-event')
        assert response.status_code == status.HTTP_204_NO_CONTENT

        response = self.client.get(f'/events/{self.alice_party.id}')
        assert response.status_code == status.HTTP_200_OK

        assert json.loads(response.content)['current_overall_state'] == Event.OverallState.OVER.value
