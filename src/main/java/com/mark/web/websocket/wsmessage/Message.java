package com.mark.web.websocket.wsmessage;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access=AccessLevel.PACKAGE)
public class Message implements TextMessage{
    String from;
    String to;
    String messageContent;
    MessageType messageType;
}
