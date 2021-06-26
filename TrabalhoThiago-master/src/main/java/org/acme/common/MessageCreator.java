package org.acme.common;

import io.vertx.core.json.JsonObject;

public class MessageCreator {

    public static JsonObject createMessage(String code, String message){
        JsonObject messageCreated = new JsonObject();
        messageCreated.put("error_code", code);
        messageCreated.put("error_msg", message);
        return messageCreated;
    }
}
