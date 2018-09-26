package pl.org.opi.grammatik.model.expression;

import pl.org.opi.grammatik.model.output.Text;

public interface Expression {

    Text evaluate();

    double probability();

    String label();
}
