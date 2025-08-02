package com.example.demo;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Service for managing Google Calendar operations.
 * Provides methods to create, list, delete calendars and events,
 * and search for events by various criteria.
 *
 * All datetime formats should be in Rfc3339 format, e.g. "2025-10-10T16:00:00-03:00"
 */
@Service
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    private final Calendar calendarClient;

    public ScheduleService(final Calendar calendarClient) {
        this.calendarClient = calendarClient;
    }

    @Tool(description = "Creates a new calendar with the specified name (summary)")
    public com.google.api.services.calendar.model.Calendar createCalendar(String summary) throws IOException {
        logger.info("Creating calendar with summary: {}", summary);
        final var calendar = new com.google.api.services.calendar.model.Calendar()
                .setSummary(summary)
                .setTimeZone("America/Sao_Paulo");
        com.google.api.services.calendar.model.Calendar result = calendarClient.calendars()
                .insert(calendar)
                .execute();

        calendarClient.acl().insert(result.getId(), new AclRule()
                .setRole("writer")
                .setScope(new AclRule.Scope().setType("user").setValue("gbzarelli@gmail.com")))
                .execute();

        logger.info("Created calendar: {}", result);
        return result;
    }

    @Tool(description = "Lists all calendars available")
    public List<CalendarListEntry> listCalendars() throws IOException {
        logger.info("Listing all calendars");
        Calendar.CalendarList.List request = calendarClient.calendarList().list();
        List<CalendarListEntry> result = request.execute().getItems();
        logger.info("Listed calendars: {}", result);
        return result;
    }

    @Tool(description = "Deletes a calendar by its ID")
    public boolean deleteCalendar(String calendarId) throws IOException {
        logger.info("Deleting calendar with ID: {}", calendarId);
        calendarClient.calendars().delete(calendarId).execute();
        logger.info("Deleted calendar with ID: {}", calendarId);
        return true;
    }

    @Tool(description = "Gets a calendar by its ID")
    public com.google.api.services.calendar.model.Calendar getCalendar(String calendarId) throws IOException {
        logger.info("Getting calendar with ID: {}", calendarId);
        com.google.api.services.calendar.model.Calendar result = calendarClient.calendars().get(calendarId).execute();
        logger.info("Got calendar: {}", result);
        return result;
    }

    @Tool(description = "Finds a calendar by its name (summary)")
    public com.google.api.services.calendar.model.Calendar findCalendarBySummary(String summary) throws IOException {
        logger.info("Finding calendar by summary: {}", summary);
        for (CalendarListEntry entry : listCalendars()) {
            if (entry.getSummary().equalsIgnoreCase(summary)) {
                logger.info("Found calendar with ID: {}", entry.getId());
                com.google.api.services.calendar.model.Calendar result = calendarClient.calendars().get(entry.getId()).execute();
                logger.info("Found calendar: {}", result);
                return result;
            }
        }
        logger.warn("No calendar found with summary: {}", summary);
        return null;
    }

    @Tool(description = "Creates an event in the calendar given the ID, summary, description, start and end date/time")
    public Event createEvent(String calendarId, String summary, String description, String startDateTime, String endDateTime) throws IOException {
        logger.info("Creating event in calendarId: {} with summary: {}, description: {}, start: {}, end: {}", calendarId, summary, description, startDateTime, endDateTime);
        Event ev = new Event()
                .setSummary(summary)
                .setDescription(description)
                .setStart(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(startDateTime)))
                .setEnd(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(endDateTime)));
        Event result = calendarClient.events().insert(calendarId, ev).execute();
        logger.info("Created event: {}", result);
        return result;
    }

    @Tool(description = "Removes an existing event by providing the calendar and event ID")
    public boolean deleteEvent(String calendarId, String eventId) throws IOException {
        logger.info("Deleting event with ID: {} from calendarId: {}", eventId, calendarId);
        calendarClient.events().delete(calendarId, eventId).execute();
        logger.info("Deleted event with ID: {} from calendarId: {}", eventId, calendarId);
        return true;
    }

    @Tool(description = "Searches for events by summary / keyword within a specified calendar id")
    public List<Event> getEvents(String calendarId, String query) throws IOException {
        logger.info("Searching events in calendarId: {} with query: {}", calendarId, query);
        Events events = calendarClient.events().list(calendarId)
                .setQ(query)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> result = events.getItems();
        logger.info("Found events: {}", result);
        return result;
    }

    @Tool(description = "Searches for events within a specified date range in a calendar")
    public List<Event> getEventsByDateRange(String calendarId, String startDateTime, String endDateTime) throws IOException {
        logger.info("Searching events in calendarId: {} from {} to {}", calendarId, startDateTime, endDateTime);
        Events events = calendarClient.events().list(calendarId)
                .setTimeMin(new com.google.api.client.util.DateTime(startDateTime))
                .setTimeMax(new com.google.api.client.util.DateTime(endDateTime))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> result = events.getItems();
        logger.info("Found events in date range: {}", result);
        return result;
    }

    @Tool(description = "Searches for events by summary / keyword within a specified date range in a calendar")
    public List<Event> getEventsByDateRangeAndKeyword(String calendarId, String startDateTime, String endDateTime, String keyword) throws IOException {
        logger.info("Searching events in calendarId: {} from {} to {} with keyword: {}", calendarId, startDateTime, endDateTime, keyword);
        Events events = calendarClient.events().list(calendarId)
                .setTimeMin(new com.google.api.client.util.DateTime(startDateTime))
                .setTimeMax(new com.google.api.client.util.DateTime(endDateTime))
                .setQ(keyword)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> result = events.getItems();
        logger.info("Found events in date range with keyword: {}", result);
        return result;
    }
}