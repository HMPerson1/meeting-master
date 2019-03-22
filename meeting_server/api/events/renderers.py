from datetime import datetime

import icalendar
from django.contrib.sites.models import Site
from rest_framework.renderers import BaseRenderer


class IcalRenderer(BaseRenderer):
    media_type = 'text/calendar'
    format = 'ical'

    def render(self, data, accepted_media_type=None, renderer_context=None):
        now = datetime.utcnow()
        cal = icalendar.Calendar()
        cal.add('prodid', '~//meeting-master//NONSGML Meeting Master Generated//')
        cal.add('version', '2.0')
        for event_data in data:
            cal_event = icalendar.Event()
            cal_event['uid'] = 'pk-{}@{}'.format(event_data['pk'], Site.objects.get_current().domain)
            cal_event.add('dtstamp', now)
            cal_event.add('summary', event_data['event_name'])
            cal_event.add('dtstart', datetime.combine(event_data['event_date'], event_data['event_time']))
            cal_event.add('duration', event_data['event_duration'])
            cal_event.add('location', '{street_address}, {city}, {state}'.format(**event_data['event_location']))
            cal_event.add('description', event_data['notes'])
            if event_data['file_attachment'] is not None:
                cal_event.add('attach', event_data['file_attachment'])
            for att_data in event_data['attendees']:
                cal_att = icalendar.vCalAddress('MAILTO:' + att_data['email'])
                cal_att.params['cn'] = icalendar.vText(att_data['full_name'])
                cal_att.params['ROLE'] = icalendar.vText('REQ-PARTICIPANT')
                cal_att.params['PARTSTAT'] = icalendar.vText(
                    {1: 'NEEDS-ACTION', 2: 'ACCEPTED', 3: 'DECLINED'}[att_data['status']])
                cal_event.add('attendee', cal_att, encode=0)
            cal.add_component(cal_event)
        return cal.to_ical()
