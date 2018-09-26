package pl.org.opi.grammatik.model.output;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class TextFragment implements Serializable {

    private final String label;

    private final String value;

    public TextFragment(String label, String value) {
        this.label = StringUtils.stripToNull(label);
        this.value = value;
    }

    public TextFragment(String value) {
        this.value = value;
        this.label = null;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public boolean isNotBlank() {
        return StringUtils.isNotBlank(value);
    }

    public Text asText() {
        return Text.of(this.value, this.label);
    }

    @Override
    public String toString() {
        if(StringUtils.isNotBlank(label)) {
            return String.format("<%s>%s</%s>", label, value, label);
        } else {
            return value;
        }
    }
}
