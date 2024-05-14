package com.ununicode.providers;

import com.ununicode.editortab.CustomHttpResponseEditorTab;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.extension.*;

public class CustomHttpResponseEditor implements HttpResponseEditorProvider {

    MontoyaApi api;

    public CustomHttpResponseEditor(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public ExtensionProvidedHttpResponseEditor provideHttpResponseEditor(EditorCreationContext creationContext) {
        return new CustomHttpResponseEditorTab(api, creationContext);
    }
}
