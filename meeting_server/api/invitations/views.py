# Create your views here.
from rest_framework.response import Response
from rest_framework import status

from django.http import Http404
from .serializers import InvitationModelSerializer
from rest_framework.views import APIView


class InvitationListView(APIView):
    # queryset = Invitation.objects.all()
    # serializer_class = InvitationModelSerializer

    def post(self, request, format=None):

        serializer = InvitationModelSerializer(data=request.DATA)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


# Get the list of event ids for which you have been invited
class InvitationUserView(APIView):

    def get_inv_by_user(self, user_id):
        try:
            return Response.objects.get(user_id=user_id)
        except status.HTTP_404_NOT_FOUND:
            raise Http404

    def get(self, request, user_id, format=None):
        invitation_list = self.get_inv_by_user(user_id)
        # Todo: Try list serializer?
        serializer = InvitationModelSerializer(invitation_list)
        return Response(serializer.data)


# Get the list of invitees for a specific event
class InvitationEventView(APIView):

    def get_inv_by_event(self, event_id):
        try:
            return Response.objects.get(event_id=event_id)
        except status.HTTP_404_NOT_FOUND:
            raise Http404

    def get(self, request, event_id, format=None):
        invitation_list = self.get_inv_by_event(event_id)
        # Todo: Try list serializer?
        serializer = InvitationModelSerializer(invitation_list)
        return Response(serializer.data)

class InvitationDetailView(APIView):

    def get_object(self, user_id, event_id):
        try:
            return Response.objects.get(user_id=user_id, event_id=event_id)
        except status.HTTP_404_NOT_FOUND:
            raise Http404

    # GET - /invitation/{user_id}/{event_id}
    def get(self, request, pk, format=None):
        event = self.get_object(pk)
        serializer = EventModelSerializer(event)
        return Response(serializer.data)

    # PUT - /event/{pk}
    def put(self, request, pk, format=None):
        event = self.get_object(pk)
        serializer = EventModelSerializer(event, data=request.DATA)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    # DELETE - /event/{pk}
    def delete(self, request, pk, format=None):
        event = self.get_object(pk)
        event.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)