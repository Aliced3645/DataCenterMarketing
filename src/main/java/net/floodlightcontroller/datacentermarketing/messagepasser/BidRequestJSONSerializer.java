package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.floodlightcontroller.datacentermarketing.logic.BidRequest;
import net.floodlightcontroller.datacentermarketing.logic.BidResult;
import net.floodlightcontroller.datacentermarketing.logic.Resource;

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
		HashMap<Resource, Long> requiredResources = bidRequest.getRequiredResources();
		//traverse the hashmap
		Set<Entry<Resource, Long>> resourceSet = requiredResources.entrySet();
		for(Entry<Resource,Long> entry : resourceSet){
			Resource name = entry.getKey();
			switch(name){
				case DATA:
					jGen.writeNumberField("Data", entry.getValue());
				case MIN_RATE:
					jGen.writeNumberField("Min Rate", entry.getValue());
				case MAX_RATE:
					jGen.writeNumberField("Max Rate", entry.getValue());
				case START_TIME:
					jGen.writeNumberField("Start Time", entry.getValue());
				case END_TIME:
					jGen.writeNumberField("End Time", entry.getValue());
				case LATENCY:
					jGen.writeNumberField("Latency", entry.getValue());
				default:
					continue;
			}
		}
		jGen.writeEndObject();
	}
	
    @Override
    public Class<BidRequest> handledType() {
        return BidRequest.class;
    }
}
