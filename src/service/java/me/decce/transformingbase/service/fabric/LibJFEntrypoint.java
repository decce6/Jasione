package me.decce.transformingbase.service.fabric;

//? fabric {
//? >=26 {
/*import dev.jfronny.libjf.unsafe.asm.AsmConfig;
import dev.jfronny.libjf.unsafe.asm.patch.Patch;
*///? } else {
import io.gitlab.jfronny.libjf.unsafe.asm.AsmConfig;
import io.gitlab.jfronny.libjf.unsafe.asm.patch.Patch;
//? }
import me.decce.transformingbase.service.transform.CommonTransformer;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

public class LibJFEntrypoint implements AsmConfig {
    @Override
    public Set<String> skipClasses() {
        return null;
    }

    @Override
    public Set<Patch> getPatches() {
        return Set.of(createPatch());
    }

    private Patch createPatch() {
        try {
            var method = Patch.class.getMethod("apply", ClassNode.class);
            if (method.getReturnType() == boolean.class) {
                return createDirectPatch();
            }
            else {
                return createProxyPatch(method);
            }
        } catch (NoSuchMethodException ignored) {
            // The API has probably changed. Try our best here (will work for renaming).
            return createDirectPatch();
        }
    }

    private Patch createDirectPatch() {
        return CommonTransformer::process;
    }

    private Patch createProxyPatch(Method applyMethod) {
        // Required for compatibility with older LibJF versions. See https://github.com/decce6/Jasione/issues/16
        return (Patch) Proxy.newProxyInstance(
                Patch.class.getClassLoader(),
                new Class[] { Patch.class },
                new LibJFInvocationHandler(applyMethod)
        );
    }

    private static final class LibJFInvocationHandler implements InvocationHandler {
        private final Method applyMethod;

        private LibJFInvocationHandler(Method applyMethod) {
            this.applyMethod = applyMethod;
            // Make sure we don't create any classloading in the invoke method to prevent StackOverflowError's
            CommonTransformer.init();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isDefault()) {
                return InvocationHandler.invokeDefault(proxy, method, args);
            }
            if (applyMethod.equals(method)) {
                return CommonTransformer.process((ClassNode) args[0]);
            }
            throw new IllegalStateException();
        }
    }
}
//? }