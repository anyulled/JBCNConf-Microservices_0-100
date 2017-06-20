package com.gft.jbcnconf.report.event.movement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import com.gft.jbcnconf.report.service.ReportService;
/**
 * Event {@link MovementEvent} processor 
 * @author MOCR
 *
 */
@EnableBinding(Sink.class)
public class MovementEventListener {
	
	private final Logger log = Logger.getLogger(MovementEventListener.class);

	private final ReportService reportService;
	
	@Autowired
	public MovementEventListener (ReportService reportService) {
		this.reportService = reportService;
	}
    /**
     * Message listener 
     * @param message {@link MovementEvent}
     */
	@StreamListener(Sink.INPUT)
	public void process(Message<MovementEvent> message) {
		log.info(" >> NEW MESSAGE >>> " + message);
		Acknowledgment acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
		if (acknowledgment != null) {
			log.info("Acknowledgment provided");
			acknowledgment.acknowledge();
		} 
		processMovementEvent(message.getPayload()); 
	}
	/**
	 * Get message data and call the service to process
	 * @param event {@link MovementEvent}
	 */
	private void processMovementEvent (MovementEvent event) {
		log.info("Process MovementEvent " + event.toString());
		long accountId = event.getEntity().getAccountId();
		double amount = event.getEntity().getAmount();
		String type = event.getEntity().getType().name();
		
		if (event.getType().equals(MovementEventType.CREATED)) { 
			reportService.reportMovement(accountId, amount, type);
		}
	}
}
