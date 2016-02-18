package org.androidannotations.api.sharedpreferences;

public final class StringPrefEditorField<T extends EditorHelper<T>> extends AbstractPrefEditorField<T> {
    StringPrefEditorField(T editorHelper, String key) {
        super(editorHelper, key);
    }

    public T put(String value) {
        this.editorHelper.getEditor().putString(this.key, value);
        return this.editorHelper;
    }
}
