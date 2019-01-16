package com.lib.processor.factory;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

public class LocaleHelperFactory {
    private ProcessingEnvironment processingEnv;
    private Filer filer;

    public LocaleHelperFactory(ProcessingEnvironment processingEnv, Filer filer) {
        this.processingEnv = processingEnv;
        this.filer = filer;
    }

    public void processLocaleHelper() {
        String nameClass = "LocaleHelper";

        String packageName = Constant.PACKAGE_NAME;

        //set class
        TypeSpec.Builder classGenerateBuilder = TypeSpec.classBuilder(nameClass)
                .addModifiers(Modifier.PUBLIC);
        //type
        TypeName context = Constant.context;
        TypeName build = Constant.build;

        ClassName nonNull = Constant.nonNull;
        AnnotationSpec annotationNonNull = AnnotationSpec.builder(nonNull).build();

        ClassName targetApi = Constant.targetApi;
        AnnotationSpec annotationTargetApi = AnnotationSpec.builder(targetApi).addMember("value", "Build.VERSION_CODES.N").build();

        AnnotationSpec annotationSuppressWarnings = AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "deprecation").build();

        //set properties
        classGenerateBuilder.addField(FieldSpec.builder(String.class, "LANGUAGE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("$S", "language").build());
        //setMethod
        classGenerateBuilder
                //onAttach
                .addMethod(MethodSpec.methodBuilder("onAttach")
                        .addParameter(context.annotated(annotationNonNull), "context")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addCode("String lang = getPersistedData(context, $T.getDefault().getLanguage());\n" +
                                "        return setLocale(context, lang);\n", Locale.class)
                        .returns(context)
                        .build())
                //onAttach default
                .addMethod(MethodSpec.methodBuilder("onAttach")
                        .addParameter(context.annotated(annotationNonNull), "context")
                        .addParameter(String.class, "defaultLanguage")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addCode("String lang = getPersistedData(context, defaultLanguage);\n" +
                                "        return setLocale(context, lang);\n")
                        .returns(context)
                        .build())
                //getLanguage
                .addMethod(MethodSpec.methodBuilder("getLanguage")
                        .addCode("return getPersistedData(context, Locale.getDefault().getLanguage());\n")
                        .addParameter(context, "context")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(String.class)
                        .build())
                //setLocale
                .addMethod(MethodSpec.methodBuilder("setLocale")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(context, "context")
                        .addParameter(String.class, "language")
                        .addCode("persist(context, language);\n" +
                                "\n" +
                                "        if ($T.VERSION.SDK_INT >= Build.VERSION_CODES.N) {\n" +
                                "            return updateResources(context, language);\n" +
                                "        }\n" +
                                "\n" +
                                "        return updateResourcesLegacy(context, language);\n", build)
                        .returns(context)
                        .build())
                //getPersistedData
                .addMethod(MethodSpec.methodBuilder("getPersistedData")
                        .addCode("$T preferences = $T.getDefaultSharedPreferences(context);\n" +
                                "        return preferences.getString(LANGUAGE, defaultLanguage);\n", Constant.sharedPreferences, Constant.preferenceManager)
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .addParameter(context.annotated(annotationNonNull), "context")
                        .addParameter(String.class, "defaultLanguage")
                        .returns(String.class)
                        .build())
                //persist
                .addMethod(MethodSpec.methodBuilder("persist")
                        .addParameter(context, "context")
                        .addParameter(String.class, "language")
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .addCode("SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);\n" +
                                "        SharedPreferences.Editor editor = preferences.edit();\n" +
                                "        editor.putString(LANGUAGE, language);\n" +
                                "        editor.apply();\n")
                        .build())
                //updateResources
                .addMethod(MethodSpec.methodBuilder("updateResources")
                        .addAnnotation(annotationTargetApi)
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .addParameter(context, "context")
                        .addParameter(String.class, "language")
                        .addCode("Locale locale = new Locale(language);\n" +
                                "        Locale.setDefault(locale);\n" +
                                "        $T configuration = context.getResources().getConfiguration();\n" +
                                "        configuration.setLocale(locale);\n" +
                                "        configuration.setLayoutDirection(locale);\n" +
                                "\n" +
                                "        return context.createConfigurationContext(configuration);\n", Constant.configuration)
                        .returns(context)
                        .build())
                //updateResourcesLegacy
                .addMethod(MethodSpec.methodBuilder("updateResourcesLegacy")
                        .addAnnotation(annotationSuppressWarnings)
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .addParameter(context, "context")
                        .addParameter(String.class, "language")
                        .addCode("Locale locale = new Locale(language);\n" +
                                "        Locale.setDefault(locale);\n" +
                                "\n" +
                                "        $T resources = context.getResources();\n" +
                                "\n" +
                                "        Configuration configuration = resources.getConfiguration();\n" +
                                "        configuration.locale = locale;\n" +
                                "        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {\n" +
                                "            configuration.setLayoutDirection(locale);\n" +
                                "        }\n" +
                                "\n" +
                                "        resources.updateConfiguration(configuration, resources.getDisplayMetrics());\n" +
                                "\n" +
                                "        return context;\n", Constant.resources)
                        .returns(context)
                        .build());

        TypeSpec classGenerate = classGenerateBuilder
                .build();
        JavaFile javaFile = JavaFile.builder(packageName, classGenerate).build();
        try {
            javaFile.writeTo(filer);

        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
