package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.io.IOException;

import net.floodlightcontroller.datacentermarketing.logic.BidResult;
import net.floodlightcontroller.datacentermarketing.logic.Bidder;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.openflow.protocol.OFMatch;

public class BidderJSONSerializer extends JsonSerializer<Bidder>{

	@Override
	public void serialize(Bidder bidder, JsonGenerator jGen,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		// TODO Auto-generated method stub
		jGen.writeStartObject();
		jGen.writeStringField("BidderID", bidder.getBidderID());
		jGen.writeEndObject();
		return;
	}
	
    @Override
    public Class<Bidder> handledType() {
        return Bidder.class;
    }
    
    
}
