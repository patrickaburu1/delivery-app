package com.patrick.delivery.utils.templates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Map;

/**
 * This class will make sure that if there is a single object to
 * transform to JSON, it won't be rendered inside a map.
 * <p>
 * Created by Anthony on 3/14/2017.
 */
public class JsonView extends MappingJackson2JsonView {

    @Override
    public void setObjectMapper(ObjectMapper objectMapper) {
        DefaultSerializerProvider sp = new DefaultSerializerProvider.Impl();

        // Use the custom serializer for null items
        sp.setNullValueSerializer(new NullSerializer());
        objectMapper.setSerializerProvider(sp);

        // To prevent errors on empty beans

//        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        super.setObjectMapper(objectMapper);
    }

    /**
     * @param model
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Object filterModel(Map<String, Object> model) {
        Object data = super.filterModel(model);

        if (!(data instanceof Map))
            return data;

        Map map = (Map) data;

        if (map.size() == 1)
            return map.values().toArray()[0];
        else return map;
    }
}
