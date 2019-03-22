import datetime

import django.contrib.auth.models as auth_models
from django.test import TestCase

from api import fcm
from api.events.models import Event
from api.invitations.models import Invitation, inv_status
from api.locations.models import Location
from api.users.models import UserProfile


class FcmTests(TestCase):
    def setUp(self):
        self.alice = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='alice', first_name='Alice', last_name='Q.', email='alice@example.com'))
        self.bob = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='bob', first_name='Bob', last_name='Q.', email='bob@example.com'))
        self.charlie = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='charlie', first_name='Bob', last_name='Q.', email='charlie@example.com'))
        self.dave = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='dave', first_name='Bob', last_name='Q.', email='dave@example.com'))
        self.emily = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='emily', first_name='Bob', last_name='Q.', email='emily@example.com'))

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
        Invitation.objects.create(user_id=self.bob, event_id=self.alice_party, status=inv_status['ACCEPTED'])
        Invitation.objects.create(user_id=self.charlie, event_id=self.alice_party, status=inv_status['ACCEPTED'])

    def test_notif_invite(self):
        self.dave.firebase_reg_token = 'c3qK8fsFYVU:APA91bGjTo7UNvJZTvEF4rGlNO_g3VQ1zxbeL8PDoh3y3UGK5584sBinVDyL6sLJdt626i5Fq_NfiZ2Ms0AD03dXUKXoKPLvIhql6rvP8O5im6ZFIyUcIGYaZIBzwC7JEdvenvp-q5d9'
        fcm.notify_invite(self.dave, self.alice_party, dry_run=True)

    def test_notif_invite_notoken(self):
        self.emily.firebase_reg_token = ''
        fcm.notify_invite(self.emily, self.alice_party, dry_run=True)

    def test_notif_edit(self):
        self.bob.firebase_reg_token = 'c3qK8fsFYVU:APA91bGjTo7UNvJZTvEF4rGlNO_g3VQ1zxbeL8PDoh3y3UGK5584sBinVDyL6sLJdt626i5Fq_NfiZ2Ms0AD03dXUKXoKPLvIhql6rvP8O5im6ZFIyUcIGYaZIBzwC7JEdvenvp-q5d9'
        self.bob.save()
        fcm.notify_edit(self.alice_party, dry_run=True)

    def test_notif_edit_notoken(self):
        self.charlie.firebase_reg_token = ''
        self.charlie.save()
        fcm.notify_edit(self.alice_party, dry_run=True)
