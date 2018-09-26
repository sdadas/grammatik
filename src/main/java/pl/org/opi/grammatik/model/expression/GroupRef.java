package pl.org.opi.grammatik.model.expression;

import pl.org.opi.grammatik.model.GroupNode;
import pl.org.opi.grammatik.model.output.Text;

public class GroupRef extends ExpressionBase {

    private final GroupNode node;

    public GroupRef(GroupNode node) {
        this.node = node;
    }

    @Override
    public Text evaluate() {
        Text res = node.evaluate();
        if(label() != null) res.setLabel(label());
        return res;
    }
}
