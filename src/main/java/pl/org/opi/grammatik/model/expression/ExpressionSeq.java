package pl.org.opi.grammatik.model.expression;

import pl.org.opi.grammatik.model.output.Text;
import pl.org.opi.grammatik.model.output.TextFragment;
import pl.org.opi.grammatik.utils.RandomUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionSeq extends ExpressionBase {

    private final List<Expression> expressions;

    public ExpressionSeq(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Expression> expressions() {
        return expressions;
    }

    public Text evaluate() {
        List<TextFragment> fragments = expressions.stream()
                .map(this::evaluate)
                .filter(Objects::nonNull)
                .flatMap(this::fragments)
                .filter(TextFragment::isNotBlank)
                .collect(Collectors.toList());
        return new Text(fragments);
    }

    private Text evaluate(Expression expression) {
        if(RandomUtils.randomDouble() <= expression.probability()) {
            return expression.evaluate();
        } else {
            return null;
        }
    }

    private Stream<TextFragment> fragments(Text parent) {
        List<TextFragment> fragments = parent.getFragments();
        if(fragments.isEmpty()) return Stream.empty();
        if(fragments.size() == 1) return Stream.of(new TextFragment(parent.getLabel(), fragments.get(0).getValue()));
        return fragments.stream().map(fragment -> {
            if(fragment.getLabel() == null && parent.getLabel() != null) {
                return new TextFragment(fragment.getLabel(), fragment.getValue());
            } else {
                return fragment;
            }
        });
    }
}
