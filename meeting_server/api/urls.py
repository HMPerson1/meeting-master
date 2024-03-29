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
from django.conf import settings
from django.conf.urls import url
from django.conf.urls.static import static
from django.contrib import admin
from django.urls import include, path
from drf_yasg import openapi
from drf_yasg.views import get_schema_view
from rest_framework import routers, permissions

from api.events import urls as events_urls
from api.invitations import urls as invitations_urls
from api.locations import urls as locations_urls
from api.users import urls as users_urls
from api.suggestions import urls as suggestions_urls
from api.users.views import UserViewSet

router = routers.DefaultRouter()
router.register('users', UserViewSet, 'user')
# router.register('events', EventViewSet, 'event')

schema_view = get_schema_view(
    openapi.Info(
        title='Meeting Master API',
        default_version='v1',
        description='A suite of API Endpoints for the Meeting Master mobile app backend.'
    ),
    public=True,
    permission_classes=(permissions.AllowAny,),
)

urlpatterns = [
    url(r'^swagger/$', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
    url(r'^redoc/$', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc'),
    url(r'^admin/', admin.site.urls),
    url(r'^register/', include('rest_auth.registration.urls')),
    url(r'^rest-auth/', include('rest_auth.urls')),
    url(r'^events/', include(events_urls)),
    url(r'^locations/', include(locations_urls)),
    url(r'^invitations/', include(invitations_urls)),
    url(r'^suggestions/', include(suggestions_urls)),
    url(r'^current_user/', include(users_urls)),
    url(r'^', include(router.urls))
]

if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
