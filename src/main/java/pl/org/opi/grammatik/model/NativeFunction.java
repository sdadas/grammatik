package pl.org.opi.grammatik.model;

import pl.org.opi.grammatik.model.output.Text;

import java.lang.reflect.Method;

/**
 * @author Sławomir Dadas
 */
public class NativeFunction {

    private final Object context;

    private final Method method;

    private final Class<?>[] params;

    public NativeFunction(Object context, Method method, Class<?>... params) {
        this.context = context;
        this.method = method;
        this.params = params;
    }

    public Class<?>[] getParams() {
        return params;
    }

    public Text invoke(Object... args) {
        try {
            return (Text) method.invoke(this.context, args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
