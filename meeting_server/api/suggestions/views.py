from django.http import Http404
from django.core.exceptions import ObjectDoesNotExist
from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.generics import ListCreateAPIView, ListAPIView
from api.suggestions.models import Suggestion
from api.suggestions.serializers import SuggestionModelSerializer


# Creates a new Suggestion
class SuggestionCreationView(ListCreateAPIView):
    queryset = Suggestion.objects.all()
    serializer_class = SuggestionModelSerializer


# Get the list of suggestions for a specific event
class SuggestionEventView(ListAPIView):
    serializer_class = SuggestionModelSerializer

    def get_queryset(self):
        return Suggestion.objects.filter(event_id=self.kwargs['event_id'])


class SuggestionDetailView(APIView):

    def get_object(self, event_id, location_id):
        try:
            return Suggestion.objects.get(event_id=event_id, location_id=location_id)
        except ObjectDoesNotExist:
            raise Http404

    def get(self, request, event_id, location_id, format=None):
        inv = self.get_object(event_id, location_id)
        serializer = SuggestionModelSerializer(inv)
        return Response(serializer.data)

    # DELETE - suggestion/{event_id}/{location_id}
    def delete(self, request, event_id, location_id, format=None):
        event = self.get_object(event_id, location_id)
        event.delete()
        return Response({"detail": "Delete Succeeded"}, status=status.HTTP_204_NO_CONTENT)

