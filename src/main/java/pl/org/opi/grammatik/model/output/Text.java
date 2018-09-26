package pl.org.opi.grammatik.model.output;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Text implements Serializable {

    private String label;

    private List<TextFragment> fragments = new ArrayList<>();

    public static Text of(String value) {
        return new Text(value, null);
    }

    public static Text of(String value, String label) {
        return new Text(value, label);
    }

    public Text() {
    }

    public Text(String value, String label) {
        this.fragments = Lists.newArrayList(new TextFragment(value));
        this.label = StringUtils.stripToNull(label);
    }

    public Text(List<TextFragment> fragments) {
        this.fragments = fragments;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = StringUtils.stripToNull(label);
    }

    public List<TextFragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<TextFragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public String toString() {
        return Joiner.on(' ').skipNulls().join(fragments);
    }
}
