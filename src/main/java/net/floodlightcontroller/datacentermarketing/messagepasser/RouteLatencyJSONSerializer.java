package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class RouteLatencyJSONSerializer extends JsonSerializer<RouteLatency>
{

    @Override
    public void serialize(RouteLatency rtlt, JsonGenerator jGen,
	    SerializerProvider arg2) throws IOException,
	    JsonProcessingException
    {
	// TODO Auto-generated method stub
	jGen.writeStartObject();
	jGen.writeStringField("Delay of Route", rtlt.toString());
	jGen.writeEndObject();

    }

}
