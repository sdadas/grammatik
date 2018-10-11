package pl.org.opi.grammatik.test;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import pl.org.opi.grammatik.model.output.Text;
import pl.org.opi.grammatik.model.output.TextFragment;
import pl.org.opi.grammatik.parser.Graph;
import pl.org.opi.grammatik.parser.GraphBuilder;
import pl.org.opi.grammatik.utils.RandomUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sławomir Dadas
 */
public class GraphBuilderTest {

    @Test
    public void exampleRestaurants() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("restaurants.txt");
        GraphBuilder builder = GraphBuilder.read(is);
        builder.registerMethod(this, "city", "city", String.class);
        builder.registerMethod("time", () -> Text.of(String.valueOf(RandomUtils.randomInt(1, 10)) + " pm"));
        Graph graph = builder.build();
        graph.samples("findRestaurants", 100).forEachRemaining(System.out::println);
        is.close();
    }

    public Text city(String location) {
        String[] cities = new String[]{"new york", "atlanta", "san francisco", "paris", "rome", "berlin", "barcelona"};
        if("usa".equalsIgnoreCase(location)) cities = ArrayUtils.subarray(cities, 0, 3);
        else if("europe".equalsIgnoreCase(location)) cities = ArrayUtils.subarray(cities, 3, cities.length);
        return Text.builder(new TextFragment("city", RandomUtils.randomChoice(cities))).build();
    }
}
