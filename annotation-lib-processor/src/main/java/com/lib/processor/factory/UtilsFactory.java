package com.lib.processor.factory;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UtilsFactory {
    private ProcessingEnvironment processingEnv;
    private Filer filer;

    public UtilsFactory(ProcessingEnvironment processingEnv, Filer filer) {
        this.processingEnv = processingEnv;
        this.filer = filer;
    }

    public void processUtilsFactory() {
        String nameClass = "Utils";

        String packageName = Constant.PACKAGE_NAME;

        //set class
        TypeSpec.Builder classGenerateBuilder = TypeSpec.classBuilder(nameClass)
                .addModifiers(Modifier.PUBLIC);
        //type
        TypeName context = Constant.context;
        TypeName activity = Constant.activity;
        TypeName editText = Constant.editText;
        TypeName inputMethodManager = Constant.inputMethodManager;
        TypeName view = Constant.view;
        TypeName connectivityManager = Constant.connectivityManager;
        TypeName toast = Constant.toast;
        TypeName dialog = Constant.dialog;
        TypeName colorDrawable = Constant.colorDrawable;
        ClassName gson = Constant.gson;
        TypeName classTArray = Constant.classTArray;
        TypeName listT = Constant.listT;

        //set method
        classGenerateBuilder
                //showSoftKeyboard
                .addMethod(MethodSpec.methodBuilder("showSoftKeyboard")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(activity, "activity")
                        .addParameter(editText, "editText")
                        .addCode("$T imm = (InputMethodManager) activity.getSystemService($T.INPUT_METHOD_SERVICE);\n" +
                                "        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);\n", inputMethodManager, context)
                        .build())
                //hideSoftKeyboard
                .addMethod(MethodSpec.methodBuilder("hideSoftKeyboard")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(activity, "activity")
                        .addCode("if (activity == null) {\n" +
                                "            return;\n" +
                                "        }\n" +
                                "        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);\n" +
                                "        $T v = activity.getCurrentFocus();\n" +
                                "        if (imm != null && v != null) {\n" +
                                "            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);\n" +
                                "        }\n", view)

                        .build())
                //checkNetwork
                .addMethod(MethodSpec.methodBuilder("checkNetwork")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(context, "activity")
                        .addParameter(String.class, "text")
                        .addCode("$T manager = (ConnectivityManager) activity.getSystemService(activity.CONNECTIVITY_SERVICE);\n" +
                                "\n" +
                                "        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)\n" +
                                "                .isConnectedOrConnecting();\n" +
                                "\n" +
                                "        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)\n" +
                                "                .isConnectedOrConnecting();\n" +
                                "\n" +
                                "        if (isWifi) return ConnectivityManager.TYPE_WIFI;\n" +
                                "        if (is3g) return ConnectivityManager.TYPE_MOBILE;\n" +
                                "\n" +
                                "        if (!is3g && !isWifi) {\n" +
                                "            $T.makeText(activity, text, Toast.LENGTH_LONG).show();\n" +
                                "            return -1;\n" +
                                "        }\n" +
                                "\n" +
                                "        return -1;\n", connectivityManager, toast)
                        .returns(TypeName.INT)
                        .build())
                //getDialogWaiting
                .addMethod(MethodSpec.methodBuilder("getDialogWaiting")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(context, "context")
                        .addCode("$T dialogLoad=new Dialog(context);\n" +
                                "        dialogLoad.getWindow().setBackgroundDrawable(new $T(android.graphics.Color.TRANSPARENT));\n" +
                                "        dialogLoad.setCanceledOnTouchOutside(false);\n" +
                                "        dialogLoad.setCancelable(false);\n" +
                                "        return dialogLoad;\n", dialog, colorDrawable)
                        .returns(dialog)
                        .addJavadoc("create dialog waiting when click button send api\n")
                        .addJavadoc("@param context\n")
                        .addJavadoc("@return\n")
                        .build())
                //addDataArrayFromAssetFileJson
                .addMethod(MethodSpec.methodBuilder("addDataArrayFromAssetFileJson")
                        .addParameter(context, "context")
                        .addParameter(String.class, "filename")
                        .addParameter(classTArray, "type")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addTypeVariable(Constant.T)
                        .addCode("//Read file Json from Asset\n" +
                                "        $T is = context.getAssets().open(filename);\n" +
                                "        int size = is.available();\n" +
                                "        byte[] buffer = new byte[size];\n" +
                                "        is.read(buffer);\n" +
                                "        is.close();\n" +
                                "        String json = new String(buffer, \"UTF-8\");\n" +
                                "        //Return list data with Generic T\n" +
                                "        $T<T> data = new $T<>();\n" +
                                "        $T gson = new Gson();\n" +
                                "        T[] arrayClass = gson.fromJson(json, type);\n" +
                                "        for (T element : arrayClass) {\n" +
                                "            data.add(element);\n" +
                                "        }\n" +
                                "        return data;\n", InputStream.class, List.class, ArrayList.class, gson)
                        .returns(listT)
                        .addException(Exception.class)
                        .addJavadoc("Using read file json from assets to list with POJO (GSON support)\n")
                        .addJavadoc("@param context\n")
                        .addJavadoc("@param filename\n")
                        .addJavadoc("@param type\n")
                        .addJavadoc("@param <T>\n")
                        .addJavadoc("@return\n")
                        .addJavadoc("@throws IOException\n")
                        .build())
                //getSSLSocketFactory
                .addMethod(MethodSpec.methodBuilder("getSSLSocketFactory")
                        .addCode("$T[] trustAllCerts = new TrustManager[]{\n" +
                                        "                new $T() {\n" +
                                        "                    @Override\n" +
                                        "                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws $T {\n" +
                                        "                    }\n" +
                                        "\n" +
                                        "                    @Override\n" +
                                        "                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {\n" +
                                        "                    }\n" +
                                        "\n" +
                                        "                    @Override\n" +
                                        "                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {\n" +
                                        "                        return new java.security.cert.X509Certificate[]{};\n" +
                                        "                    }\n" +
                                        "                }\n" +
                                        "        };\n" +
                                        "\n" +
                                        "        // Install the all-trusting trust manager\n" +
                                        "        $T sslContext = null;\n" +
                                        "        try {\n" +
                                        "            sslContext = SSLContext.getInstance(\"SSL\");\n" +
                                        "            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());\n" +
                                        "        } catch ($T e) {\n" +
                                        "            e.printStackTrace();\n" +
                                        "        } catch ($T e) {\n" +
                                        "            e.printStackTrace();\n" +
                                        "        }\n" +
                                        "\n" +
                                        "        // Create an ssl socket factory with our all-trusting manager\n" +
                                        "        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();\n" +
                                        "        return sslSocketFactory;\n", TrustManager.class, X509TrustManager.class, CertificateException.class
                                , SSLContext.class, NoSuchAlgorithmException.class, KeyManagementException.class)
                        .returns(SSLSocketFactory.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .build())
                //hideKeyword
                .addMethod(MethodSpec.methodBuilder("hideKeyword")
                        .addParameter(view, "view")
                        .addParameter(activity, "activity",Modifier.FINAL)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addCode(" // Set up touch listener for non-text box views to hide keyboard.\n" +
                                "        if (!(view instanceof EditText)) {\n" +
                                "            view.setOnTouchListener(new View.OnTouchListener() {\n" +
                                "                public boolean onTouch(View v, $T event) {\n" +
                                "                    hideSoftKeyboard(activity);\n" +
                                "                    return false;\n" +
                                "                }\n" +
                                "            });\n" +
                                "        }\n" +
                                "\n" +
                                "        //If a layout container, iterate over children and seed recursion.\n" +
                                "        if (view instanceof ViewGroup) {\n" +
                                "            for (int i = 0; i < (($T) view).getChildCount(); i++) {\n" +
                                "                View innerView = ((ViewGroup) view).getChildAt(i);\n" +
                                "                hideKeyword(innerView,activity);\n" +
                                "            }\n" +
                                "        }\n", Constant.motionEvent, Constant.viewGroup)
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
