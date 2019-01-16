package com.lib.processor.factory;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

public class CustomToastFactory {
    private ProcessingEnvironment processingEnv;
    private Filer filer;

    public CustomToastFactory(ProcessingEnvironment processingEnv, Filer filer) {
        this.processingEnv = processingEnv;
        this.filer = filer;
    }

    public void processCustomToast() {
        String nameClass = "CustomToast";

        String packageName = Constant.PACKAGE_NAME;

        //set class
        TypeSpec.Builder classGenerateBuilder = TypeSpec.classBuilder(nameClass)
                .addModifiers(Modifier.PUBLIC);

        TypeName context = Constant.context;
        TypeName toast = Constant.toast;

        ClassName nonNull = Constant.nonNull;
        AnnotationSpec annotationNonNull = AnnotationSpec.builder(nonNull).build();

        //set properties
        classGenerateBuilder.addField(FieldSpec.builder(context, "context", Modifier.PRIVATE).build())
                .addField(FieldSpec.builder(ClassName.get(packageName, nameClass), "customToast", Modifier.STATIC, Modifier.PRIVATE).build())
                .addField(FieldSpec.builder(toast, "mess", Modifier.PRIVATE).build());

        //set constructor
        MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
        classGenerateBuilder.addMethod(constructor);
        //set method
        classGenerateBuilder
                //getInstance
                .addMethod(MethodSpec.methodBuilder("getInstance")
                        .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED, Modifier.STATIC)
                        .returns(ClassName.get(packageName, nameClass))
                        .addCode(" if (customToast == null) customToast = new $N();\n" +
                                " return customToast;\n", nameClass)
                        .build())
                //setupContext
                .addMethod(MethodSpec.methodBuilder("setupContext")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(context.annotated(annotationNonNull), "context")
                        .addCode("this.context = context.getApplicationContext();")
                        .build())
                //setText
                .addMethod(MethodSpec.methodBuilder("setText")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(String.class, "text")
                        .addCode("       mess = Toast.makeText(context, text, Toast.LENGTH_SHORT);\n" +
                                "        mess.setText(text);\n" +
                                "        mess.show();")
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
