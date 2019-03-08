"""meeting_server URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import include, path
from rest_framework import routers
from rest_framework_swagger.views import get_swagger_view

import invitations.urls

from users.views import UserViewSet
from events.views import EventViewSet

router = routers.DefaultRouter()
router.register('users', UserViewSet, 'user')
router.register('events', EventViewSet, 'event')

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', get_swagger_view()),
    path('register/', include('rest_auth.registration.urls')),
    path('rest-auth/', include('rest_auth.urls')),
    # path(r'events/', include(events.urls)),
    path(r'invitations/', include(invitations.urls)),
    path('', include(router.urls)),
]

