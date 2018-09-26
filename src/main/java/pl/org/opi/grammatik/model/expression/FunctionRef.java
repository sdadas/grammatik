package pl.org.opi.grammatik.model.expression;

import com.google.common.base.Joiner;
import pl.org.opi.grammatik.model.FunctionNode;
import pl.org.opi.grammatik.model.NativeFunction;
import pl.org.opi.grammatik.model.output.Text;
import pl.org.opi.grammatik.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionRef extends ExpressionBase {

    private final FunctionNode node;

    private final Object[] args;

    private Object[] realArgs;

    public FunctionRef(FunctionNode node, Object[] args) {
        this.node = node;
        this.args = args;
    }

    public void validate() {
        NativeFunction nf = node.getFunction();
        Class<?>[] realTypes = nf.getParams();
        FunctionNode.DataType[] declaredTypes = node.arguments();
        if(declaredTypes.length != realTypes.length) {
            String format = "registerd method '%s' has wrong number of arguments, shoud be %d";
            throw new ParseException(String.format(format, node.name(), declaredTypes.length));
        }

        List<Object> converted = new ArrayList<>();
        for (int i = 0; i < declaredTypes.length; i++) {
            Object param = declaredTypes[i].convert(args[i], realTypes[i], this);
            converted.add(param);
        }
        this.realArgs = converted.toArray();
    }

    @Override
    public Text evaluate() {
        if(realArgs == null) validate();
        Text res = node.evaluate(realArgs);
        if(label() != null) res.setLabel(label());
        return res;
    }

    @Override
    public String toString() {
        List<String> params = Arrays.stream(args).map(this::argToString).collect(Collectors.toList());
        String paramsJoinder = Joiner.on(",").join(params);
        return String.format("%s(%s)", node.name(), paramsJoinder);
    }

    private String argToString(Object arg) {
        if(arg == null) return "null";
        else if(arg instanceof CharSequence) return String.format("\"%s\"", arg);
        else return arg.toString();
    }
}
