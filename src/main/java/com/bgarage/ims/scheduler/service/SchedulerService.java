package com.bgarage.ims.scheduler.service;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bgarage.ims.scheduler.models.ProcessScheduledOrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SchedulerService {

	@Value("${scheduler.time.window.start}")
	private String timeWindowStartStr;

	@Value("${scheduler.time.window.end}")
	private String timeWindowEndStr;

	@Autowired
	private EventPublisher eventPublisher;

	@Value("${bgarage.kafka.topic.process-schedule-order}")
	private String processOrderTopic;

	@Scheduled(cron = "${scheduler.cron.expression}")
	public void triggerScheduledOrders() {
		LocalTime now = LocalTime.now();
		LocalTime timeWindowStart = LocalTime.parse(timeWindowStartStr);
		LocalTime timeWindowEnd = LocalTime.parse(timeWindowEndStr);

		if (isWithinTimeWindow(now, timeWindowStart, timeWindowEnd)) {
			try {
				ProcessScheduledOrderEvent event = new ProcessScheduledOrderEvent();
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonString = objectMapper.writeValueAsString(event);
				eventPublisher.publishEvent(processOrderTopic, jsonString);
			} catch (Exception e) {
				throw new RuntimeException("ProcessScheduledOrderListener");
			}
		}
	}

	private boolean isWithinTimeWindow(LocalTime now, LocalTime start, LocalTime end) {
		if (start.isBefore(end)) {
			return now.isAfter(start) && now.isBefore(end);
		} else {
			return now.isAfter(start) || now.isBefore(end);
		}
	}
}
