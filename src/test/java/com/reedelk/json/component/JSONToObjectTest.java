package com.reedelk.json.component;

import com.reedelk.json.internal.exception.JSONToObjectException;
import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.reedelk.runtime.api.commons.ImmutableMap.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class JSONToObjectTest {

    @Mock
    private FlowContext context;

    private JSONToObject component = new JSONToObject();

    @BeforeEach
    void setUp() {
        component.initialize();
    }

    @Test
    void shouldConvertJSONArrayToList() {
        // Given
        String input = "[{'one':'one value','two':'two value'}]";
        Message message = MessageBuilder.get(TestComponent.class).withJson(input).build();

        // When
        Message actual = component.apply(context, message);

        // Then
        List<Map<String,Object>> payload = actual.payload();
        assertThat(payload).isNotNull();

        Map<String, Object> object = payload.get(0);
        assertThat(object).isEqualTo(of("one", "one value", "two", "two value"));
    }

    @Test
    void shouldConvertJSONObjectToMap() {
        // Given
        String input = "{'one':'one value','two':'two value'}";
        Message message = MessageBuilder.get(TestComponent.class).withJson(input).build();

        // When
        Message actual = component.apply(context, message);

        // Then
        Map<String,Object> payload = actual.payload();
        assertThat(payload).isEqualTo(of("one", "one value", "two", "two value"));
    }

    @Test
    void shouldConvertJSONObjectWithArray() {
        // Given
        String input = "{'one':'one value','two': [20, 55, 32] }";
        Message message = MessageBuilder.get(TestComponent.class).withJson(input).build();

        // When
        Message actual = component.apply(context, message);

        // Then
        Map<String,Object> payload = actual.payload();
        assertThat(payload).isEqualTo(of("one", "one value", "two", Arrays.asList(20, 55, 32)));
    }

    @Test
    void shouldConvertJSONObjectWithJSONObject() {
        // Given
        String input = "{'one':'one value','two': { 'three': 'three value' } }";
        Message message = MessageBuilder.get(TestComponent.class).withJson(input).build();

        // When
        Message actual = component.apply(context, message);

        // Then
        Map<String,Object> payload = actual.payload();
        assertThat(payload).isEqualTo(of("one", "one value", "two", ImmutableMap.of("three", "three value")));
    }

    @Test
    void shouldReturnNullPayloadWhenInputIsNull() {
        // Given
        Message message = MessageBuilder.get(TestComponent.class).empty().build();

        // When
        Message actual = component.apply(context, message);

        // Then
        Object payload = actual.payload();
        assertThat(payload).isNull();
    }

    @Test
    void shouldThrowExceptionWhenJSONIsNotValid() {
        // Given
        String input = "one':'one value','two':'two value'}";
        Message message = MessageBuilder.get(TestComponent.class).withJson(input).build();

        // When
        JSONToObjectException thrown =
                assertThrows(JSONToObjectException.class, () -> component.apply(context, message));

        assertThat(thrown).hasMessage("The JSON cannot be parsed, cause=[Token 'one'' was not expected].");
    }

    @Test
    void shouldThrowExceptionWhenPayloadIsNotString() {
        // Given
        Pair<String,String> input = Pair.create("key", "value");
        Message message = MessageBuilder.get(TestComponent.class).withJavaObject(input).build();

        // When
        JSONToObjectException thrown =
                assertThrows(JSONToObjectException.class, () -> component.apply(context, message));

        assertThat(thrown).hasMessage(
                "The message payload of type (SerializablePair) is not a string. " +
                "Only a payload containing a string type can be converted to JSON.");
    }
}
