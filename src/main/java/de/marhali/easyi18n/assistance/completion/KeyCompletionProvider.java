package de.marhali.easyi18n.assistance.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ProcessingContext;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.assistance.OptionalAssistance;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Set;

/**
 * Provides existing translation keys for code completion.
 * @author marhali
 */
class KeyCompletionProvider extends CompletionProvider<CompletionParameters> implements OptionalAssistance {

    private static final Icon icon = IconLoader.getIcon("/icons/translate13.svg", KeyCompletionProvider.class);

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        Project project = parameters.getOriginalFile().getProject();

        if(!isAssistance(project)) {
            return;
        }

        ProjectSettings settings = ProjectSettingsService.get(project).getState();
        TranslationData data = InstanceManager.get(project).store().getData();
        Set<KeyPath> fullKeys = data.getFullKeys();
        for (KeyPath key : fullKeys) {
            LookupElement lookupElement = constructLookup(new Translation(key, data.getTranslation(key)), settings);
            if (lookupElement!=null) {
                result.addElement(lookupElement);
            }
        }
    }

    private LookupElement constructLookup(Translation translation, ProjectSettings settings) {
        KeyPathConverter converter = new KeyPathConverter(settings);
        if (translation.getKey().toString().contains("language_nl")) {
            String lang = translation.getKey().remove(0);
            if (translation.getKey().toString().contains("tenants")) {
                translation.getKey().remove(0);
                translation.getKey().remove(0);
            }
            return LookupElementBuilder
                    .create(converter.toString(translation.getKey()))
                    .withTailText(" " + translation.getValue().get("tenant_translation"), true)
                    .withIcon(icon);
        } else {
            return null;
        }
    }
}
