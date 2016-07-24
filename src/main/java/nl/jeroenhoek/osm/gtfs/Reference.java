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
    public String toString() {
        return isResolved() ? getReferred().toString() : getId().toString();
    }
}
