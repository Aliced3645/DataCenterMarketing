package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.io.IOException;

import net.floodlightcontroller.datacentermarketing.logic.BidRequest;
import net.floodlightcontroller.datacentermarketing.logic.BidResult;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.openflow.protocol.OFMatch;

public class BidRequestJSONSerializer extends JsonSerializer<BidRequest>{

	@Override
	public void serialize(BidRequest bidRequest, JsonGenerator jGen,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		// TODO Auto-generated method stub
		jGen.writeStartObject();
		jGen.writeStringField("Bidder", bidRequest.getBidder().getBidderID());
		jGen.writeNumberField("Value", bidRequest.getBidValue());
		jGen.writeNumberField("SourceHostID", bidRequest.getSourceID());
		jGen.writeNumberField("DestHostID", bidRequest.getDestID());
		jGen.writeEndObject();
		
	}
	
	
    @Override
    public Class<BidRequest> handledType() {
        return BidRequest.class;
    }
}
