package com.reedelk.json.component;

import com.reedelk.json.internal.JSONToObjectConverter;
import com.reedelk.json.internal.commons.Preconditions;
import com.reedelk.runtime.api.annotation.ComponentInput;
import com.reedelk.runtime.api.annotation.ComponentOutput;
import com.reedelk.runtime.api.annotation.Description;
import com.reedelk.runtime.api.annotation.ModuleComponent;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessageBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

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

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        Object payload = message.payload();
        if (payload == null) {
            // The payload was null, we return an empty message.
            return MessageBuilder.get(JSONToObject.class)
                    .empty()
                    .build();
        }

        Preconditions.checkIsStringOrThrow(payload);

        Object asJavaObject = converter.toObject((String) payload);

        return MessageBuilder.get(JSONToObject.class)
                .withJavaObject(asJavaObject)
                .build();
    }
}
