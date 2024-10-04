package com.loan.aggregator.logmasking;

import java.io.IOException;
import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


public class JSONMaskingUtility  {

	private  PCIPIIProperties pcipiiProperties;

	private static DigestUtils digestUtil = new DigestUtils("SHA-256");
	private static ObjectMapper objectMapper = new ObjectMapper();

	private static final String REGEX = "((?:emails|phoneNumbers)\\\":\\s*\\[[^\\]]+value\\\":\\s*\\\")([^\\\"]+)(\\\"[^\\]]+])";
	private static final String REPLACEMENT = "$1**********$3";

	public  String removePIIData(String content) {
		if (StringUtils.isBlank(content)) {
			return content;
		}
		Map<String, Object> jsonDataMap;
		try {
			jsonDataMap = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {

			});
		} catch (JsonProcessingException e) {
			return "";
		}
		return mapToJsonString(removePIIData(jsonDataMap));

	}

	public  String removePIIData(byte[] content) {
		if (content.length < 1) {
			return "";
		}
		Map<String, Object> jsonDataMap;
		try {
			jsonDataMap = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {

			});
		} catch (IOException e) {
			return "";
		}
		return mapToJsonString(removePIIData(jsonDataMap));
	}

	public  <T> String removePIIData(T content) {
		if (Objects.isNull(content)) {
			return "";
		}
		Map<String, Object> jsonDataMap;
		try {
			jsonDataMap = objectMapper.readValue(objectMapper.writeValueAsBytes(content),
					new TypeReference<Map<String, Object>>() {

					});
		} catch (IOException e) {
			return "";
		}
		return mapToJsonString(removePIIData(jsonDataMap));
	}

	private static String mapToJsonString(Map<String, Object> jsonMap) {
		try {
			return objectMapper.writeValueAsString(jsonMap).replaceAll(REGEX,REPLACEMENT);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	private  Map<String, Object> removePIIData(Map<String, Object> jsonData) {

		Set<String> toBeRemoved = new HashSet<>();
		Set<Map.Entry<String, Object>> toBeHashed = new HashSet<>();

		for (Map.Entry<String, Object> entry : jsonData.entrySet()) {

			if (pcipiiProperties.getRemove().contains(entry.getKey())) {
				toBeRemoved.add(entry.getKey());
				continue;
			}
			if (pcipiiProperties.getHash().contains(entry.getKey())) {
				if (entry.getValue() == null) {
					continue;
				}
				toBeHashed.add(entry);

				continue;

			}

			if (pcipiiProperties.getMask().contains(entry.getKey())) {
				entry.setValue("*********");
				continue;
			}

			if (entry.getValue() instanceof Map) {

				removePIIData((Map<String, Object>) entry.getValue());
			}

			if (entry.getValue() instanceof List) {
				for (Object listKey : (List) entry.getValue()) {
					if (listKey instanceof Map) {
						removePIIData((Map<String, Object>) listKey);
					}
				}

			}

		}

		// Remove the sensitive fields
		for (String key : toBeRemoved) {
			jsonData.remove(key);
		}
		for (Map.Entry<String, Object> entry : toBeHashed) {
			String hashedValue = digestUtil.digestAsHex(entry.getValue().toString());
			jsonData.put(entry.getKey(), hashedValue);

		}

		return jsonData;

	}

	public static <T> String hashString(T content){
		if(Objects.isNull(content)){
			return null;
		}
		return digestUtil.digestAsHex(content.toString().strip());

	}

	public PCIPIIProperties getPcipiiProperties() {
		return pcipiiProperties;
	}

	public void setPcipiiProperties(PCIPIIProperties pcipiiProperties) {
		this.pcipiiProperties = pcipiiProperties;
	}


}
