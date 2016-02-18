package org.androidannotations.api.sharedpreferences;

public final class FloatPrefEditorField<T extends EditorHelper<T>> extends AbstractPrefEditorField<T> {
    FloatPrefEditorField(T editorHelper, String key) {
        super(editorHelper, key);
    }

    public T put(float value) {
        this.editorHelper.getEditor().putFloat(this.key, value);
        return this.editorHelper;
    }
}
