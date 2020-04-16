package com.reedelk.json.component;

import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.DataRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.reedelk.runtime.api.commons.ImmutableMap.of;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

@ExtendWith(MockitoExtension.class)
class ObjectToJSONTest {

    @Mock
    private ConverterService converterService;
    @Mock
    private FlowContext context;

    private ObjectToJSON component = new ObjectToJSON();

    @BeforeEach
    void setUp() {
        lenient().doAnswer(invocation -> invocation.getArgument(0))
                .when(converterService)
                .convert(any(String.class), eq(String.class));

        component.converterService = converterService;
    }

    @Test
    void shouldConvertDataRowStream() {
        // Given
        DataRow<Serializable> row1 = TestDataRow.create(asList("id","name"), asList(4, "John Doe"));
        DataRow<Serializable> row2 = TestDataRow.create(asList("id","name"), asList(3, "Mark Luis"));

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .withStream(Flux.just(row1, row2), DataRow.class)
                .build();

        // When
        Message outMessage = component.apply(context, inMessage);

        // Then
        String json = outMessage.payload();
        String expected = "[\n" +
                "    {\n" +
                "        \"name\": \"John Doe\",\n" +
                "        \"id\": 4\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Mark Luis\",\n" +
                "        \"id\": 3\n" +
                "    }\n" +
                "]";
        assertEquals(expected, json, STRICT);
    }

    @Test
    void shouldConvertDataRow() {
        // Given
        DataRow<Serializable> row = TestDataRow.create(asList("id","name"), asList(4, "John Doe"));

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .withJavaObject(row)
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        String actualJson = actual.payload();
        String expectedJson = "{\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"id\": 4\n" +
                "}";
        assertEquals(expectedJson, actualJson, STRICT);
    }

    @Test
    void shouldConvertJavaMap() {
        // Given
        Map<String,Object> myObject = of("key1", "value1", "key2", "value2");

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .withJavaObject(myObject)
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        String actualJson = actual.payload();
        String expectedJson = "{\n" +
                "    \"key1\": \"value1\",\n" +
                "    \"key2\": \"value2\"\n" +
                "}";
        assertEquals(expectedJson, actualJson, STRICT);
    }

    @Test
    void shouldConvertNestedJavaMap() {
        // Given
        Map<String,Object> myObject =
                of("key1", "value1", "key2", of("key3", "value3"));

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .withJavaObject(myObject)
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        String actualJson = actual.payload();
        String expectedJson = "{\n" +
                "    \"key1\": \"value1\",\n" +
                "    \"key2\": {\"key3\": \"value3\"}\n" +
                "}";
        assertEquals(expectedJson, actualJson, STRICT);
    }

    @Test
    void shouldConvertList() {
        // Given
        List<String> myObject = asList("one", "two", "three");

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .withJavaObject(myObject)
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        String actualJson = actual.payload();
        String expectedJson = "[\n" +
                "    \"one\",\n" +
                "    \"two\",\n" +
                "    \"three\"\n" +
                "]";
        assertEquals(expectedJson, actualJson, STRICT);
    }

    @Test
    void shouldConvertNestedList() {
        // Given
        List<Object> myObject = asList("one", "two", asList("three", "four", "five"));

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .withJavaObject(myObject)
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        String actualJson = actual.payload();
        String expectedJson = "[\n" +
                "    \"one\",\n" +
                "    \"two\",\n" +
                "    [\n" +
                "        \"three\",\n" +
                "        \"four\",\n" +
                "        \"five\"\n" +
                "    ]\n" +
                "]";
        assertEquals(expectedJson, actualJson, STRICT);
    }

    @Test
    void shouldConvertNestedListWithMap() {
        // Given
        List<Object> myObject = asList("one", "two", of("three", "four", "five", of("six", 6)));

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .withJavaObject(myObject)
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        String actualJson = actual.payload();
        String expectedJson = "[\n" +
                "    \"one\",\n" +
                "    \"two\",\n" +
                "    {\n" +
                "        \"three\": \"four\",\n" +
                "        \"five\": {\"six\": 6}\n" +
                "    }\n" +
                "]";
        assertEquals(expectedJson, actualJson, STRICT);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        // Given
        Message inMessage = MessageBuilder.get(TestComponent.class)
                .empty()
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        Object payload = actual.payload();
        assertThat(payload).isNull();
    }
}
