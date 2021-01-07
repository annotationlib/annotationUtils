package com.lib.processor.factory;

import com.lib.processor.annotation.SharePreferences;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import static com.lib.processor.factory.FormatString.capitalizeWord;

public class SharePreferencesLoaderFactory {
    private ProcessingEnvironment processingEnv;
    private Filer filer;

    public SharePreferencesLoaderFactory(ProcessingEnvironment processingEnv, Filer filer) {
        this.processingEnv = processingEnv;
        this.filer = filer;
    }

    public void processAnnotationSharePreferencesLoader(Element element) {
        String nameClass = "SharePreferencesLoader";

        String packageName = Constant.PACKAGE_NAME;

        SharePreferences anno = element.getAnnotation(SharePreferences.class);

        //set class
        TypeSpec.Builder classGenerateBuilder = TypeSpec.classBuilder(nameClass)
                .addModifiers(Modifier.PUBLIC);
        //set properties
        classGenerateBuilder.addField(FieldSpec.builder(Constant.sharedPreferences, "mPreferences", Modifier.PRIVATE, Modifier.STATIC).build())
                .addField(FieldSpec.builder(TypeName.INT, "PREFERENCES_MODE", Modifier.PRIVATE).initializer(ClassName.get("android.app", "Activity.MODE_PRIVATE").toString()).build())
                .addField(FieldSpec.builder(String.class, "PREFERENCES_NAME", Modifier.PRIVATE).initializer("$S", "").build())
                .addField(FieldSpec.builder(Constant.context, "mContext", Modifier.PRIVATE).build())
                .addField(FieldSpec.builder(ClassName.get(packageName, nameClass), "sharePreferencesLoader", Modifier.PRIVATE, Modifier.STATIC).build());
        //set constructor
        MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
        classGenerateBuilder.addMethod(constructor);
        //Define parameter
        ParameterSpec context = ParameterSpec.builder(Constant.context, "pContext").build();
        ParameterSpec name = ParameterSpec.builder(String.class, "name", Modifier.FINAL).build();
        ParameterSpec mode = ParameterSpec.builder(TypeName.INT, "mode", Modifier.FINAL).build();
        ParameterSpec pKeyName = ParameterSpec.builder(String.class, "pKeyName", Modifier.FINAL).build();

        ParameterSpec pSValue = ParameterSpec.builder(String.class, "pValue", Modifier.FINAL).build();
        ParameterSpec pIValue = ParameterSpec.builder(TypeName.INT, "pValue", Modifier.FINAL).build();
        ParameterSpec pLValue = ParameterSpec.builder(TypeName.LONG, "pValue", Modifier.FINAL).build();
        ParameterSpec pBValue = ParameterSpec.builder(TypeName.BOOLEAN, "pValue", Modifier.FINAL).build();

        ParameterSpec obj = ParameterSpec.builder(TypeName.OBJECT, "obj", Modifier.FINAL).build();

        ParameterSpec pDefaultValue = ParameterSpec.builder(String.class, "pDefaultValue", Modifier.FINAL).build();
        ParameterSpec pIDefaultValue = ParameterSpec.builder(TypeName.INT, "pDefaultValue", Modifier.FINAL).build();
        ParameterSpec pLDefaultValue = ParameterSpec.builder(TypeName.LONG, "pDefaultValue", Modifier.FINAL).build();
        ParameterSpec pBDefaultValue = ParameterSpec.builder(TypeName.BOOLEAN, "pDefaultValue", Modifier.FINAL).build();

        TypeName arrayListT = Constant.arrayListT;
        ParameterSpec pClass = ParameterSpec.builder(Constant.T, "pClass").build();
        ParameterSpec type = ParameterSpec.builder(Type.class, "type", Modifier.FINAL).build();
        ParameterSpec list = ParameterSpec.builder(Constant.listT, "list").build(); // create List<T>
        //return this
        String returnThis = "return this";
        //ClassName
        ClassName gson = ClassName.get("com.google.gson", "Gson");

        //set method
        classGenerateBuilder
                //add method getInstance
                .addMethod(
                        MethodSpec.methodBuilder("getInstance").addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED, Modifier.STATIC)
                                .returns(ClassName.get(packageName, nameClass))
                                .beginControlFlow("if(sharePreferencesLoader==null)")
                                .addStatement("sharePreferencesLoader = new $N()", nameClass)
                                .endControlFlow()
                                .addStatement("return sharePreferencesLoader")
                                .build())
                .addMethod(
                        MethodSpec.methodBuilder("setupContext")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(ClassName.get(packageName, nameClass))
                                .addParameter(context)
                                .addStatement("this.mContext = pContext.getApplicationContext()")
                                .addStatement("this.PREFERENCES_NAME = mContext.getPackageName()")
                                .addStatement(returnThis)
                                .build())
                .addMethod(MethodSpec.methodBuilder("setupPreferencesName")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.get(packageName, nameClass))
                        .addParameter(name)
                        .addStatement("PREFERENCES_NAME = name")
                        .addStatement(returnThis)
                        .build())
                .addMethod(MethodSpec.methodBuilder("setupPreferencesMode")
                        .returns(ClassName.get(packageName, nameClass))
                        .addParameter(mode)
                        .addStatement("PREFERENCES_MODE = mode")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement(returnThis)
                        .build())
                .addMethod(MethodSpec.methodBuilder("build")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.get("android.content", "SharedPreferences"))
                        .addStatement("mPreferences = mContext.getSharedPreferences(PREFERENCES_NAME, PREFERENCES_MODE)")
                        .addStatement("return mPreferences")
                        .build())
                .addMethod(MethodSpec.methodBuilder("saveValueToSharedPreferences")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.get("android.content.SharedPreferences", "Editor"))
                        .addStatement("return mPreferences.edit()")
                        .build())
                .addMethod(MethodSpec.methodBuilder("remove")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(pKeyName)
                        .beginControlFlow("if (pKeyName != null)")
                        .addStatement("this.saveValueToSharedPreferences().remove(pKeyName).commit()")
                        .endControlFlow()
                        .build())
                .addMethod(MethodSpec.methodBuilder("saveValueToSharedPreferences")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(pKeyName)
                        .addParameter(pSValue)
                        .addStatement("saveValueToSharedPreferences().putString(pKeyName, pValue).commit()")
                        .build())
                .addMethod(MethodSpec.methodBuilder("saveValueToSharedPreferences")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(pKeyName)
                        .addParameter(pIValue)
                        .addStatement("saveValueToSharedPreferences().putInt(pKeyName, pValue).commit()")
                        .build())
                .addMethod(MethodSpec.methodBuilder("saveLongValueToSharedPreferences")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(pKeyName)
                        .addParameter(pLValue)
                        .addStatement("saveValueToSharedPreferences().putLong(pKeyName, pValue).commit()")
                        .build())
                .addMethod(MethodSpec.methodBuilder("saveValueToSharedPreferences")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(pKeyName)
                        .addParameter(pBValue)
                        .addStatement("saveValueToSharedPreferences().putBoolean(pKeyName, pValue).commit()")
                        .build())
                .addMethod(MethodSpec.methodBuilder("saveValueToSharedPreferences")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(pKeyName)
                        .addParameter(obj)
                        .addStatement("$T gson= new $T()", gson, gson)
                        .addStatement("String json = gson.toJson(obj);")
                        .beginControlFlow("if (json != null)")
                        .addStatement("saveValueToSharedPreferences().putString(pKeyName, json).commit()")
                        .endControlFlow()
                        .build())
                .addMethod(MethodSpec.methodBuilder("getValueFromPreferences")
                        .addModifiers(Modifier.PUBLIC)
                        .addTypeVariable(Constant.T)
                        .returns(TypeName.OBJECT)
                        .addParameter(pKeyName)
                        .addParameter(pDefaultValue)
                        .addParameter(pClass)
                        .addStatement("$T gson= new $T()", gson, gson)
                        .addStatement("String json = mPreferences.getString(pKeyName, pDefaultValue)")
                        .addStatement("Object object=gson.fromJson(json, pClass.getClass())")
                        .addStatement("return object")
                        .build())
                .addMethod(MethodSpec.methodBuilder("saveValueList")
                        .addModifiers(Modifier.PUBLIC)
                        .addTypeVariable(Constant.T)
                        .addParameter(pKeyName)
                        .addParameter(type)
                        .addParameter(list)
                        .addStatement("$T gson= new $T()", gson, gson)
                        .addStatement("//   Type type = new TypeToken<List<T>>(){}.getType()")
                        .addStatement("String json = gson.toJson(list, type)")
                        .beginControlFlow("if (json != null)")
                        .addStatement("saveValueToSharedPreferences().putString(pKeyName, json).commit()")
                        .endControlFlow()
                        .build())
                .addMethod(MethodSpec.methodBuilder("getValueList")
                        .addModifiers(Modifier.PUBLIC)
                        .addTypeVariable(Constant.T)
                        .returns(arrayListT)
                        .addParameter(pKeyName)
                        .addParameter(type)
                        .addParameter(pDefaultValue)
                        .addStatement("$T gson= new $T()", gson, gson)
                        .addStatement("String json = mPreferences.getString(pKeyName, pDefaultValue)")
                        .addStatement("//  Type type = new TypeToken<List<T>>(){}.getType()")
                        .addStatement("ArrayList<T> list = gson.fromJson(json, type)")
                        .addStatement("return list")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getValueFromPreferences")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(pKeyName)
                        .addParameter(pDefaultValue)
                        .addStatement("return mPreferences.getString(pKeyName, pDefaultValue)")
                        .returns(String.class)
                        .build())
                .addMethod(MethodSpec.methodBuilder("getValueFromPreferences")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(pKeyName)
                        .addParameter(pIDefaultValue)
                        .addStatement("return mPreferences.getInt(pKeyName, pDefaultValue)")
                        .returns(TypeName.INT)
                        .build())
                .addMethod(MethodSpec.methodBuilder("getValueLongFromPreferences")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(pKeyName)
                        .addParameter(pLDefaultValue)
                        .addStatement("return mPreferences.getLong(pKeyName, pDefaultValue)")
                        .returns(TypeName.LONG)
                        .build())
                .addMethod(MethodSpec.methodBuilder("onDestroy")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("mContext = null")
                        .addStatement("mPreferences = null")
                        .addStatement("sharePreferencesLoader = null")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getValueFromPreferences")
                        .addParameter(pKeyName)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(pBDefaultValue)
                        .addStatement("return mPreferences.getBoolean(pKeyName, pDefaultValue)")
                        .returns(TypeName.BOOLEAN)
                        .build());

        TypeSpec classGenerate = classGenerateBuilder
                .build();
        JavaFile javaFile = JavaFile.builder(packageName, classGenerate).build();
        try {
            javaFile.writeTo(filer);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
