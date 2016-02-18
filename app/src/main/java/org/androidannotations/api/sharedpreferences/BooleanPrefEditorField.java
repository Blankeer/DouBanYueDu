package org.androidannotations.api.sharedpreferences;

public final class BooleanPrefEditorField<T extends EditorHelper<T>> extends AbstractPrefEditorField<T> {
    BooleanPrefEditorField(T editorHelper, String key) {
        super(editorHelper, key);
    }

    public T put(boolean value) {
        this.editorHelper.getEditor().putBoolean(this.key, value);
        return this.editorHelper;
    }
}
