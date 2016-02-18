package io.realm.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public class NativeObjectReference extends PhantomReference<NativeObject> {
    final long nativePointer;

    public NativeObjectReference(NativeObject referent, ReferenceQueue<? super NativeObject> referenceQueue) {
        super(referent, referenceQueue);
        this.nativePointer = referent.nativePointer;
    }
}
