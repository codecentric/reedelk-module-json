package de.codecentric.reedelk.json.component;

import de.codecentric.reedelk.json.internal.JSONToObjectConverter;
import de.codecentric.reedelk.json.internal.commons.Preconditions;
import de.codecentric.reedelk.runtime.api.annotation.ComponentInput;
import de.codecentric.reedelk.runtime.api.annotation.ComponentOutput;
import de.codecentric.reedelk.runtime.api.annotation.Description;
import de.codecentric.reedelk.runtime.api.annotation.ModuleComponent;
import de.codecentric.reedelk.runtime.api.component.ProcessorSync;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import de.codecentric.reedelk.runtime.api.message.MessageBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import java.nio.charset.StandardCharsets;

@ModuleComponent("JSON to Object")
@ComponentOutput(
        attributes = MessageAttributes.class,
        payload = Object.class,
        description = "An Object structure representing the given JSON string")
@ComponentInput(
        payload = { String.class },
        description = "The JSON string to be converted to an Object structure.")
@Description("Converts a JSON string into a Java Object. " +
        "A JSON object is mapped to a Java Map and a JSON array is mapped to a Java List. " +
        "A null payload produces a null output payload and an exception is thrown if the input is not a String " +
        "or a not valid JSON.")
@Component(service = JSONToObject.class, scope = ServiceScope.PROTOTYPE)
public class JSONToObject implements ProcessorSync {

    private JSONToObjectConverter converter;

    @Override
    public void initialize() {
        converter = new JSONToObjectConverter();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Message apply(FlowContext flowContext, Message message) {

        Object payload = message.payload();
        if (payload == null) {
            // The payload was null, we return an empty message.
            return MessageBuilder.get(JSONToObject.class)
                    .empty()
                    .build();
        }

        if (payload instanceof byte[] || payload instanceof Byte[]) {
            // We convert the payload to string. This is to avoid
            // having the user con convert byte array to string.
            // We convert using UTF-8, because a JSON is UTF-8 encoded.
            payload = convertAsStringUTF8((byte[]) payload);
        }

        Preconditions.checkIsStringOrThrow(payload);

        Object asJavaObject = converter.toObject((String) payload);

        return MessageBuilder.get(JSONToObject.class)
                .withJavaObject(asJavaObject)
                .build();
    }

    // TODO: This method should be a generic method in the converter service.
    //  the converter service should allow to convert to a string with a given charset.
    //  when created also the PayloadToString component should use the same (with streams).
    @Deprecated
    private String convertAsStringUTF8(byte[] payload) {
        return new String(payload, StandardCharsets.UTF_8);
    }
}
