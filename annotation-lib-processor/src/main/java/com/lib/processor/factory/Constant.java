package com.lib.processor.factory;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;

class Constant {
    static final String PACKAGE_NAME = "com.annotation.compiler";

    //android.content
    static TypeName context = ClassName.get("android.content", "Context");
    static TypeName contentValues = ClassName.get("android.content", "ContentValues");
    static TypeName contentUris = ClassName.get("android.content", "ContentUris");
    static TypeName sharedPreferences = ClassName.get("android.content", "SharedPreferences");
    static TypeName configuration = ClassName.get("android.content.res", "Configuration");
    static TypeName resources = ClassName.get("android.content.res", "Resources");

    //android.app
    static TypeName activity = ClassName.get("android.app", "Activity");
    static TypeName dialog = ClassName.get("android.app", "Dialog");

    //android.provider
    static TypeName calendar = ClassName.get("android.provider.CalendarContract", "Calendars");
    static TypeName events = ClassName.get("android.provider.CalendarContract", "Events");
    static TypeName reminders = ClassName.get("android.provider.CalendarContract", "Reminders");
    static TypeName attendees = ClassName.get("android.provider.CalendarContract", "Attendees");
    static TypeName calendarContract = ClassName.get("android.provider", "CalendarContract");

    //android.database
    static TypeName cursor = ClassName.get("android.database", "Cursor");

    //Annotation
    static ClassName nonNull = ClassName.get("android.support.annotation", "NonNull");
    static ClassName suppressLint = ClassName.get("android.annotation", "SuppressLint");
    static ClassName requiresPermission = ClassName.get("android.support.annotation", "RequiresPermission");
    static ClassName permission = ClassName.get("android.Manifest", "permission");
    static ClassName targetApi = ClassName.get("android.annotation", "TargetApi");

    //android.net
    static TypeName uri = ClassName.get("android.net", "Uri");
    static TypeName connectivityManager = ClassName.get("android.net", "ConnectivityManager");

    //android.widget
    static TypeName toast = ClassName.get("android.widget", "Toast");
    static TypeName editText = ClassName.get("android.widget", "EditText");
    static TypeName view = ClassName.get("android.view", "View");
    static TypeName motionEvent = ClassName.get("android.view", "MotionEvent");
    static TypeName viewGroup = ClassName.get("android.view", "ViewGroup");

    //android.view
    static TypeName inputMethodManager = ClassName.get("android.view.inputmethod", "InputMethodManager");

    //android.os
    static TypeName build = ClassName.get("android.os", "Build");

    //android.graphics
    static TypeName colorDrawable = ClassName.get("android.graphics.drawable", "ColorDrawable");

    //android.preference
    static TypeName preferenceManager = ClassName.get("android.preference", "PreferenceManager");

    //Gson
    static ClassName gson = ClassName.get("com.google.gson", "Gson");

    //Create generic T
    static TypeVariableName T = TypeVariableName.get("T");
    static TypeVariableName tArray = TypeVariableName.get("T[]");
    static TypeName arrayListT = ParameterizedTypeName.get(ClassName.get(ArrayList.class), T);// Create generic ArrayList<T>
    static TypeName listT = ParameterizedTypeName.get(ClassName.get(List.class), T);//Create generic List<T>
    static TypeName classT = ParameterizedTypeName.get(ClassName.get(Class.class), T);//Create generic Class<T>
    static TypeName classTArray = ParameterizedTypeName.get(ClassName.get(Class.class), tArray);//Create generic Class<T[]>
}
