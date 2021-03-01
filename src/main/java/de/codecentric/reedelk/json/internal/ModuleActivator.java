package de.codecentric.reedelk.json.internal;

import de.codecentric.reedelk.json.internal.script.GlobalFunctions;
import de.codecentric.reedelk.runtime.api.converter.ConverterService;
import de.codecentric.reedelk.runtime.api.script.ScriptEngineService;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static org.osgi.service.component.annotations.ServiceScope.SINGLETON;

@Component(service = ModuleActivator.class, scope = SINGLETON, immediate = true)
public class ModuleActivator {

    @Reference
    private ScriptEngineService scriptEngine;
    @Reference
    ConverterService converterService;

    @Activate
    public void start(BundleContext context) {
        long moduleId = context.getBundle().getBundleId();
        GlobalFunctions globalFunctions =
                new GlobalFunctions(moduleId,
                        new ObjectToJSONConverter(converterService),
                        new JSONToObjectConverter());
        scriptEngine.register(globalFunctions);
    }
}
