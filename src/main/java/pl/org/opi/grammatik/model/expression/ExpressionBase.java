package pl.org.opi.grammatik.model.expression;

public abstract class ExpressionBase implements Expression {

    private String label;

    private Double probability;

    public void setLabel(String label) {
        this.label = label;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    @Override
    public double probability() {
        return probability != null ? probability : 1.0;
    }

    @Override
    public String label() {
        return label;
    }
}
