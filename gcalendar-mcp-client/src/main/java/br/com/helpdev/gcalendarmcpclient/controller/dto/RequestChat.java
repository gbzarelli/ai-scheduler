package br.com.helpdev.gcalendarmcpclient.controller.dto;

public record RequestChat(String toClient, String fromRequester, String message) {
}