package com.reedelk.json.component;

import com.reedelk.json.commons.Messages;
import com.reedelk.runtime.api.annotation.Description;
import com.reedelk.runtime.api.annotation.ModuleComponent;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.ResultRow;
import com.reedelk.runtime.api.message.content.TypedContent;
import com.reedelk.runtime.api.message.content.TypedPublisher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import reactor.core.publisher.Flux;

import java.util.List;


@ModuleComponent("Result Set To JSON")
@Description("Converts a Result Set into a JSON structure.")
@Component(service = ResultSetAsJson.class, scope = ServiceScope.PROTOTYPE)
public class ResultSetAsJson implements ProcessorSync {

    private static final int INDENT_FACTOR = 4;

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        TypedContent<ResultRow, List<ResultRow>> content = message.content();
        TypedPublisher<ResultRow> resultSet = content.stream();

        if (!resultSet.getType().equals(ResultRow.class)) {
            String errorMessage = Messages.ResultSetAsJson.WRONG_ARGUMENT
                    .format(ResultSetAsJson.class.getSimpleName(),
                            ResultRow.class.getSimpleName(),
                            resultSet.getType().getSimpleName());
            throw new IllegalArgumentException(errorMessage);
        }

        try {
            JSONArray convert = convert(resultSet);
            String result = convert.toString(INDENT_FACTOR);

            return MessageBuilder.get()
                    .withJson(result)
                    .build();
        } catch (Throwable exception) {
            throw new ESBException(exception);
        }
    }

    private static JSONArray convert(TypedPublisher<ResultRow> resultSetFlux) throws JSONException {
        JSONArray json = new JSONArray();
        Flux.from(resultSetFlux).subscribe(resultSetRow -> {
            JSONObject rowObject = new JSONObject();
            int numColumns = resultSetRow.columnCount();
            for (int i = 1; i < numColumns + 1; i++) {
                String columnName = resultSetRow.columnName(i);
                rowObject.put(columnName, resultSetRow.get(i));
            }
            json.put(rowObject);
        });
        return json;
    }
}
