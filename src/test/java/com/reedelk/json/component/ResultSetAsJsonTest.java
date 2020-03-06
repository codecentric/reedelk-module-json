package com.reedelk.json.component;

import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.ResultRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResultSetAsJsonTest {

    @Mock
    private FlowContext mockFlowContext;

    private ResultSetAsJson component = new ResultSetAsJson();

    @Test
    void shouldCorrectlyConvertToJson() {
        // Given
        ResultRow row1 = TestResultRow.create(asList("id","name"), asList(4, "John Doe"));
        ResultRow row2 = TestResultRow.create(asList("id","name"), asList(3, "Mark Luis"));
        Flux<ResultRow> result = Flux.just(row1, row2);
        Message inMessage = MessageBuilder.get()
                .withStream(result, ResultRow.class)
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

        assertThat(thrown).hasMessage("ResultSetAsJson component " +
                "expects message with payload of " +
                "type=[ResultRow] but type=[String] was given.");
    }

    static class TestResultRow implements ResultRow {

        private final List<String> columns;
        private final List<Object> values;

        static ResultRow create(List<String> columns, List<Object> values) {
            return new TestResultRow(columns, values);
        }

        private TestResultRow(List<String> columns, List<Object> values) {
            this.columns = columns;
            this.values = values;
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
        public Object get(int i) {
            return values.get(i - 1);
        }

        @Override
        public Object getByColumnName(String columnName) {
            for (int i = 1; i <= columns.size(); i++) {
                if (columns.get(i).equals(columnName)) {
                    return i;
                }
            }
            throw new ESBException("Could not find column with name: " + columnName);
        }

        @Override
        public List<Object> row() {
            return Collections.unmodifiableList(values);
        }
    }
}
