package pl.org.opi.grammatik.model;

import pl.org.opi.grammatik.model.output.Text;

import java.io.Serializable;

public interface Node extends Serializable {

    String name();

    Text evaluate(Object... args);
}
