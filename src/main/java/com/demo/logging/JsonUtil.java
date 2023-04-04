package com.demo.logging;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@Component("JsonUtil")
public class JsonUtil {
	
//	@Value("${custom.application.url}")
//	private String m_appURL;
	
//	@Autowired
//    public void setURL(String a_appURL){
//		JsonUtil.m_appURL = a_appURL;
//    }

	public static ObjectMapper getObjectMapper() {
		return getObjectMapper(true);
	}

	public static ObjectMapper getObjectMapper(boolean a_setDateFormat) {
		String w_dateFormat = null;
		if (a_setDateFormat) {
			w_dateFormat = "yyyy-MM-dd";
		}

		return getObjectMapper(w_dateFormat);
	}

	public static ObjectMapper getObjectMapper(String a_dateFormat) {
		ObjectMapper objectMapper = new ObjectMapper();

		// 1. Set date format
		if (a_dateFormat != null) {
			DateFormat df = new SimpleDateFormat(a_dateFormat);
			objectMapper.setDateFormat(df);
		}

		// 2. set FAIL_ON_UNKNOWN_PROPERTIES
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 3. set visibility
		objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

		return objectMapper;
	}

	public static ObjectNode getObjectNode() {

		ObjectMapper mapper = getObjectMapper();
		return mapper.createObjectNode();
	}

	public static String getAjaxResponseMessage(boolean a_success, String a_payLoad, String a_errorMessage,
			String a_JWT) {
		ObjectMapper mapper = getObjectMapper();
		JsonNode childNode1 = mapper.createObjectNode();
		((ObjectNode) childNode1).put("success", a_success);
		((ObjectNode) childNode1).put("payload", (a_payLoad != null) ? a_payLoad : "");
		((ObjectNode) childNode1).put("error", (a_errorMessage != null) ? a_errorMessage : "");

		if (a_JWT != null) {
			((ObjectNode) childNode1).put("jwt", a_JWT);
		}

		String jsonString = null;
		try {
			jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(childNode1);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return jsonString;
	}

	public static String getAjaxResponseMessage(boolean a_success, String a_payLoad, String a_errorMessage)
			throws Exception {
		return getAjaxResponseMessage(a_success, a_payLoad, a_errorMessage, null);
	}

	public static String getAjaxResponseMessage(boolean a_success, Object a_payLoad, String a_errorMessage)
			throws Exception {
		return getAjaxResponseMessage(a_success, a_payLoad, a_errorMessage, null);
	}

	public String getWebHookMessage(boolean a_success, String a_payLoad, String a_errorMessage, String a_entityType, String a_eventType, String a_appURL)
			throws Exception {
		ObjectMapper mapper = getObjectMapper();

		JsonNode childNode1 = mapper.createObjectNode();

		((ObjectNode) childNode1).put("success", a_success);
		((ObjectNode) childNode1).put("payload", a_payLoad);
		((ObjectNode) childNode1).put("error", (a_errorMessage != null) ? a_errorMessage : "");
		((ObjectNode) childNode1).put("entitytype", a_entityType);
		((ObjectNode) childNode1).put("event_type", a_eventType);
		((ObjectNode) childNode1).put("appurl", "");

		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(childNode1);

		return jsonString;
	}
	
	@SuppressWarnings("deprecation")
	public static String getAjaxResponseMessage(boolean a_success, Object a_payLoad, String a_errorMessage,
			String a_JWT) {
		ObjectMapper mapper = getObjectMapper();

		JsonNode childNode1 = mapper.createObjectNode();

		((ObjectNode) childNode1).put("success", a_success);
		((ObjectNode) childNode1).put("payload", mapper.convertValue(a_payLoad, JsonNode.class));
		((ObjectNode) childNode1).put("error", (a_errorMessage != null) ? a_errorMessage : "");

		if (a_JWT != null) {
			((ObjectNode) childNode1).put("jwt", a_JWT);
		}

		String jsonString = null;
		try {
			jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(childNode1);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		return jsonString;
	}
	
	public static Map<String, String> getMapFromJSONString(String a_string) {
		Map<String, String> w_retMap = null;

		ObjectMapper mapper = getObjectMapper();
		try {
			w_retMap = mapper.readValue(a_string, new TypeReference<Map<String, String>>() {
			});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		return w_retMap;
	}

	public static String getJSONString(String a_key, Object a_entityList) {
		return getJSONString(a_key, a_entityList, true);
	}

	public static String getJSONString(String a_key, Object a_entityList, boolean a_setDateFormat) {
		ObjectMapper mapper = getObjectMapper(a_setDateFormat);
		JsonNode childNode1 = mapper.createObjectNode();
		((ObjectNode) childNode1).putPOJO(a_key, a_entityList);
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(childNode1);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getJSONString(HashMap<String, Object> a_entityMap) {
		ObjectMapper mapper = getObjectMapper();
		JsonNode childNode1 = mapper.createObjectNode();
		for (Entry<String, Object> w_entry : a_entityMap.entrySet()) {
			((ObjectNode) childNode1).putPOJO(w_entry.getKey(), w_entry.getValue());
		}
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(childNode1);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getJSONStringFromString(HashMap<String, String> a_entityMap) {
		ObjectMapper mapper = getObjectMapper();
		JsonNode childNode1 = mapper.createObjectNode();
		for (Entry<String, String> w_entry : a_entityMap.entrySet()) {
			((ObjectNode) childNode1).putPOJO(w_entry.getKey(), w_entry.getValue());
		}
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(childNode1);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getJSONStringFromObject(Object a_object) {
		ObjectWriter ow = getObjectMapper().writer().withDefaultPrettyPrinter();
		String json = null;
		try {
			json = ow.writeValueAsString(a_object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return json;
	}

	public static String getJSONStringFromObject1(Object a_object) {
		ObjectWriter ow = getObjectMapper().writer();
		String json = null;
		try {
			json = ow.writeValueAsString(a_object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return json;
	}

	public static <T> T getObjectForReadFromJSON(String a_jsonStr, Class<T> a_class, String a_dateFormat,
			T a_entityData) {
		ObjectMapper mapper = getObjectMapper(a_dateFormat);
		if (a_entityData == null) {
			try {
				return mapper.readValue(a_jsonStr, a_class);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}

		ObjectReader objectReader = mapper.readerForUpdating(a_entityData);
		try {
			return objectReader.readValue(a_jsonStr);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T getObjectFromJSON(String a_jsonStr, Class<T> a_class, String a_dateFormat) {
		return getObjectForReadFromJSON(a_jsonStr, a_class, a_dateFormat, null);
	}

	public static <T> T getObjectFromJSON(String a_jsonStr, Class<T> a_class) {
		return getObjectFromJSON(a_jsonStr, a_class, "yyyy-MM-dd");
	}

	public static <T> T fromJSON(TypeReference<T> a_type, String a_jsonString) {
		T data = null;

		try {
			data = getObjectMapper(false).readValue(a_jsonString, a_type);
		} catch (Exception e) {
			// Handle the problem
		}
		return data;
	}

//	public static <T> T getObjectListFromCSV(File a_inputFile, TypeReference<T> a_type) {
//
//		List<Map<?, ?>> w_data = readObjectsFromCsv(a_inputFile);
//
//		String w_jsonString = getJSONStringFromObject(w_data);
//
//		return fromJSON(a_type, w_jsonString);
//	}

	public static String getJSONStringWithFilter(String a_key, Object a_entityList, String[] a_ignorableFieldList,
			String a_className) {

		HashMap<String, Object> w_map = new HashMap<String, Object>();
		w_map.put(a_key, a_entityList);

		return getJSONStringWithFilter(w_map, a_ignorableFieldList, a_className);
	}

	public static String getJSONStringWithFilter(HashMap<String, Object> a_entityMap, String[] a_ignorableFieldList,
			String a_className) {
		FilterProvider filters = new SimpleFilterProvider().addFilter(a_className,
				SimpleBeanPropertyFilter.serializeAllExcept(a_ignorableFieldList));

		ObjectMapper mapper = getObjectMapper();

		ObjectWriter writer = mapper.writer(filters);

		JsonNode childNode1 = mapper.createObjectNode();
		for (Entry<String, Object> w_entry : a_entityMap.entrySet()) {
			((ObjectNode) childNode1).putPOJO(w_entry.getKey(), w_entry.getValue());
		}

		try {
			return writer.writeValueAsString(childNode1);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("deprecation")
//	private static List<Map<?, ?>> readObjectsFromCsv(File a_file) throws IOException {
//		CsvSchema bootstrap = CsvSchema.emptySchema().withHeader();
//		CsvMapper csvMapper = new CsvMapper();
//		MappingIterator<Map<?, ?>> mappingIterator = csvMapper.reader(Map.class).with(bootstrap).readValues(a_file);
//
//		return mappingIterator.readAll();
//	}

	public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
		Iterator<String> fieldNames = updateNode.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode jsonNode = mainNode.get(fieldName);
			// if field exists and is an embedded object
			if (jsonNode != null && jsonNode.isObject()) {
				merge(jsonNode, updateNode.get(fieldName));
			} else {
				if (mainNode instanceof ObjectNode) {
					// Overwrite field
					JsonNode value = updateNode.get(fieldName);
					((ObjectNode) mainNode).replace(fieldName, value);
				}
			}
		}
		return mainNode;
	}

	public static String getAttribValueAsString(JsonNode a_objectNode) {
		String w_retValue = null;
		if (a_objectNode.getNodeType() == JsonNodeType.NUMBER) {
			w_retValue = String.valueOf(a_objectNode.asInt());
		} else if (a_objectNode.getNodeType() == JsonNodeType.STRING) {
			w_retValue = a_objectNode.asText();
		}
		return w_retValue;
	}

	public static String getEntityKey(JsonNode a_attributeList, String[] a_uniqueAtrribList, String a_separator) {
		String w_key = "";
		int i = 0;
		for (String w_attrib : a_uniqueAtrribList) {
			JsonNode w_attribNode = a_attributeList.get(w_attrib);
			String w_attribValue = "";
			if (w_attribNode != null) {
				w_attribValue = JsonUtil.getAttribValueAsString(w_attribNode);
			}
			if (i == 0)
				w_key = w_attribValue;
			else
				w_key += a_separator + w_attribValue;

			i++;
		}
		return w_key;
	}
}
