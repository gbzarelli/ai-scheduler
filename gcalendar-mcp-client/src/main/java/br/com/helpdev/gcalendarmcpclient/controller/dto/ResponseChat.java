package br.com.helpdev.gcalendarmcpclient.controller.dto;

public record ResponseChat(String toClient, String fromRequester, String message) {
}