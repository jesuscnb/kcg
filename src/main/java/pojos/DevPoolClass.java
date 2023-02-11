package pojos;

import java.util.Set;

public record DevPoolClass(String name,
                           String serializedName,
                           String type,
                           String packageName,
                           Set<DevPoolAttribute> attributes) {
}
