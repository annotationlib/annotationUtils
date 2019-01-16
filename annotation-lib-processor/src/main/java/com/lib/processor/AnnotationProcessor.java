package com.lib.processor;

import com.lib.processor.annotation.CalendarContract;
import com.lib.processor.annotation.SharePreferences;
import com.lib.processor.factory.CalendarContractUtilFactory;
import com.lib.processor.factory.CustomToastFactory;
import com.lib.processor.factory.LocaleHelperFactory;
import com.lib.processor.factory.SharePreferencesLoaderFactory;
import com.lib.processor.factory.UtilsFactory;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public class AnnotationProcessor extends AbstractProcessor {
    private Messager mess;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        mess = processingEnvironment.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportType = new LinkedHashSet<>();
        supportType.add(SharePreferences.class.getCanonicalName());
        supportType.add(CalendarContract.class.getCanonicalName());
        return supportType;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element element : roundEnvironment.getElementsAnnotatedWith(SharePreferences.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                mess.printMessage(Diagnostic.Kind.ERROR, "Don't support type ", element);
                return true;
            }
            SharePreferencesLoaderFactory sharePreferencesLoaderFactory = new SharePreferencesLoaderFactory(processingEnv, filer);
            sharePreferencesLoaderFactory.processAnnotationSharePreferencesLoader(element);

            CustomToastFactory customToastFactory = new CustomToastFactory(processingEnv, filer);
            customToastFactory.processCustomToast();

            LocaleHelperFactory localeHelperFactory = new LocaleHelperFactory(processingEnv, filer);
            localeHelperFactory.processLocaleHelper();

            UtilsFactory utilsFactory = new UtilsFactory(processingEnv, filer);
            utilsFactory.processUtilsFactory();
        }
        for (Element element : roundEnvironment.getElementsAnnotatedWith(CalendarContract.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                mess.printMessage(Diagnostic.Kind.ERROR, "Don't support type ", element);
                return true;
            }
            CalendarContractUtilFactory calendarContractUtilFactory = new CalendarContractUtilFactory(processingEnv, filer);
            calendarContractUtilFactory.processAnnotationCalendarContractUtil(element);
        }

        return false;
    }


}
