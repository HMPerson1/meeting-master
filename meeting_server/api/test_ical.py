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


class IcalTest(TestCase):
    def setUp(self):
        super(IcalTest, self).setUp()
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

    def testIcalUrl(self):
        response = self.client.get('/current_user/ical_url')
        assert response.status_code == status.HTTP_200_OK
        ical_url = json.loads(response.content)['ical_url']
        response = APIClient().get(ical_url)
        assert response.status_code == status.HTTP_200_OK

    def testIcalUrlStable(self):
        response = self.client.get('/current_user/ical_url')
        assert response.status_code == status.HTTP_200_OK
        ical_url1 = json.loads(response.content)['ical_url']
        response = self.client.get('/current_user/ical_url')
        assert response.status_code == status.HTTP_200_OK
        ical_url2 = json.loads(response.content)['ical_url']
        assert ical_url1 == ical_url2
