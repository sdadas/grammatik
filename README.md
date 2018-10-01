## Grammatik

A library for language generation for NLP tasks such as chatbots, sequence tagging, text classification, information extraction etc. Tools similar to Grammatik already exist (i.g. [Chatito](https://github.com/rodrigopivi/Chatito)). However, this library is intended for more advanced use cases, with the ability to write custom extensions for the grammar and with a strict control of probability of generated text fragments.

### Dependency

Currently, you can declare this library as a maven / gradle / sbt dependency directly from github using [JitPack.io](https://jitpack.io/) serivce. Add JitPack to your project repositories:

```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

Next, add a dependency to the tagged release on github:

```xml
<dependency>
  <groupId>com.github.sdadas</groupId>
  <artifactId>grammatik</artifactId>
  <version>0.1</version>
</dependency>
```

### Usage

Create a grammar file defininig a set of rules for the language generator. 

```markdown
# declaring external functions
declare city(string);
declare time();

# group declaration with default label "restaurant"
# group can have one or more entries that generator chooses by random
# entry can contain static strings in quotes or dynamically evaluated values
# you can assign a probability to dynamic value i.g. ${greet[0.5]} will occur only half of the times
findRestaurants:"restaurant" {
  ${greet[0.5]} ${"please"[0.2]} ${"find"[0.5]} ${restaurants} ${place} ${time[0.2]};
  "i'm" ${"very"[0.05]} "hungry at" ${city(null)}[0.1];
}

# you can boost or decrease probability of an entry
# i.g. ${located} ${city("usa")}[2] is 2 times more probable than other options
place {
  ${located} ${city("usa")}[2];
  ${located} ${city("europe")};
  "near my home";
  "close to me";
}

located {
  "in";
  "in the area of";
  "located in";
}

greet {
  "hey";
  "hi";
  "hello";
  "greeting";
}

restaurants {
  "restaurants";
  "places to eat";
  "where to eat";
}

time {
  "that opens" ${"at noon":"opening_time"}[0.1];
  "opening at" ${time():"opening_time"};
  "open until" ${time():"closing_time"};
}
```

Feed this file to `GraphBuilder` class.
You can also implement custom methods in Java that may be referenced in definitions file. 
In this example we register `city()` and `time()` functions declared earlier.
Create a `Graph` object and generate some samples by calling `sample(definitionRoot)` or `samples(definitionRoot, numberOrRows)`.

```java
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
        return Text.of(RandomUtils.randomChoice(cities), "city");
    }
}
```

The above example generates output such as:

```xml
please find places to eat located in <city>new york</city>
greeting restaurants near my home
hello find where to eat near my home
please find where to eat in <city>new york</city> opening at <opening_time>5 pm</opening_time>
find restaurants close to me
hi please places to eat in the area of <city>berlin</city>
please find restaurants in <city>barcelona</city>
greeting find places to eat in the area of <city>atlanta</city> open until <closing_time>7 pm</closing_time>
please restaurants in the area of <city>new york</city>
places to eat close to me
(...)
```

### Features

#### Function declarations and bindings

You can declare an external function in the grammar using `declare` statement e.g. `declare personName(string,number);`. Three types of arguments are supported: `string`, `boolean`, and `number`  that correspond to `String`, `Boolean` and various numeric Java classes - in the case of `number`, an argument can be converted to `Integer`, `Long`, `Short`, `BigDecimal`, `BigInteger`, `Double`, `Float` or `Byte`. Any declared function can be invoked inside entry definition e.g. `"hi, my name is" ${personName("firstName", 123)};`. In order to use the grammar with functions, one needs to register function binding in Java before creating a sample generator. Either method name or lambda expression may be passed as a binding.

```java
GraphBuilder builder = GraphBuilder.read(is);
builder.registerMethod(context, "myMethod", "personName", String.class, Double.class);
```

Where `context` is the object on which the method will be invoked, `"myMethod"` is the name of the method, `"personName"` is the name of function declared in the grammar. Last two params are the real argument types for the method.

#### Controlling probability

A fine-grained control of generation probabilities is possible in Grammatik. When probability is used inside entry definition e.g. `${greet[0.5]}`, it allows to include the `greet` fragment optionally, with a probability of 0.5. One can define a probability multiplier at the end of an entry e.g. `${located} ${city("usa")}[2];`. In this case, it applies to the probability of chosing this entry in relation to other entries in the same group. Each entry in a group has a sample weight of 1, using a multiplier of [2] means that this entry will be sampled two times as often as any other entry. Likewisie, using a multiplier of [0.1] means that the entry is ten times less probable than other entries.
