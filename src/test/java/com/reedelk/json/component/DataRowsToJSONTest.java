package com.reedelk.json.component;

import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.exception.PlatformException;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DataRowsToJSONTest {

    @Mock
    private FlowContext mockFlowContext;

    private DataRowsToJSON component = new DataRowsToJSON();

    @Test
    void shouldCorrectlyConvertToJson() {
        // Given
        DataRow<Serializable> row1 = TestDataRow.create(asList("id","name"), asList(4, "John Doe"));
        DataRow<Serializable> row2 = TestDataRow.create(asList("id","name"), asList(3, "Mark Luis"));
        Flux<DataRow> result = Flux.just(row1, row2);
        Message inMessage = MessageBuilder.get()
                .withStream(result, DataRow.class)
                .build();

        // When
        Message outMessage = component.apply(mockFlowContext, inMessage);

        // Then
        String json = outMessage.payload();
        assertThat(json).isEqualTo("[\n" +
                "    {\n" +
                "        \"name\": \"John Doe\",\n" +
                "        \"id\": 4\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Mark Luis\",\n" +
                "        \"id\": 3\n" +
                "    }\n" +
                "]");
    }

    @Test
    void shouldThrowCorrectExceptionWhenPayloadDoesNotHaveCorrectType() {
        // Given
        Flux<String> result = Flux.just("one", "two");
        Message inMessage = MessageBuilder.get()
                .withStream(result, String.class)
                .build();

        // When
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> component.apply(mockFlowContext, inMessage));

        assertThat(thrown).hasMessage(DataRowsToJSON.class.getSimpleName() + " component " +
                "expects message with payload of " +
                "type=[DataRow] but type=[String] was given.");
    }

    static class TestDataRow implements DataRow<Serializable> {

        private final List<String> columns;
        private final List<Serializable> values;

        static DataRow<Serializable> create(List<String> columns, List<Serializable> values) {
            return new TestDataRow(columns, values);
        }

        private TestDataRow(List<String> columns, List<Serializable> values) {
            this.columns = columns;
            this.values = values;
        }

        @Override
        public Map<String, Serializable> attributes() {
            return ImmutableMap.of();
        }

        @Override
        public Serializable attribute(String name) {
            return null;
        }

        @Override
        public int columnCount() {
            return columns.size();
        }

        @Override
        public String columnName(int i) {
            return columns.get(i - 1);
        }

        @Override
        public List<String> columnNames() {
            return columns;
        }

        @Override
        public Serializable get(int i) {
            return values.get(i - 1);
        }

        @Override
        public Serializable getByColumnName(String columnName) {
            for (int i = 1; i <= columns.size(); i++) {
                if (columns.get(i).equals(columnName)) {
                    return i;
                }
            }
            throw new PlatformException("Could not find column with name: " + columnName);
        }

        @Override
        public List<Serializable> values() {
            return Collections.unmodifiableList(values);
        }
    }
}
