package com.loan.aggregator.notification;

import java.util.Map;

public interface Notification {
	void sendNotification(Map<String, Object> mapData, String appId);

}
