package pl.org.opi.grammatik.model;

import pl.org.opi.grammatik.model.expression.ExpressionSeq;
import pl.org.opi.grammatik.model.output.Text;
import pl.org.opi.grammatik.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupNode implements Node {

    private final String name;

    private List<ExpressionSeq> entries = new ArrayList<>();

    private double probabilitySum;

    private String label;

    public GroupNode(String name) {
        this.name = name;
    }

    public void addEntry(ExpressionSeq entry) {
        this.entries.add(entry);
        this.probabilitySum += entry.probability();
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<ExpressionSeq> entries() {
        return entries;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Text evaluate(Object... args) {
        double sum = 0;
        double random = RandomUtils.randomDouble(0, probabilitySum);
        int idx = 0;
        ExpressionSeq entry = entries.get(0);
        while(sum < random && idx < entries.size()) {
            entry = entries.get(idx);
            sum += entry.probability();
            idx++;
        }
        Text res = entry.evaluate();
        if(res.getLabel() == null) {
            res.setLabel(label);
        }
        return res;
    }
}
