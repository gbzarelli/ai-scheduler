package br.com.helpdev.gcalendarmcpclient.controller;

import br.com.helpdev.gcalendarmcpclient.controller.dto.RequestChat;
import br.com.helpdev.gcalendarmcpclient.controller.dto.ResponseChat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {


     @PostMapping("/chat")
     public ResponseChat chat(@RequestBody RequestChat message) {
return null;
     }
}




