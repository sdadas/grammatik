package pl.org.opi.grammatik.model.expression;

import pl.org.opi.grammatik.model.output.Text;

public class ConstantRef extends ExpressionBase {

    private final Text value;

    public ConstantRef(String value) {
        this.value = Text.of(value);
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
        this.value.setLabel(label);
    }

    @Override
    public Text evaluate() {
        return value;
    }
}
