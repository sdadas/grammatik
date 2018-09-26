package pl.org.opi.grammatik.parser;

import com.google.common.base.Joiner;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;
import pl.org.opi.grammatik.GrammatikBaseListener;
import pl.org.opi.grammatik.GrammatikLexer;
import pl.org.opi.grammatik.GrammatikParser;
import pl.org.opi.grammatik.model.FunctionNode;
import pl.org.opi.grammatik.model.GroupNode;
import pl.org.opi.grammatik.model.NativeFunction;
import pl.org.opi.grammatik.model.expression.*;
import pl.org.opi.grammatik.model.output.Text;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GraphBuilder extends GrammatikBaseListener {

    private final Graph graph = new Graph();

    private final Map<String, GroupNode> tempGroups = new HashMap<>();

    public static GraphBuilder read(InputStream input) throws IOException {
        GrammatikLexer lexer = new GrammatikLexer(CharStreams.fromStream(input, StandardCharsets.UTF_8));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GrammatikParser parser = new GrammatikParser(tokens);
        ParseTree tree = parser.eval();
        ParseTreeWalker walker = new ParseTreeWalker();
        GraphBuilder builder = new GraphBuilder();
        walker.walk(builder, tree);
        return builder;
    }

    @Override
    public void exitEval(GrammatikParser.EvalContext ctx) {
        if(!tempGroups.isEmpty()) {
            String groups = Joiner.on(", ").join(tempGroups.keySet());
            String msg = String.format("there are references to undeclared group names in the file: '%s'", groups);
            throw new ParseException(msg);
        }
    }

    @Override
    public void exitFunctionDeclaration(GrammatikParser.FunctionDeclarationContext ctx) {
        String name = ctx.name().getText();
        String[] args = new String[0];
        GrammatikParser.FunctionArgsContext argsContext = ctx.functionArgs();
        if(argsContext != null) {
            args = argsContext.functionArg().stream().map(RuleContext::getText).toArray(String[]::new);
        }
        FunctionNode node = new FunctionNode(name, args);
        validateFunctionNode(node, ctx.start.getLine());
        graph.putFunction(node);
    }

    private void validateFunctionNode(FunctionNode node, int line) {
        if(graph.getFunction(node.name()) != null) {
            throw new ParseException(line, String.format("function '%s' is already defined", node.name()));
        }
    }

    @Override
    public void exitGroup(GrammatikParser.GroupContext ctx) {
        String name = ctx.name().getText();
        String label = tag(ctx.tagName());
        GroupNode node = tempGroups.remove(name);
        if(node == null) {
            node = new GroupNode(name);
        }
        node.setLabel(label);
        List<GrammatikParser.EntryContext> entries = ctx.entry();
        for (GrammatikParser.EntryContext entryContext : entries) {
            ExpressionSeq entry = createEntry(entryContext);
            node.addEntry(entry);
        }
        validateGroupNode(node, ctx.start.getLine());
        graph.putGroup(node);
    }

    private void validateGroupNode(GroupNode node, int line) {
        if(graph.getGroup(node.name()) != null) {
            throw new ParseException(line, String.format("group '%s' is already defined", node.name()));
        }
    }

    private ExpressionSeq createEntry(GrammatikParser.EntryContext ctx) {
        List<Expression> expressions = new ArrayList<>();
        for (GrammatikParser.ExpressionContext expressionContext : ctx.expression()) {
            Expression expression = createExpression(expressionContext);
            expressions.add(expression);
        }
        ExpressionSeq res = new ExpressionSeq(expressions);
        res.setProbability(createProbability(ctx.probability()));
        return res;
    }

    private Expression createExpression(GrammatikParser.ExpressionContext ctx) {
        if(ctx.constantExpression() != null) {
            return createConstantRef(ctx.constantExpression());
        } else if(ctx.dynamicExpression() != null) {
            GrammatikParser.DynamicExpressionContext dec = ctx.dynamicExpression();
            if(dec.functionInvokation() != null) {
                return createFunctionRef(ctx);
            } else if(dec.name() != null) {
                return createGroupRef(ctx);
            } else if(dec.constantExpression() != null) {
                return createDynamicConstantRef(ctx);
            }
        }
        String msg = String.format("Unknown expression type '%s'", ctx.getText());
        throw new ParseException(ctx.start.getLine(), ctx.start.getCharPositionInLine(), msg);
    }

    private ConstantRef createConstantRef(GrammatikParser.ConstantExpressionContext ctx) {
        return new ConstantRef(string(ctx.STRING()));
    }

    private ConstantRef createDynamicConstantRef(GrammatikParser.ExpressionContext ctx) {
        GrammatikParser.DynamicExpressionContext dec = ctx.dynamicExpression();
        GrammatikParser.ConstantExpressionContext cec = dec.constantExpression();
        ConstantRef result = new ConstantRef(string(cec.STRING()));
        result.setProbability(createProbability(dec.probability()));
        result.setLabel(tag(dec.tagName()));
        return result;
    }

    private GroupRef createGroupRef(GrammatikParser.ExpressionContext ctx) {
        GrammatikParser.DynamicExpressionContext dec = ctx.dynamicExpression();
        String name = dec.name().getText();
        GroupNode node = getOrCreateGroup(name);
        GroupRef result = new GroupRef(node);
        result.setProbability(createProbability(dec.probability()));
        result.setLabel(tag(dec.tagName()));
        return result;
    }

    private GroupNode getOrCreateGroup(String name) {
        GroupNode group = graph.getGroup(name);
        if(group != null) return group;
        group = tempGroups.get(name);
        if(group != null) return group;
        group = new GroupNode(name);
        tempGroups.put(group.name(), group);
        return group;
    }

    private FunctionRef createFunctionRef(GrammatikParser.ExpressionContext ctx) {
        GrammatikParser.DynamicExpressionContext dec = ctx.dynamicExpression();
        GrammatikParser.FunctionInvokationContext fic = dec.functionInvokation();
        String name = fic.name().getText();
        FunctionNode node = graph.getFunction(name);
        if(node == null) {
            String msg = String.format("referencing undeclared function '%s'", name);
            throw new ParseException(fic.start.getLine(), fic.start.getCharPositionInLine(), msg);
        }
        Object[] args = createFunctionArgs(fic.functionValues());
        FunctionNode.DataType[] nodeArgs = node.arguments();
        if(nodeArgs.length != args.length) {
            String msg = String.format("wrong number of arguments shoud be %d, is %d", nodeArgs.length, args.length);
            throw new ParseException(fic.start.getLine(), fic.start.getCharPositionInLine(), msg);
        }
        FunctionRef result = new FunctionRef(node, args);
        result.setProbability(createProbability(dec.probability()));
        result.setLabel(tag(dec.tagName()));
        return result;
    }

    private Object[] createFunctionArgs(GrammatikParser.FunctionValuesContext ctx) {
        if(ctx == null) return new Object[0];
        List<Object> results = new ArrayList<>();
        for (GrammatikParser.FunctionValueContext fvc : ctx.functionValue()) {
            results.add(createFunctionArg(fvc));
        }
        return results.toArray();
    }

    private Object createFunctionArg(GrammatikParser.FunctionValueContext fvc) {
        if(fvc.BOOLEAN() != null) {
            return Boolean.valueOf(fvc.BOOLEAN().getText());
        } else if(fvc.STRING() != null) {
            return string(fvc.STRING());
        } else if(fvc.DECIMAL() != null) {
            return new BigDecimal(fvc.DECIMAL().getText());
        } else if(fvc.NULL() != null) {
            return null;
        } else {
            String msg = String.format("unknown argument data type '%s'", fvc.getText());
            throw new ParseException(fvc.start.getLine(), fvc.start.getCharPositionInLine(), msg);
        }
    }

    private Double createProbability(GrammatikParser.ProbabilityContext probabilityContext) {
        Double probability = null;
        if(probabilityContext != null) {
            String proba = probabilityContext.DECIMAL().getText();
            probability = Double.parseDouble(proba);
        }
        return probability;
    }

    private String string(TerminalNode node) {
        return StringUtils.strip(node.getText(), "\"");
    }

    private String tag(GrammatikParser.TagNameContext ctx) {
        if(ctx == null) return null;
        return StringUtils.stripToNull(string(ctx.constantExpression().STRING()));
    }

    public void registerMethod(Object context, String methodName, String declaredName, Class<?>... params) {
        this.graph.registerMethod(context, methodName, declaredName, params);
    }

    public void registerMethod(String declaredName, Supplier<Text> lambda) {
        registerMethod(lambda, "get", declaredName);
    }

    public <T> void registerMethod(String declaredName, Function<T, Text> lambda, Class<T> param) {
        Method method = graph.method(lambda, "apply", Object.class);
        NativeFunction nf = new NativeFunction(lambda, method, param);
        graph.registerMethod(nf, declaredName);
    }

    public <T1, T2> void registerMethod(String declaredName, BiFunction<T1, T2, Text> lambda,
                                        Class<T1> param1, Class<T2> param2) {
        Method method = graph.method(lambda, "apply", Object.class, Object.class);
        NativeFunction nf = new NativeFunction(lambda, method, param1, param2);
        graph.registerMethod(nf, declaredName);
    }

    public Graph build() {
        validateBuild();
        validateFunctionRefs();
        return this.graph;
    }

    private void validateBuild() {
        Collection<FunctionNode> functions = this.graph.getFunctions().values();
        List<String> unregistered = functions.stream()
                .filter(node -> !node.isRegistered())
                .map(FunctionNode::name)
                .collect(Collectors.toList());
        if(!unregistered.isEmpty()) {
            String list = Joiner.on(", ").join(unregistered);
            String msg = String.format("some functions have been declared but not registered: %s", list);
            throw new ParseException(msg);
        }
    }

    private void validateFunctionRefs() {
        tempGroups.values().stream()
                .flatMap(group -> group.entries().stream())
                .flatMap(entry -> entry.expressions().stream())
                .filter(expression -> expression instanceof FunctionRef)
                .map(expression -> (FunctionRef) expression)
                .forEach(FunctionRef::validate);
    }
}
