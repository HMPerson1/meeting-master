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
from api.suggestions.models import Suggestion
#
# # Create your tests here.
#


class CreateSuggestion(TestCase):
    def setUp(self):
        super(CreateSuggestion, self).setUp()
        self.alice = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='alice', first_name='Alice', last_name='Q.', email='alice@example.com'))
        self.bob = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='bob', first_name='Bob', last_name='Q.', email='bob@example.com'))
        self.charlie = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='charlie', first_name='Charlie', last_name='Q.', email='charlie@example.com'))

        self.party_place1 = Location.objects.create(street_address='123 Main St.', city='Anywhere', state='IN')
        self.party_place2 = Location.objects.create(street_address='456 NotMain St.', city='Somewhere', state='OH')
        self.party_place3 = Location.objects.create(street_address='10110011 Binary St.', city='Text Segment',
                                                    state="ME")
        self.alice_party = Event.objects.create(
            event_admin=self.alice,
            event_name='Party!',
            event_date=datetime.date.today() + datetime.timedelta(days=3),
            event_time=datetime.time(16, 10),
            event_duration=datetime.time(1, 0),
            event_location=self.party_place1,
            notes='PREPARE TO PARTY',
        )
        Invitation.objects.create(user_id=self.bob, event_id=self.alice_party, status=Invitation.ACCEPTED)
        Invitation.objects.create(user_id=self.charlie, event_id=self.alice_party, status=Invitation.ACCEPTED)

        Suggestion.objects.create(location_id=self.party_place2, event_id=self.alice_party, suggested_by=self.bob)
        Suggestion.objects.create(location_id=self.party_place3, event_id=self.alice_party, suggested_by=self.charlie)

        self.client = APIClient()
        self.client.credentials(
            HTTP_AUTHORIZATION='Token ' + create_token(TokenModel, self.alice.django_user, None).key)

    def testGetSuggestions(self):
        response = self.client.get("/suggestions/event-suggestions/{}".format(self.alice_party.pk))
        assert response.status_code == status.HTTP_200_OK
        assert json.loads(response.content) == [
            {'location_id': self.party_place2.pk, 'event_id': self.alice_party.pk},
            {'location_id': self.party_place3.pk, 'event_id': self.alice_party.pk},
        ]

    def testSuggestionsDetail(self):
        response = self.client.get("/suggestions/{}/{}".format(self.alice_party.pk, self.party_place2.pk))
        assert response.status_code == status.HTTP_200_OK
        assert json.loads(response.content) == {'location_id': self.party_place2.pk, 'event_id': self.alice_party.pk}

