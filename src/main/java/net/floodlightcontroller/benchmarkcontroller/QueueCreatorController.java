package net.floodlightcontroller.benchmarkcontroller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openflow.protocol.OFBarrierRequest;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketQueue;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFQueueGetConfigReply;
import org.openflow.protocol.OFQueueGetConfigRequest;
import org.openflow.protocol.OFQueueProp;
import org.openflow.protocol.OFType;
import org.openflow.protocol.OFVendor;
import org.openflow.protocol.OFQueueProp.OFQueuePropType;
import org.openflow.util.U16;
import org.openflow.vendor.openflow.OFOpenFlowVendorData;
import org.openflow.vendor.openflow.OFQueueDeleteVendorData;
import org.openflow.vendor.openflow.OFQueueModifyVendorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

public class QueueCreatorController implements IOFSwitchListener, IOFMessageListener, IFloodlightModule {

    protected IFloodlightProviderService floodlightProvider;
    protected static Logger logger;
    protected int currentQueueNum;
    protected final int TOTAL_QUEUE = 300;
    protected final int QUEUE_PER_PORT = 7;
    protected final int PORT_OFFSET = 1;
    protected final int PORT_MAX = 50;
    protected Boolean cleaned = false;
    protected int currentDeleting = 0;
    
    protected boolean ON_ALL_PORT = false;
    
    protected double lastTime;
    protected BufferedWriter createRecorder;
    protected BufferedWriter deleteRecorder;
    protected String outPathCreate = "/tmp/createQueue.dat";
    protected String outPathDelete = "/tmp/deleteQueue.dat";
    
	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l =
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        logger = LoggerFactory.getLogger(QueueCreatorController.class);
        currentQueueNum = 0;
        try {
        	createRecorder = new BufferedWriter(new FileWriter(outPathCreate));
        	deleteRecorder = new BufferedWriter(new FileWriter(outPathDelete));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO Auto-generated method stub
		floodlightProvider.addOFSwitchListener(this);
        floodlightProvider.addOFMessageListener(OFType.QUEUE_GET_CONFIG_REPLY, this);   
        floodlightProvider.addOFMessageListener(OFType.BARRIER_REPLY, this);   
	}

	private int nextPort(int n) {
		if(!ON_ALL_PORT) {
			return n/QUEUE_PER_PORT + PORT_OFFSET;
		} else {
			return (n%(PORT_MAX - PORT_OFFSET + 1)) + PORT_OFFSET;
		}
	}

	private int nextQueue(int n ) {
		if(!ON_ALL_PORT) {
			return n%QUEUE_PER_PORT;
		} else {
			return n/(PORT_MAX - PORT_OFFSET + 1);
		}
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
        
        logger.info("this message is from switch: " + sw.getId());

        switch (msg.getType()) {
        case QUEUE_GET_CONFIG_REPLY:
            System.out.println("Got a Queue Config Reply!!");
            OFQueueGetConfigReply reply = (OFQueueGetConfigReply) msg;
            List<OFPacketQueue> queues = reply.getQueues();

            System.out.println("This is the port: " + reply.getPortNumber());
            
            System.out.println("Number of queues: " + queues.size());
            
            for (OFPacketQueue queue : queues) {
                long qid = queue.getQueueId();
                List<OFQueueProp> props = queue.getProperties();

                System.out.println("    qid = " + qid);
                System.out.println("    num props = " + props.size());
                
                for (OFQueueProp prop : props) {
                    System.out.println("      type = " + prop.getType());
                    System.out.println("      rate = " + prop.getRate());
                }
                
            }
            
            break;
        case BARRIER_REPLY:
        	if(currentQueueNum == TOTAL_QUEUE) {
//        		synchronized(cleaned) {
//        			if (cleaned == false) {
//        				//stop benchmarking, delete all queues
//        				System.out.println("Wait some time before cleaning up...");
//        				//getAllQueueConfigs(sw);
//        				try {
//        					Thread.sleep(10000);
//        				} catch (InterruptedException e) {
//        					e.printStackTrace();
//        				}
//        				System.out.println("Finished, cleaning up...");
//        				int port;
//        				int queue;
//        				for (int i = 0;i < currentQueueNum;i++) {
//        					port = nextPort(i);
//        					queue = nextQueue(i);
//        					System.out.println("Deleting queue " + queue + " on port " + port + "...");
//        					deleteQueue(sw, (short) port, queue);
//        				}
//        				cleaned = true;
//        			}
//        		}
            	System.out.println("Received Barrier Reply");
				double time = (double)System.nanoTime()/1000000.0;
				try {
					deleteRecorder.write((time - lastTime)+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.err.println(time - lastTime);
            	int nextPort = nextPort(currentDeleting);
            	int nextQueue = nextQueue(currentDeleting);
            	lastTime = (double)System.nanoTime()/1000000.0;
            	System.err.println(nextPort + " " + nextQueue);
            	deleteQueue(sw, (short) nextPort, nextQueue);
            	currentDeleting ++;
            	if (currentDeleting < currentQueueNum) {
            		sendBarrier(sw);
            	} else {
            		System.out.println("all cleaned...");
            		try {
						deleteRecorder.close();
						createRecorder.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
        	} else {
            	System.out.println("Received Barrier Reply");
				double time = (double)System.nanoTime()/1000000.0;
				try {
					createRecorder.write((time - lastTime)+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
            	int nextPort = nextPort(currentQueueNum);
            	int nextQueue = nextQueue(currentQueueNum);
            	lastTime = (double)System.nanoTime()/1000000.0;	
            	createQueue(sw, (short) nextPort, nextQueue, (short) 70);
            	sendBarrier(sw);
        		currentQueueNum ++;
        	}
        	break;
        default:
            System.out.println("unexpected message type: " + msg.getType());

        };
        
        return Command.CONTINUE;
	}

    private void getAllQueueConfigs(IOFSwitch sw) {
        OFQueueGetConfigRequest m = new OFQueueGetConfigRequest();
        
        Collection<OFPhysicalPort> ports = sw.getPorts();
        
        for (OFPhysicalPort port : ports) {
            if (U16.f(port.getPortNumber()) >= U16.f(OFPort.OFPP_MAX.getValue())) {
                continue;
            }
            
            System.out.println("Sending a queue get config to: " + port.getPortNumber());
            
            m.setPortNumber(port.getPortNumber());
            
            try {
                sw.write(m, null);
            } catch (IOException e) {
                logger.error("Tried to write to switch {} but got {}", sw.getId(), e.getMessage());
            } 
        }

        sw.flush();
    }
    
    private void sendBarrier(IOFSwitch sw) {
        OFBarrierRequest m = new OFBarrierRequest();
        try {
            sw.write(m, null);
        } catch (IOException e) {
            logger.error("Tried to write to switch {} but got {}", sw.getId(), e.getMessage());
        }

        sw.flush();
    }

    private void sendOFVendorData(IOFSwitch sw, OFOpenFlowVendorData data) {
        OFVendor msg = (OFVendor) this.floodlightProvider.
                getOFMessageFactory().getMessage(OFType.VENDOR);
        msg.setVendor(OFOpenFlowVendorData.OF_VENDOR_ID);

        msg.setVendorData(data);
        msg.setLengthU(OFVendor.MINIMUM_LENGTH + data.getLength());

        try {
            sw.write(msg, null);
        } catch (IOException e) {
            logger.error("Tried to write to switch {} but got {}", sw.getId(), e.getMessage());
        }

        sw.flush();
    }

    private void createQueue(IOFSwitch sw, short portNumber, int queueId, short rate) {
        OFQueueProp prop = new OFQueueProp();
        prop.setType(OFQueuePropType.OFPQT_MIN_RATE);
        prop.setRate(rate);

        OFPacketQueue queue = new OFPacketQueue(queueId);
        queue.setProperties(new ArrayList<OFQueueProp>(Arrays.asList(prop)));

        OFQueueModifyVendorData queueModifyData = new OFQueueModifyVendorData();
        queueModifyData.setPortNumber(portNumber);
        queueModifyData.setQueues(
                new ArrayList<OFPacketQueue>(Arrays.asList(queue)));

        sendOFVendorData(sw, queueModifyData);
    }

    private void deleteQueue(IOFSwitch sw, short portNumber, int queueId) {
        OFPacketQueue queue = new OFPacketQueue(queueId);

        OFQueueDeleteVendorData queueDeleteData = new OFQueueDeleteVendorData();
        queueDeleteData.setPortNumber(portNumber);
        queueDeleteData.setQueues(
                new ArrayList<OFPacketQueue>(Arrays.asList(queue)));

        sendOFVendorData(sw, queueDeleteData);
    }
    
	@Override
	public void addedSwitch(IOFSwitch sw) {
		// TODO Auto-generated method stub
//        getAllQueueConfigs(sw);
//        sendBarrier(sw);
//
//        deleteQueue(sw, (short) 2, 30);
//        sendBarrier(sw);
//
//        getAllQueueConfigs(sw);
//        sendBarrier(sw);
//
//        createQueue(sw, (short) 2, 30, (short) 70);
//        sendBarrier(sw);
//
//        getAllQueueConfigs(sw);
		sendBarrier(sw);
	}

	@Override
	public void removedSwitch(IOFSwitch sw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchPortChanged(Long switchId) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "QueueCreaterController";
	}

}