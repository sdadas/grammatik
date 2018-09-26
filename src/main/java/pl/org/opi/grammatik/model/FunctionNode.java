package pl.org.opi.grammatik.model;

import pl.org.opi.grammatik.model.expression.FunctionRef;
import pl.org.opi.grammatik.model.output.Text;
import pl.org.opi.grammatik.parser.ParseException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class FunctionNode implements Node {

    private final String name;

    private DataType[] arguments;

    private NativeFunction function;

    public FunctionNode(String name, String[] arguments) {
        this.name = name;
        this.arguments = createArguments(arguments);
    }

    private DataType[] createArguments(String[] arguments) {
        List<DataType> results = new ArrayList<>();
        for (String argument : arguments) {
            DataType type = DataType.getByName(argument);
            if(type == null) {
                String msg = String.format("Invalid argument '%s' for function declaration '%s'", argument, name);
                throw new ParseException(msg);
            }
            results.add(type);
        }
        return results.toArray(new DataType[0]);
    }

    public void setFunction(NativeFunction function) {
        this.function = function;
    }

    public NativeFunction getFunction() {
        return function;
    }

    public boolean isRegistered() {
        return function != null;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Text evaluate(Object... args) {
        return function.invoke(args);
    }

    public DataType[] arguments() {
        return arguments;
    }

    public enum DataType {
        STRING() {
            @Override
            public Object convert(Object value, Class<?> target, FunctionRef ref) {
                if(value == null) return null;
                String str = value.toString();
                if(target.isAssignableFrom(String.class)) {
                    return str;
                } else {
                    throw typeError(str.getClass(), target, ref);
                }
            }
        },
        NUMBER() {
            @Override
            public Object convert(Object value, Class<?> target, FunctionRef ref) {
                if(value == null) return null;
                if(target.isAssignableFrom(value.getClass())) {
                    return value;
                } else if(!(value instanceof BigDecimal)) {
                    throw typeError(value.getClass(), target, ref);
                } else {
                    BigDecimal decimal = (BigDecimal) value;
                    if(target.equals(Integer.class)) {
                        return decimal.intValueExact();
                    } else if(target.equals(Float.class)) {
                        return decimal.floatValue();
                    } else if(target.equals(Double.class)) {
                        return decimal.doubleValue();
                    } else if(target.equals(Short.class)) {
                        return decimal.shortValueExact();
                    } else if(target.equals(Byte.class)) {
                        return decimal.byteValueExact();
                    } else if(target.equals(Long.class)) {
                        return decimal.longValueExact();
                    } else if(target.equals(BigInteger.class)) {
                        return decimal.toBigInteger();
                    } else {
                        throw typeError(BigDecimal.class, target, ref);
                    }
                }

            }
        },
        BOOLEAN() {
            @Override
            public Object convert(Object value, Class<?> target, FunctionRef ref) {
                if(value == null) return null;
                if(target.isAssignableFrom(value.getClass())) {
                    return value;
                } else {
                    throw typeError(value.getClass(), target, ref);
                }
            }
        };

        private static DataType getByName(String name) {
            for (DataType value : DataType.values()) {
                if(value.name().equalsIgnoreCase(name)) {
                    return value;
                }
            }
            return null;
        }

        protected ParseException typeError(Class<?> source, Class<?> target, FunctionRef ref) {
            String format = "type mismatch for method invokation %s, %s != %s";
            String msg = String.format(format, ref.toString(), source.getSimpleName(), target.getSimpleName());
            return new ParseException(msg);
        }

        public abstract Object convert(Object value, Class<?> target, FunctionRef ref);
    }
}
