package com.easyminning.mongodbclient.serializer;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CustomDateDeserializer extends JsonDeserializer<Date>{

  @Override
  public Date deserialize(JsonParser arg0, DeserializationContext arg1)
      throws IOException, JsonProcessingException {
    //System.out.println("deserialize");
    Date date = null;
    if(JsonToken.VALUE_STRING.equals(arg0.getCurrentToken()))
    {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      try {
        date = formatter.parse(arg0.getText());
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return date;
  }
}
