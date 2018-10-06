package pl.org.opi.grammatik.model.output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class TextBuilder {

    private String label;

    private List<TextFragment> fragments = new ArrayList<>();

    public TextBuilder(TextFragment... values) {
        this.fragments.addAll(Arrays.asList(values));
    }

    public TextBuilder(Collection<TextFragment> values) {
        this.fragments.addAll(values);
    }

    public TextBuilder label(String value) {
        this.label = value;
        return this;
    }

    public TextBuilder add(String fragment) {
        this.fragments.add(new TextFragment(fragment));
        return this;
    }

    public TextBuilder add(String fragment, String label) {
        this.fragments.add(new TextFragment(label, fragment));
        return this;
    }

    public TextBuilder add(Collection<TextFragment> values) {
        this.fragments.addAll(values);
        return this;
    }

    public TextBuilder add(TextFragment... values) {
        this.fragments.addAll(Arrays.asList(values));
        return this;
    }

    public Text build() {
        Text res = new Text(fragments);
        res.setLabel(label);
        return res;
    }
}
