package com.loan.aggregator.task;

import java.util.Map;

import com.loan.aggregator.notification.Notification;

public class SendEmailTaskExecutor implements Runnable {

	private Map<String, Object> dataMap;

	private Notification notification;

	private String appId;

	public SendEmailTaskExecutor(Map<String, Object> dataMap, Notification emailNotification, String appId) {
		this.dataMap = dataMap;
		this.notification = emailNotification;
		this.appId = appId;
	}

	@Override
	public void run() {
		notification.sendNotification(dataMap, appId);

	}

}
