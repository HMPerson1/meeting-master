from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from django.http import Http404
from django.core.exceptions import ObjectDoesNotExist
from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.generics import ListCreateAPIView, ListAPIView

from api.invitations.models import Invitation
from api.invitations.serializers import InvitationModelSerializer, InvitationStatusUpdateSerializer


# Creates a new Invitation
class InvitationCreationView(ListCreateAPIView):
    queryset = Invitation.objects.all()
    serializer_class = InvitationModelSerializer


# Get the list of event ids for which you have been invited
class InvitationUserView(ListAPIView):
    """
    List of invitations for the current user
    """
    serializer_class = InvitationModelSerializer

    def get_queryset(self):
        return Invitation.objects.filter(user_id=self.request.user.userprofile)


# Get the list of invitees for a specific event
class InvitationEventView(ListAPIView):
    serializer_class = InvitationModelSerializer

    def get_queryset(self):
        return Invitation.objects.filter(event_id=self.kwargs['event_id'])


class InvitationDetailView(APIView):

    def get_object(self, event_id, user_id):
        try:
            return Invitation.objects.get(event_id=event_id, user_id=user_id)
        except ObjectDoesNotExist:
            raise Http404

    def get(self, request, event_id, user_id, format=None):
        inv = self.get_object(event_id, user_id)
        serializer = InvitationModelSerializer(inv)
        return Response(serializer.data)

    # DELETE - invitation/{event_id}/{user_id}
    def delete(self, request, event_id, user_id, format=None):
        event = self.get_object(user_id, event_id)
        event.delete()
        return Response({"detail": "Delete Succeeded"}, status=status.HTTP_204_NO_CONTENT)


class InvitationStatusChangeView(APIView):

    def get_object(self, event_id, user_id):
        try:
            return Invitation.objects.get(event_id=event_id, user_id=user_id)
        except ObjectDoesNotExist:
            raise Http404

    @swagger_auto_schema(
        query_serializer=InvitationStatusUpdateSerializer,
        responses={201: openapi.Response('Invitation status successfuly updated', InvitationModelSerializer)},
    )
    def put(self, request, event_id, user_id, format=None):
        event = self.get_object(event_id, user_id)
        serializer = InvitationStatusUpdateSerializer(event, data=request.DATA)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

