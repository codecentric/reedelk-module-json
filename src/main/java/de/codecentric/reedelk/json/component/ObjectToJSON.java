package de.codecentric.reedelk.json.component;

import de.codecentric.reedelk.json.internal.ObjectToJSONConverter;
import de.codecentric.reedelk.json.internal.commons.Defaults;
import de.codecentric.reedelk.json.internal.commons.IsValid;
import de.codecentric.reedelk.json.internal.exception.ObjectToJSONException;
import de.codecentric.reedelk.runtime.api.annotation.*;
import de.codecentric.reedelk.runtime.api.component.ProcessorSync;
import de.codecentric.reedelk.runtime.api.converter.ConverterService;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import de.codecentric.reedelk.runtime.api.message.MessageBuilder;
import de.codecentric.reedelk.json.internal.commons.Messages;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.util.Optional;

@ModuleComponent("Object to JSON")
@ComponentOutput(
        attributes = MessageAttributes.class,
        payload = String.class,
        description = "A JSON serialized string of the input Object.")
@ComponentInput(
        payload = Object.class,
        description = "An object structure to be converted to JSON string.")
@Description("Converts a Java Object into a JSON string. " +
        "A Java List is mapped to a JSON Array and a Java Map is mapped to a Java Map. " +
        "Any other Java object is mapped using getters. " +
        "If pretty print is set to true, the output JSON is pretty printed using the given indent factor which " +
        "adds a number of spaces to each level of indentation.")
@Component(service = ObjectToJSON.class, scope = ServiceScope.PROTOTYPE)
public class ObjectToJSON implements ProcessorSync {

    @Property("Pretty print")
    @Example("true")
    @DefaultValue("false")
    @Description("If true the output JSON is pretty printed using the given indent factor.")
    private Boolean prettyPrint;

    @Property("Indent")
    @Hint("4")
    @Example("2")
    @DefaultValue("2")
    @Description("The number of spaces to add to each level of indentation. " +
            "If indent factor is 0 JSON object has only one key, " +
            " then the object will be output on a single line: <code>{ {\"key\": 1}}</code>")
    @When(propertyName = "prettyPrint", propertyValue = "true")
    private Integer indentFactor;

    @Reference
    ConverterService converterService;

    private int theIndentFactor;
    private boolean isPrettyPrint;
    private ObjectToJSONConverter converter;

    @Override
    public void initialize() {
        isPrettyPrint = Optional.ofNullable(prettyPrint).orElse(Defaults.PRETTY);
        theIndentFactor = Optional.ofNullable(indentFactor).orElse(Defaults.INDENT_FACTOR);
        converter = new ObjectToJSONConverter(converterService);
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        Object payload = message.payload();
        if (payload == null) {
            // The payload was null, we return an empty message.
            return MessageBuilder.get(JSONToObject.class)
                    .empty()
                    .build();

        } else if (payload instanceof String) {
            // We check that it is a valid JSON.
            String input = (String) payload;
            if (IsValid.json(input)) {
                return MessageBuilder.get(JSONToObject.class)
                        .withJson(input)
                        .build();
            } else {
                throw new ObjectToJSONException(Messages.ObjectToJSON.NOT_JSON_STRING.format());
            }

        } else {
            Object result = converter.toJSON(payload);
            String json = print(result);
            return MessageBuilder.get(ObjectToJSON.class)
                    .withJson(json)
                    .build();
        }
    }

    public void setPrettyPrint(Boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public void setIndentFactor(Integer indentFactor) {
        this.indentFactor = indentFactor;
    }

    private String print(Object result) {
        if (result instanceof JSONObject) {
            JSONObject outObject = (JSONObject) result;
            return isPrettyPrint ?
                    outObject.toString(theIndentFactor) :
                    outObject.toString();
        } else if (result instanceof JSONArray) {
            JSONArray outArray = (JSONArray) result;
            return isPrettyPrint ?
                    outArray.toString(theIndentFactor) :
                    outArray.toString();

        } else {
            // A JSON is valid if and only if the Root is an array or an object.
            throw new ObjectToJSONException(Messages.ObjectToJSON.NOT_VALID_JSON_OBJECT.format(result == null ? null : result.getClass()));
        }
    }
}
