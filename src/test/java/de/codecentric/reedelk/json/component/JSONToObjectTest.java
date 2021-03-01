package de.codecentric.reedelk.json.component;

import de.codecentric.reedelk.json.internal.exception.JSONToObjectException;
import de.codecentric.reedelk.runtime.api.commons.ImmutableMap;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.MessageBuilder;
import de.codecentric.reedelk.runtime.api.message.content.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static de.codecentric.reedelk.runtime.api.commons.ImmutableMap.of;
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
    void shouldConvertJSONObjectAsBytesArray() {
        // Given
        byte[] input = "{'one':'one value','two': { 'three': 'three value' } }".getBytes();
        Message message = MessageBuilder.get(TestComponent.class).withBinary(input).build();

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
