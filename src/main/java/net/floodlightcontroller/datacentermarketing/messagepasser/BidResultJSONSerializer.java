package net.floodlightcontroller.datacentermarketing.messagepasser;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.datacentermarketing.logic.*;

public class BidResultJSONSerializer extends JsonSerializer<BidResult>{

	@Override
	public void serialize(BidResult bidResult, JsonGenerator jGen,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		jGen.writeStartObject();
		jGen.writeNumberField("Round", bidResult.getRound());
		jGen.writeStringField("BidderID", bidResult.getBidderID());
		jGen.writeStringField("BidResult", bidResult.getAllocationResultInString());
		jGen.writeEndObject();
	}
	
	
	@Override
	public Class<BidResult> handledType(){
		return BidResult.class;
	}
	
}
