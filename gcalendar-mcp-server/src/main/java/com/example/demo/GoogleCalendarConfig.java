package com.example.demo;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class GoogleCalendarConfig {

    private static final String APPLICATION_NAME = "AgendaApp";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    @Bean
    public Calendar googleCalendarService() throws Exception {
        final var in = this.getClass().getResourceAsStream("/credentials.json");
        assert in != null;
        final var credentials = ServiceAccountCredentials.fromStream(in)
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
