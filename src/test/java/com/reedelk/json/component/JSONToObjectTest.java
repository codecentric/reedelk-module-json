package com.reedelk.json.component;

import com.reedelk.json.internal.exception.JSONToObjectException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        assertThat(payload).isNotNull();

        assertThat(payload).isEqualTo(of("one", "one value", "two", "two value"));
    }

    @Test
    void shouldThrowExceptionWhenJSONIsNotValid() {
        // Given
        String input = "one':'one value','two':'two value'}";
        Message message = MessageBuilder.get(TestComponent.class).withJson(input).build();

        // When
        JSONToObjectException thrown =
                assertThrows(JSONToObjectException.class, () -> component.apply(context, message));

        assertThat(thrown).hasMessage("Not a valid JSON");
    }
}
