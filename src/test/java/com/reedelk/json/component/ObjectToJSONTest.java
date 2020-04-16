package com.reedelk.json.component;

import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.DataRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.io.Serializable;

import static java.util.Arrays.asList;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

@ExtendWith(MockitoExtension.class)
class ObjectToJSONTest {

    @Mock
    private FlowContext context;

    private ObjectToJSON component = new ObjectToJSON();

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
}
