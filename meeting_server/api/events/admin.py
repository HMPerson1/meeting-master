# Register your models here.
from django.contrib import admin
from .models import Event


class EventAdmin(admin.ModelAdmin):
    list_display = ('uuid', 'event_name', 'date')
    search_fields = ('event_name', 'date')
    ordering = ('event_name',)

admin.site.register(Event, EventAdmin)
