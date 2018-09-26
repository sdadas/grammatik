package pl.org.opi.grammatik.parser;

import pl.org.opi.grammatik.model.FunctionNode;
import pl.org.opi.grammatik.model.GroupNode;
import pl.org.opi.grammatik.model.NativeFunction;
import pl.org.opi.grammatik.model.output.Text;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Sławomir Dadas
 */
public class Graph implements Serializable {

    private Map<String, FunctionNode> functions = new HashMap<>();

    private Map<String, GroupNode> groups = new HashMap<>();

    Map<String, FunctionNode> getFunctions() {
        return functions;
    }

    FunctionNode getFunction(String name) {
        return functions.get(name);
    }

    void putFunction(FunctionNode node) {
        functions.put(node.name(), node);
    }

    GroupNode getGroup(String name) {
        return groups.get(name);
    }

    void putGroup(GroupNode node) {
        groups.put(node.name(), node);
    }

    void registerMethod(Object context, String methodName, String declaredName, Class<?>... params) {
        Method method = method(context, methodName, params);
        NativeFunction nf = new NativeFunction(context, method, params);
        registerMethod(nf, declaredName);
    }

    void registerMethod(NativeFunction nf, String declaredName) {
        FunctionNode node = functions.get(declaredName);
        if(node == null) {
            String msg = String.format("registering undeclared function '%s'", declaredName);
            throw new ParseException(msg);
        }
        node.setFunction(nf);
    }

    Method method(Object context, String methodName, Class<?>... params) {
        try {
            Method method = context.getClass().getDeclaredMethod(methodName, params);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            String className = context.getClass().getSimpleName();
            String msg = String.format("no such method '%s' on object '%s'", methodName, className);
            throw new ParseException(msg, e);
        }
    }

    public Text sample(String  root) {
        GroupNode node = groups.get(root);
        if(node == null) throw new IllegalStateException(String.format("No such group '%s'", root));
        return node.evaluate();
    }

    public Iterator<Text> samples(String root) {
        return samples(root, null);
    }

    public Iterator<Text> samples(String root, Integer limit) {
        return new SampleIterator(root, limit);
    }

    private class SampleIterator implements Iterator<Text> {

        private final String root;

        private final Integer limit;

        private int current;

        public SampleIterator(String root, Integer limit) {
            this.root = root;
            this.limit = limit;
        }

        @Override
        public boolean hasNext() {
            return limit == null || current < limit;
        }

        @Override
        public Text next() {
            current++;
            return Graph.this.sample(this.root);
        }
    }
}
