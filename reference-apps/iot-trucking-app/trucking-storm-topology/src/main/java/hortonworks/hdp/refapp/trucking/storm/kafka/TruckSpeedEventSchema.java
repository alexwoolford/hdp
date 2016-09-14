package hortonworks.hdp.refapp.trucking.storm.kafka;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;


public class TruckSpeedEventSchema implements Scheme{

	private static final long serialVersionUID = -2990121166902741545L;

	private static final Logger LOG = LoggerFactory.getLogger(TruckSpeedEventSchema.class);
	

	@Override
	public List<Object> deserialize(ByteBuffer buffer) {
		try {
			String[] pieces = convertRawEvent(buffer.array());
			
			Timestamp eventTime = Timestamp.valueOf(pieces[0]);
			int truckId = Integer.valueOf(pieces[1]);
			int driverId = Integer.valueOf(pieces[2]);
			String driverName = pieces[3];
			int routeId = Integer.valueOf(pieces[4]);
			String routeName = pieces[5];
			int speed = Integer.valueOf(pieces[6]);
			
			if(LOG.isTraceEnabled()) {
				LOG.trace("Creating a Truck Scheme with driverId["+driverId + "], driverName["+driverName+"], routeId["+routeId+"], routeName["+ routeName +"], "
						+ "and speed["+speed +"]");				
			}

			
			return new Values(driverId, truckId, eventTime, driverName, routeId, routeName, speed);
			
		} catch (Exception e) {
			LOG.error("Error serializeing truck event in Kafka Spout",  e);
			return null;
		}
		
	}


	private String[] convertRawEvent(byte[] bytes) throws Exception {
		String truckEvent = new String(bytes, "UTF-8");
		
		if(LOG.isTraceEnabled()) {
			LOG.trace("Raw Truck Event is: " + truckEvent);
		}
	
		String initialPieces[] = truckEvent.split("DIVIDER") ;
		String[] pieces = initialPieces[1].split("\\|");
		return pieces;
	}



	@Override
	public Fields getOutputFields() {
		return new Fields("driverId", "truckId", "eventTime", "driverName", "routeId", "routeName", "truckSpeed");
		
	}
	


}