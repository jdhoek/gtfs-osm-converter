package nl.jeroenhoek.osm.gtfs;

import nl.jeroenhoek.osm.gtfs.RecordKnowledgeBase.ClassDescriptor;

public class Reference<I, R> {
    R referred;
    I id;

    private Reference(I id, R referred) {
        this.id = id;
        this.referred = referred;
    }

    public static <I, R> Reference<I, R> byId(I id) {
        return new Reference<>(id, null);
    }

    public static <I, R> Reference<I, R> byReferred(R referred) {
        I id = findId(referred);
        return new Reference<>(id, referred);
    }

    public boolean isResolved() {
        return id != null && referred != null;
    }

    public I getId() {
        return id;
    }

    public R getReferred() {
        return referred;
    }

    public void resolve(R referred) {
        this.referred = referred;
        this.id = findId(referred);
    }

    @SuppressWarnings("unchecked")
    static <I, R> I findId(R referred) {
        ClassDescriptor<R> descriptor = RecordKnowledgeBase.INSTANCE.inspect((Class<R>) referred.getClass());
        return  (I) descriptor.getId(referred);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Reference<?, ?> reference = (Reference<?, ?>) other;

        return id != null ? id.equals(reference.id) : reference.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return isResolved() ? getReferred().toString() : getId().toString();
    }
}
