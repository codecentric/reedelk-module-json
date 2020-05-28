package com.reedelk.json.component;

import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void shouldConvertJavaMap() {
        // Given
        component.initialize();

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
        component.initialize();

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
        component.initialize();

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
        component.initialize();

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
        component.initialize();

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
    void shouldConvertPair() {
        // Given
        component.initialize();

        Pair<String, Integer> pair = Pair.create("one", 2);

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .withJavaObject(pair)
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        String actualJson = actual.payload();
        String expectedJson = "{\"left\":\"one\",\"right\":2}";
        assertEquals(expectedJson, actualJson, STRICT);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        // Given
        component.initialize();

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .empty()
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        Object payload = actual.payload();
        assertThat(payload).isNull();
    }

    @Test
    void shouldPrettyPrint() {
        // Given
        component.setPrettyPrint(true);
        component.initialize();

        List<Object> myObject = asList("one", "two", of("three", "four"));

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .withJavaObject(myObject)
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        String actualJson = actual.payload();
        String expectedJson = "[\n" +
                "  \"one\",\n" +
                "  \"two\",\n" +
                "  {\"three\": \"four\"}\n" +
                "]";
        assertEquals(expectedJson, actualJson, STRICT);
    }

    @Test
    void shouldPrettyPrintWithGivenIndentFactor() {
        // Given
        component.setPrettyPrint(true);
        component.setIndentFactor(5);
        component.initialize();

        List<Object> myObject = asList("one", "two", of("three", "four"));

        Message inMessage = MessageBuilder.get(TestComponent.class)
                .withJavaObject(myObject)
                .build();

        // When
        Message actual = component.apply(context, inMessage);

        // Then
        String actualJson = actual.payload();
        String expectedJson = "[\n" +
                "     \"one\",\n" +
                "     \"two\",\n" +
                "     {\"three\": \"four\"}\n" +
                "]";
        assertEquals(expectedJson, actualJson, STRICT);
    }
}
