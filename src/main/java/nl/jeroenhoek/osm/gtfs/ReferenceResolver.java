package nl.jeroenhoek.osm.gtfs;

import nl.jeroenhoek.osm.gtfs.RecordKnowledgeBase.ClassDescriptor;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReferenceResolver {
    private final RecordKnowledgeBase recordKnowledgeBase;

    public ReferenceResolver(RecordKnowledgeBase recordKnowledgeBase) {
        this.recordKnowledgeBase = recordKnowledgeBase;
    }

    public <I1, R1, I2, R2> void resolveBidirectionalReferences(Map<I1, R1> left, Map<I2, R2> right) {
        if (left.isEmpty() || right.isEmpty()) return;

        Set<Class<?>> typesLeft = left.values().stream()
                .map(Object::getClass)
                .collect(Collectors.toSet());
        Set<Class<?>> typesRight = right.values().stream()
                .map(Object::getClass)
                .collect(Collectors.toSet());

        for (Map.Entry<I1, R1> entry : left.entrySet()) {
            //I1 id = entry.getKey();
            R1 record = entry.getValue();
            // Bullshit.
            @SuppressWarnings("unchecked")
            Class<R1> clazz = (Class<R1>) record.getClass();
            ClassDescriptor<R1> descriptor = recordKnowledgeBase.inspect(clazz);
            for (Class<?> referenceClazz : typesRight) {
//                Reference<I2, R2> reference = descriptor.getReferenceTo(record, referenceClazz);
//                if (reference != null && !reference.isResolved()) {
//                    I2 refId = reference.getId();
//                    R2 referred = right.get(refId);
//
//                }
            }


            Object id = descriptor.getId(record);
            System.out.println(id.toString());
        }

    }

    public <I1, R1, I2, R2> void resolveReferenceGraph(Map<I1, R1> left, Map<I2, R2> right) {

    }
}
