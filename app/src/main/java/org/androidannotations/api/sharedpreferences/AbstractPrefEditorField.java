package org.androidannotations.api.sharedpreferences;

public abstract class AbstractPrefEditorField<T extends EditorHelper<T>> {
    protected final T editorHelper;
    protected final String key;

    public AbstractPrefEditorField(T editorHelper, String key) {
        this.editorHelper = editorHelper;
        this.key = key;
    }

    public final T remove() {
        this.editorHelper.getEditor().remove(this.key);
        return this.editorHelper;
    }
}
