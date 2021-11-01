/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patrick.delivery.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * To God be the Glory.
 *
 * @author David
 */
public class JsonUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * Checks if a String is a valid json String
     *
     * @param jsonString
     * @return
     */
    public static boolean isValidJsonString(String jsonString) {
        try {
            JsonParser parser = new ObjectMapper().getFactory().createParser(jsonString);
            while (parser.nextToken() != null) {
            }
            return true;
        } catch (JsonParseException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Creates a Json String from <code>this</code> object instance
     *
     * @param object
     * @return
     */
    public static String createJsonFromObject(Object object) {
        return createJsonFromObject(object, false);
    }

    /**
     * Creates a Json String from the passed Object. If indent is true json is
     * idented
     *
     * @param object
     * @param indent
     * @return
     */
    public static String createJsonFromObject(Object object, boolean indent) {
        return createJsonFromObject(object, null, indent);
    }

    /**
     * Creates a Json String from the passed Object. If indent is true json is
     * idented using the <code>viewClass</code> passed.
     *
     * @param <T>
     * @param object
     * @param viewClass
     * @param indent
     * @return
     */
    public static <T extends Views.Limited> String createJsonFromObject(Object object, Class<T> viewClass, boolean indent) {
        String json = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getDefault());
        if (indent) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        try {
            if (null != viewClass) {
                json = mapper.writerWithView(viewClass).writeValueAsString(object);
            } else {
                json = mapper.writeValueAsString(object);
            }
            
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return json;
    }

    /**
     * Method that creates an <code>Object</code> instance from a json
     * <code>String</code>. Uses generics to return the parametrised type
     * <code>T</code> instance.
     *
     * @param <T>
     * @param json
     * @param classInstance
     * @return
     */
    public static <T> T createObjectFromJson(String json, Class<T> classInstance) {
        return createObjectFromJson(json, classInstance, null);
    }

    /**
     * Method that creates an <code>Object</code> instance from a json
     * <code>String</code>. Uses generics to return the parametrised type
     * <code>T</code> instance.
     *
     * @param <T>
     * @param <K>
     * @param json
     * @param classInstance
     * @param viewClass
     * @return
     */
    public static <T, K extends Views.Limited> T createObjectFromJson(String json, Class<T> classInstance, Class<K> viewClass) {
        T object = null;

        ObjectMapper mapper = new ObjectMapper();
        // if there are unknown features in the json not defined in the class then read the 
        // defined ones
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        try {
            if (null != viewClass) {
                object = mapper.readerWithView(viewClass)
                        .forType(classInstance)
                        .readValue(json);
            } else {
                object = mapper.readValue(json, classInstance);
            }

        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return object;
    }

    /**
     * Get the value associated to the passed key from a JSON
     *
     * @param jsonString
     * @return
     */
    public static String getNodeAsString(String jsonString, String key) {
        String tnxType = null;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(jsonString);
            tnxType = node.get(key).asText();
        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tnxType;
    }

    public static JsonNode getNode(String jsonString, String key) {
        JsonNode node = null;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(jsonString);
            node = rootNode.get(key);
        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return node;
    }

    /**
     * Convert a json to Map
     *
     * @param json
     * @return
     * @throws IOException
     */
    public static Map<String, Object> createMapObjectFromJsonString(String json) throws IOException {
        Map<String, Object> data = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);
        data = mapper.readValue(json, mapType);

        return data;
    }

    /**
     * Convert a json to Map
     *
     * @param <K>
     * @param <L>
     * @param <M>
     * @param json
     * @param mapType The type of map to be created e.g. HashMap
     * @param mapKeyType The key type e.g. String.class
     * @param mapValueType The value type e.g. Integer.class
     * @return
     * @throws IOException
     */
    public static <K extends Map, L, M> K createMapObjectFromJsonString(String json, Class<K> mapType, Class<L> mapKeyType, Class<M> mapValueType) throws IOException {
//        Map<String, Object> data = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        MapType typeOfMap = typeFactory.constructMapType(mapType, mapKeyType, mapValueType);

        final K readValue = mapper.readValue(json, typeOfMap);

        return readValue;
    }

    /**
     * Convert a json to Map
     *
     * @param <K>
     * @param <L>
     * @param <M>
     * @param json
     * @param mapType The type of map to be created e.g. HashMap
     * @param mapKeyType The key type e.g. String.class
     * @param mapValueType The value type e.g. Integer.class
     * @return
     * @throws IOException
     */
    public static <K extends Map, L, M> K createMapFromJsonString(String json, Class<K> mapType, Class<L> mapKeyType, Class<M> mapValueType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        MapType typeOfMap = typeFactory.constructMapType(mapType, mapKeyType, mapValueType);

        final K readValue = mapper.readValue(json, typeOfMap);

        return readValue;

//        return data;
    }

    /**
     * Creates a list of <code>T</code> from the jsonString passed
     *
     * @param <T>
     * @param jsonString
     * @param t
     * @return
     */
    public static <T> List<T> createListOfTFromJSONString(String jsonString, Class<T> t) {
        if (jsonString == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final TypeFactory factory = mapper.getTypeFactory();
        final JavaType listOfT = factory.constructCollectionType(List.class, t);
        try {
            List<T> list = mapper.readValue(jsonString, listOfT);
            return list;
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <K, T> K createGenericObjectFromJson(String jsonString, Class<K> k, Class<T> t) {
        if (jsonString == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final TypeFactory factory = mapper.getTypeFactory();
        final JavaType genericType = factory.constructParametricType(k, t);
        try {
            K list = mapper.readValue(jsonString, genericType);
            return list;
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns an object based on the type reference passed
     * @param jsonString
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T createGenericObjectFromJson(String jsonString, TypeReference<T> typeReference) {
        if (jsonString == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            final T t = mapper.readValue(jsonString, typeReference);

            return t;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns a class of type {@code K <T <S>>
     * @param <K>
     * @param <T>
     * @param <S>
     * @param jsonString
     * @param k
     * @param t
     * @param s
     * @return
     */
    public static <K, T, S> K createGenericObjectFromJson(String jsonString, Class<K> k, Class<T> t, Class<S> s) {
        if (jsonString == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final TypeFactory factory = mapper.getTypeFactory();
        JavaType type = mapper.getTypeFactory().constructParametricType(t, s);
        final JavaType genericType = factory.constructParametricType(k, type);
        try {
            K list = mapper.readValue(jsonString, genericType);
            return list;
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts from one value type to another
     * @param obj
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T convertBean(Object obj, Class<T> t) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.convertValue(obj, t);
    }

    /**
     *
     * To God be the Glory.
     *
     * @author David
     */
    public static class Views {

        /**
         * All fields annotated with <code>Limited</code> will be viewed only if
         * the <code>Limited</code> class will be used in serialisation or
         * deserialisation.
         */
        public static class Limited {
        }

        /**
         * When <code>All</code> is used in serialisation or deserialisation,
         * all fields annotated by <code>Limited</code> and <code>All</code>
         * will be serialised or deserialised. Annotate with <code>All</code>
         * any field that needs to be excluded in serialisation/deserialisation
         * coz when using <code>Limited</code> for
         * serialisation/deserialisation, fields annotated with <code>All</code>
         * will be exempted.
         */
        public static class All extends Limited {
        }
    }
}
