import json

import django.contrib.auth.models as auth_models
from django.test import TestCase
from rest_auth.app_settings import create_token
from rest_auth.models import TokenModel
from rest_framework import status
from rest_framework.test import APIClient

from .models import UserProfile


# Create your tests here.


class ProfilePictureTests(TestCase):
    def setUp(self):
        super(ProfilePictureTests, self).setUp()
        self.alice = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='alice', first_name='Alice', last_name='Q.', email='alice@example.com'))
        self.pfp_alice = open('test_files/alice.png', 'rb')
        self.pfp_large = open('test_files/alice-large.png', 'rb')
        self.client = APIClient()
        self.client.credentials(
            HTTP_AUTHORIZATION='Token ' + create_token(TokenModel, self.alice.django_user, None).key)

    def tearDown(self):
        super(ProfilePictureTests, self).tearDown()
        self.pfp_alice.close()
        self.pfp_large.close()

    def testSetPfp(self):
        self.alice.profile_picture = None
        self.alice.save()
        response = self.client.put('/current_user/profile_picture', {'profile_picture': self.pfp_alice},
                                   format='multipart')
        assert response.status_code == status.HTTP_200_OK

        response = self.client.get("/rest-auth/user/")
        assert response.status_code == status.HTTP_200_OK
        assert json.loads(response.content)['profile_picture'] is not None

    def testPfpTooLarge(self):
        self.alice.profile_picture = None
        self.alice.save()
        response = self.client.put('/current_user/profile_picture', {'profile_picture': self.pfp_large},
                                   format='multipart')
        assert response.status_code == status.HTTP_400_BAD_REQUEST
        assert json.loads(response.content) == {'profile_picture': ['File too large']}

        response = self.client.get("/rest-auth/user/")
        assert response.status_code == status.HTTP_200_OK
        assert json.loads(response.content)['profile_picture'] is None


class RegisterTests(TestCase):
    def setUp(self):
        super(RegisterTests, self).setUp()
        self.client = APIClient()
        self.client.credentials()

    def testRegister(self):
        response = self.client.post('/register/',
                                    {'username': 'alice', 'password1': 'a1b2c3qqq', 'password2': 'a1b2c3qqq'},
                                    format='json')
        assert response.status_code == status.HTTP_201_CREATED

    def testRegisterExistingUsername(self):
        UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='alice', first_name='Alice', last_name='Q.', email='alice@example.com')).save()
        response = self.client.post('/register/',
                                    {'username': 'alice', 'password1': 'a1b2c3qqq', 'password2': 'a1b2c3qqq'},
                                    format='json')
        assert response.status_code == status.HTTP_400_BAD_REQUEST
        assert json.loads(response.content) == {'username': ['A user with that username already exists.']}


class LoginTests(TestCase):
    def setUp(self):
        super(LoginTests, self).setUp()
        self.alice = UserProfile.objects.create(django_user=auth_models.User.objects.create(
            username='alice', first_name='Alice', last_name='Q.', email='alice@example.com'))
        self.alice.django_user.set_password('a1b2c3qqq')
        self.alice.django_user.save()
        self.client = APIClient()
        self.client.credentials()

    def testLogin(self):
        response = self.client.post('/rest-auth/login/', data={'username': 'alice', 'password': 'a1b2c3qqq'},
                                    format='json')
        assert response.status_code == status.HTTP_200_OK
        assert 'key' in json.loads(response.content)

    def testLoginWrongPassword(self):
        response = self.client.post('/rest-auth/login/', data={'username': 'alice', 'password': 'jjjjjjjjjjjj'},
                                    format='json')
        assert response.status_code == status.HTTP_400_BAD_REQUEST
        assert json.loads(response.content) == {'non_field_errors': ['Unable to log in with provided credentials.']}
