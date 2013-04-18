package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.io.IOException;

import net.floodlightcontroller.datacentermarketing.logic.BiddingClock;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class ClockJSONSerializer extends JsonSerializer<BiddingClock>{

	@Override
	public void serialize(BiddingClock clock, JsonGenerator jGen,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		// TODO Auto-generated method stub
		jGen.writeStartObject();
		jGen.writeNumberField("Current Time", (long)clock.getCurrentTime());
		jGen.writeEndObject();
		
	}
	
	

}
