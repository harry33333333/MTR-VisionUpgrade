package org.mtr.visionupgrade.culling;

public final class InteriorCullingState {
    private static final ThreadLocal<Boolean> CULLING_INTERIOR = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private InteriorCullingState() {}

    public static boolean isCullingInterior() {
        return CULLING_INTERIOR.get();
    }

    public static void setCullingInterior(boolean culling) {
        CULLING_INTERIOR.set(culling);
    }

    public static void reset() {
        CULLING_INTERIOR.set(Boolean.FALSE);
    }
}
