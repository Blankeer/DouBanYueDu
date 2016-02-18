package org.androidannotations.api.sharedpreferences;

public final class IntPrefEditorField<T extends EditorHelper<T>> extends AbstractPrefEditorField<T> {
    IntPrefEditorField(T editorHelper, String key) {
        super(editorHelper, key);
    }

    public T put(int value) {
        this.editorHelper.getEditor().putInt(this.key, value);
        return this.editorHelper;
    }
}
