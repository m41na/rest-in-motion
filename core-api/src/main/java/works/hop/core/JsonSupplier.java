package works.hop.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;
import java.util.function.Supplier;

public class JsonSupplier {

    public static Supplier<ObjectMapper> version1 = new Supplier<>() {

        private ObjectMapper mapper;

        {
            this.mapper = new ObjectMapper();
            this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            this.mapper.registerModule(new JavaTimeModule());
            this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        @Override
        public ObjectMapper get() {
            return mapper;
        }
    };

    public static Supplier<ObjectMapper> version2 = new Supplier<ObjectMapper>() {

        private ObjectMapper mapper;

        {
            this.mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sssZ"));
        }

        @Override
        public ObjectMapper get() {
            return mapper;
        }
    };

    public static Supplier<ObjectMapper> xmlVersion = new Supplier<ObjectMapper>() {

        private ObjectMapper mapper;

        {
            this.mapper = new ObjectMapper();
            JacksonXmlModule xmlModule = new JacksonXmlModule();
            xmlModule.setDefaultUseWrapper(false);
            ObjectMapper objectMapper = new XmlMapper(xmlModule);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        @Override
        public ObjectMapper get() {
            return mapper;
        }
    };
}
