package nl.jeroenhoek.osm.gtfs;

import nl.jeroenhoek.osm.gtfs.annotation.Id;
import nl.jeroenhoek.osm.gtfs.annotation.ReferenceId;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public enum RecordKnowledgeBase {
    INSTANCE;

    Map<Class<?>, ClassDescriptor<?>> cache;

    RecordKnowledgeBase() {
        this.cache = new HashMap<>();
    }

    public <T> ClassDescriptor<T> inspect(Class<T> clazz) {
        ClassDescriptor<T> descriptor;
        if (cache.containsKey(clazz)) {
            @SuppressWarnings("unchecked")
            ClassDescriptor<T> descriptorUc = (ClassDescriptor<T>) cache.get(clazz);
            descriptor = descriptorUc;
        } else {
            descriptor = ClassDescriptor.inspect(clazz);
            cache.put(clazz, descriptor);
        }

        return descriptor;
    }

    public static class ClassDescriptor<T> {
        Class<T> clazz;
        MethodHandle idGetter = null;


        private ClassDescriptor() {
            // No-op.
        }

        public Class<T> getClazz() {
            return clazz;
        }

        public MethodHandle getIdGetter() {
            return idGetter;
        }

        static <T> ClassDescriptor<T> inspect(Class<T> clazz) {
            ClassDescriptor<T> descriptor = new ClassDescriptor<>();
            descriptor.clazz = clazz;

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                lookForIdAnnotation(descriptor, field);
                lookForReferenceAnnotation(descriptor, field);
            }

            if (descriptor.idGetter == null) {
                throw new RuntimeException("Class " + clazz.getName() + " has no field annotated with @Id.");
            }

            return descriptor;
        }

        static <T> void lookForIdAnnotation(ClassDescriptor<T> descriptor, Field field) {
            Id idAnnotation = field.getAnnotation(Id.class);
            if (idAnnotation == null) return;

            if (descriptor.idGetter != null) {
                throw new RuntimeException(
                        "Class " + descriptor.clazz.getName() + " has more than one field marked as @Id."
                );
            } else {
                // Find the getter.
                String getterName = "get" +
                        field.getName().substring(0, 1).toUpperCase() +
                        field.getName().substring(1);
                try {
                    MethodType methodType = MethodType.methodType(field.getType());
                    descriptor.idGetter = MethodHandles.lookup()
                            .findVirtual(descriptor.clazz, getterName, methodType);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(
                            "Class " + descriptor.clazz.getName() + ": can't find getter for field " +
                                    field.getName() + " (expected #" + getterName + ")."
                    );
                }
            }
        }

        static <T> void lookForReferenceAnnotation(ClassDescriptor<T> descriptor, Field field) {
            ReferenceId referenceIdAnnotation = field.getAnnotation(ReferenceId.class);
            if (referenceIdAnnotation == null) return;


        }

        public Object getId(T record) {
            try {
                return getIdGetter().invoke(record);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
