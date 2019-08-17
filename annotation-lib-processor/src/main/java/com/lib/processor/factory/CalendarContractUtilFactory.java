package com.lib.processor.factory;

import com.lib.processor.annotation.CalendarContract;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

public class CalendarContractUtilFactory {
    private ProcessingEnvironment processingEnv;
    private Filer filer;

    public CalendarContractUtilFactory(ProcessingEnvironment processingEnv, Filer filer) {
        this.processingEnv = processingEnv;
        this.filer = filer;
    }

    public void processAnnotationCalendarContractUtil(Element element) {
        String nameClass = "CalendarContractUtils";

        String packageName = Constant.PACKAGE_NAME;

        CalendarContract anno = element.getAnnotation(CalendarContract.class);

        String accountName = anno.accountName();
        String calenderName = anno.calendarName();
        String calendarColor = anno.calendarColor();
        String eventColor = anno.eventColor();

        //set name class
        TypeSpec.Builder classGenerateBuilder = TypeSpec.classBuilder(nameClass)
                .addModifiers(Modifier.PUBLIC);
        //setCLass import
        TypeName context = Constant.context;
        TypeName contentValues = Constant.contentValues;
        TypeName contentUris = Constant.contentUris;

        TypeName calendar = Constant.calendar;
        TypeName events = Constant.events;
        TypeName reminders = Constant.reminders;
        TypeName attendees = Constant.attendees;
        TypeName calendarContract = Constant.calendarContract;
        TypeName cursor = Constant.cursor;

        ClassName nonNull = Constant.nonNull;
        AnnotationSpec annotationNonNull = AnnotationSpec.builder(nonNull).build();

        ClassName suppressLint = Constant.suppressLint;
        AnnotationSpec annotationSuppressLint = AnnotationSpec.builder(suppressLint).addMember("value", "$S", "MissingPermission").build();

        ClassName requiresPermission = Constant.requiresPermission;
        ClassName permission = Constant.permission;
        AnnotationSpec annotationRequiresPermission = AnnotationSpec.builder(requiresPermission).addMember("anyOf", "{$1T.READ_CALENDAR, $1T.WRITE_CALENDAR}", permission).build();

        TypeName uri = Constant.uri;

        //set annotation class
        classGenerateBuilder.addAnnotation(annotationSuppressLint);
        //set properties
        classGenerateBuilder
                .addField(FieldSpec.builder(context, "context", Modifier.PRIVATE).build())
                .addField(FieldSpec.builder(String.class, "ACCOUNT_NAME", Modifier.FINAL, Modifier.PRIVATE).initializer("$S", accountName).build())
                .addField(FieldSpec.builder(String.class, "NAME", Modifier.FINAL, Modifier.PRIVATE).initializer("$S", calenderName).build())
                .addField(FieldSpec.builder(Integer.class, "CALENDAR_COLOR", Modifier.FINAL, Modifier.PRIVATE).initializer(calendarColor).build())
                .addField(FieldSpec.builder(Integer.class, "EVENT_COLOR", Modifier.FINAL, Modifier.PRIVATE).initializer(eventColor).build())
                .addField(FieldSpec.builder(ClassName.get(packageName, nameClass), "calendarContractUtils", Modifier.STATIC, Modifier.PRIVATE).build());
        //set constructor
        MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
        classGenerateBuilder.addMethod(constructor);
        //set method
        classGenerateBuilder
                //getInstance
                .addMethod(MethodSpec.methodBuilder("getInstance")
                        .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED, Modifier.STATIC)
                        .returns(ClassName.get(packageName, nameClass))
                        .addCode(" if (calendarContractUtils == null) calendarContractUtils = new $N();\n" +
                                " return calendarContractUtils;\n", nameClass)
                        .build())
                //setupContext
                .addMethod(MethodSpec.methodBuilder("setupContext")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(context.annotated(annotationNonNull), "context")
                        .addCode("this.context = context.getApplicationContext();")
                        .build())
                //checkCalendarId
                .addMethod(MethodSpec.methodBuilder("checkCalendarId")
                        .addModifiers(Modifier.PRIVATE)
                        .addCode("String[] projection = new String[]{$1T._ID};\n" +
                                "//String selection = $1T.ACCOUNT_NAME + \"=ACCOUNT_NAME AND\" + $1T.ACCOUNT_TYPE + \"=\" + CalendarContract.ACCOUNT_TYPE_LOCAL;\n" +
                                "\n" +
                                "String selection = \"(\" + $1T.ACCOUNT_NAME + \" = ?) AND (\" + $1T.ACCOUNT_TYPE + \" = ?)\";\n" +
                                "String[] selectionArgs = new String[]{ACCOUNT_NAME, $2T.ACCOUNT_TYPE_LOCAL};\n" +
                                "\n" +
                                "// use the same values as above:\n" +
                                "//String[] selArgs = new String[]{\"ACCOUNT_NAME\", $2T.ACCOUNT_TYPE_LOCAL};\n" +
                                "\n" +
                                "$3T cursor = context.getContentResolver().query($1T.CONTENT_URI,\n" +
                                "                projection,\n" +
                                "                selection,\n" +
                                "                selectionArgs,\n" +
                                "                null);\n" +
                                "if (cursor != null) {\n" +
                                "      if (cursor.moveToFirst()) {\n" +
                                "           return cursor.getLong(0);\n" +
                                "      }\n" +
                                "}\n" +
                                "return -1;\n", calendar, calendarContract, cursor)
                        .returns(TypeName.LONG)
                        .addJavadoc("Kiểm tra calendar có tài khoản local không\n@return\n")
                        .build())
                //checkCalendarId
                .addMethod(MethodSpec.methodBuilder("checkCalendarId")
                        .addParameter(ParameterSpec.builder(String.class, "accountName").build())
                        .addModifiers(Modifier.PRIVATE)
                        .addCode("       String[] projection = new String[]{Calendars._ID};\n" +
                                "        //String selection = Calendars.ACCOUNT_NAME + \"=accountName AND\" + Calendars.ACCOUNT_TYPE + \"=\" + CalendarContract.ACCOUNT_TYPE_LOCAL;\n" +
                                "\n" +
                                "        String selection = \"(\" + Calendars.ACCOUNT_NAME + \" = ?) AND (\" + Calendars.ACCOUNT_TYPE + \" = ?) AND (\" + Calendars.CALENDAR_DISPLAY_NAME + \"= ? )\";\n" +
                                "        String[] selectionArgs = new String[]{accountName, \"com.google\", accountName};\n" +
                                "\n" +
                                "        // use the same values as above:\n" +
                                "        //String[] selArgs = new String[]{\"ACCOUNT_NAME\", CalendarContract.ACCOUNT_TYPE_LOCAL};\n" +
                                "\n" +
                                "        Cursor cursor = context.getContentResolver().query(Calendars.CONTENT_URI,\n" +
                                "                projection,\n" +
                                "                selection,\n" +
                                "                selectionArgs,\n" +
                                "                null);\n" +
                                "        if (cursor != null) {\n" +
                                "            if (cursor.moveToFirst()) {\n" +
                                "                return cursor.getLong(0);\n" +
                                "            }\n" +
                                "        }\n" +
                                "        return -1;\n")
                        .returns(TypeName.LONG)
                        .addJavadoc("Kiểm tra tài khoản gmail của google, đồng bộ trên đt\n@param accountName\n@return\n")
                        .build())
                //createCalendar
                .addMethod(MethodSpec.methodBuilder("createCalendar")
                        .addParameter(ParameterSpec.builder(String.class, "ownerAccount").build())
                        .addAnnotation(annotationRequiresPermission)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.LONG)
                        .addCode("       $1T values = new ContentValues();\n" +
                                "        long idCalendarGoogle = checkCalendarId(ownerAccount);\n" +
                                "        long idCalendar = checkCalendarId();\n" +
                                "\n" +
                                "        if (idCalendarGoogle == -1) {\n" +
                                "            if (idCalendar == -1) {\n" +
                                "                values.put(Calendars.ACCOUNT_NAME, ACCOUNT_NAME);\n" +
                                "                values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);\n" +
                                "                values.put(Calendars.NAME, NAME);\n" +
                                "                values.put(Calendars.CALENDAR_DISPLAY_NAME, NAME);\n" +
                                "                values.put(Calendars.CALENDAR_COLOR, CALENDAR_COLOR);\n" +
                                "                values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);\n" +
                                "                values.put(Calendars.OWNER_ACCOUNT, ownerAccount);\n" +
                                "                values.put(Calendars.CALENDAR_TIME_ZONE, $2T.getDefault().getID());\n" +
                                "\n" +
                                "                $3T.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();\n" +
                                "                builder.appendQueryParameter(Calendars.ACCOUNT_NAME, ACCOUNT_NAME);\n" +
                                "                builder.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);\n" +
                                "                builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, \"true\");\n" +
                                "                Uri uri = context.getContentResolver().insert(builder.build(), values);\n" +
                                "                return Long.parseLong(uri.getLastPathSegment());\n" +
                                "            } else {\n" +
                                "                return idCalendar;\n" +
                                "            }\n" +
                                "        } else {\n" +
                                "            return idCalendarGoogle;\n" +
                                "        }\n", contentValues, TimeZone.class, uri)
                        .addJavadoc("Calendar sẽ được tạo nếu calendar Google k tồn tại tức idCalendarGoogle = -1\n")
                        .addJavadoc("và calendar local k tồn tại tức idCalendar = -1\n")
                        .addJavadoc("@param ownerAccount\n")
                        .addJavadoc("@return\n")
                        .build())
                //checkEvent(String title)
                .addMethod(MethodSpec.methodBuilder("checkEvent")
                        .addParameter(ParameterSpec.builder(String.class, "title").build())
                        .returns(TypeName.LONG)
                        .addModifiers(Modifier.PRIVATE)
                        .addCode("      String[] projection = new String[]{\n" +
                                "                $T._ID,\n" +
                                "                Events.TITLE};\n" +
                                "        String selection = Events.TITLE + \"=\" + title;\n" +
                                "        Cursor cur = context.getContentResolver().query(Events.CONTENT_URI, projection, selection, null, null);\n" +
                                "        if (cur != null) {\n" +
                                "            if (cur.moveToFirst()) {\n" +
                                "                return cur.getLong(0);\n" +
                                "            }\n" +
                                "        }\n" +
                                "        return -1;\n", events)
                        .build())
                //checkEvent(String title,long timeStart,long timeEnd)
                .addMethod(MethodSpec.methodBuilder("checkEvent")
                        .returns(TypeName.LONG)
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(ParameterSpec.builder(String.class, "title").build())
                        .addParameter(ParameterSpec.builder(TypeName.LONG, "timeStart").build())
                        .addParameter(ParameterSpec.builder(TypeName.LONG, "timeEnd").build())
                        .addCode("String[] projection = new String[]{\n" +
                                "                $T._ID,\n" +
                                "                Events.TITLE,\n" +
                                "                Events.DTSTART,\n" +
                                "                Events.DTEND};\n" +
                                "        String selection = \"(\" + Events.TITLE + \"= ? ) AND ( \" + Events.DTSTART + \" =? ) AND ( \" + Events.DTEND + \" =? )\";\n" +
                                "        String[] selectionArgs = new String[]{title, String.valueOf(timeStart), String.valueOf(timeEnd)};\n" +
                                "        Cursor cur = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);\n" +
                                "        if (cur != null) {\n" +
                                "            if (cur.moveToFirst()) {\n" +
                                "                return cur.getLong(0);\n" +
                                "            }\n" +
                                "        }\n" +
                                "        return -1;\n", events)
                        .build())
                //checkAndCreateEvent
                .addMethod(MethodSpec.methodBuilder("checkAndCreateEvent")
                        .addAnnotation(annotationRequiresPermission)
                        .returns(TypeName.LONG)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(String.class, "account").build())
                        .addParameter(ParameterSpec.builder(TypeName.LONG, "timeStart").build())
                        .addParameter(ParameterSpec.builder(TypeName.LONG, "timeEnd").build())
                        .addParameter(ParameterSpec.builder(String.class, "title").build())
                        .addParameter(ParameterSpec.builder(String.class, "description").build())
                        .addParameter(ParameterSpec.builder(String.class, "location").build())
                        .addCode("      ContentValues calEvent = new ContentValues();\n" +
                                "\n" +
                                "        long start = (long) (Math.floor(timeStart / 1000) * 1000);\n" +
                                "        long end = (long) (Math.floor(timeEnd / 1000) * 1000);\n" +
                                "\n" +
                                "        long eventID = checkEvent(title, start, end);\n" +
                                "        long idCalendar = createCalendar(account);\n" +
                                "        if (eventID == -1) {\n" +
                                "            calEvent.put(Events.CALENDAR_ID, idCalendar); // XXX pick)\n" +
                                "            calEvent.put(Events.TITLE, title);\n" +
                                "            calEvent.put(Events.DTSTART, start);\n" +
                                "            calEvent.put(Events.DTEND, end);\n" +
                                "            calEvent.put(Events.EVENT_COLOR, EVENT_COLOR);\n" +
                                "            calEvent.put(Events.DESCRIPTION, description);\n" +
                                "            calEvent.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());\n" +
                                "            calEvent.put(Events.EVENT_LOCATION, location);\n" +
                                "            calEvent.put(Events.STATUS, 1);\n" +
                                "            Uri uri = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, calEvent);\n" +
                                "            return Long.parseLong(uri.getLastPathSegment());\n" +
                                "        }\n" +
                                "        return eventID;\n")
                        .addJavadoc("do tài khoản google convert timestart và timeend sang minutes nên để kiểm tra được trong tài khoản google trùng với timestart and timeend\n")
                        .addJavadoc("ta phải convert về timestamp minutes (tức 10 số) rồi * 1000\n")
                        .addJavadoc("ví dụ: khi nhập timestamp theo milliseconds 1234567890123 thì những tài khoản owner calendar có contains là com.google sẽ được convert thành 1234567890000, còn\n")
                        .addJavadoc("những tài khoản được tạo bởi local sẽ đc giữ nguyên timestamp là 1234567890123.\n")
                        .addJavadoc("@param account\n")
                        .addJavadoc("@param timeStart\n")
                        .addJavadoc("@param timeEnd\n")
                        .addJavadoc("@param title\n")
                        .addJavadoc("@param description\n")
                        .addJavadoc("@param location\n")
                        .addJavadoc("@return\n")
                        .build())
                //updateEvent
                .addMethod(MethodSpec.methodBuilder("updateEvent")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(annotationRequiresPermission)
                        .addParameter(TypeName.LONG, "eventID")
                        .addParameter(String.class, "column")
                        .addParameter(String.class, "value")
                        .addCode("       ContentValues values = new ContentValues();\n" +
                                "        values.put(column, value);\n" +
                                "        Uri updateUri = $T.withAppendedId(Events.CONTENT_URI, eventID);\n" +
                                "        context.getContentResolver().update(updateUri, values, null, null);\n", contentUris)
                        .build())
                //deleteEvent
                .addMethod(MethodSpec.methodBuilder("deleteEvent")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(annotationRequiresPermission)
                        .addParameter(TypeName.LONG, "eventID")
                        .addCode("       Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);\n" +
                                "        context.getContentResolver().delete(deleteUri, null, null);\n")
                        .build())
                //checkRemind
                .addMethod(MethodSpec.methodBuilder("checkRemind")
                        .addParameter(TypeName.LONG, "eventID")
                        .addParameter(TypeName.LONG, "minutes")
                        .returns(TypeName.LONG)
                        .addModifiers(Modifier.PRIVATE)
                        .addCode("String[] projection = new String[]{$T._ID};\n" +
                                "        String selection = \"((\" + Reminders.EVENT_ID + \"=? ) AND (\" + Reminders.MINUTES + \"=? )) \";\n" +
                                "        String[] selectionArgs = new String[]{String.valueOf(eventID), String.valueOf(minutes)};\n" +
                                "        if (Reminders.CONTENT_URI != null) {\n" +
                                "            Cursor cur = context.getContentResolver().query(Reminders.CONTENT_URI, projection, selection, selectionArgs, null);\n" +
                                "            if (cur != null) {\n" +
                                "                if (cur.moveToFirst()) {\n" +
                                "                    return cur.getLong(0);\n" +
                                "                }\n" +
                                "            }\n" +
                                "        }\n" +
                                "        return -1;\n", reminders)
                        .build())
                //checkAndInsertRemind
                .addMethod(MethodSpec.methodBuilder("checkAndInsertRemind")
                        .addAnnotation(annotationRequiresPermission)
                        .returns(TypeName.LONG)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.LONG, "eventID")
                        .addParameter(TypeName.LONG, "minutes")
                        .addCode("long idReminder = checkRemind(eventID, minutes);\n" +
                                "        if (idReminder == -1) {\n" +
                                "            ContentValues values = new ContentValues();\n" +
                                "            values.put(Reminders.MINUTES, minutes);\n" +
                                "            values.put(Reminders.EVENT_ID, eventID);\n" +
                                "            values.put(Reminders.METHOD, Reminders.METHOD_ALERT);\n" +
                                "            Uri uri = context.getContentResolver().insert(Reminders.CONTENT_URI, values);\n" +
                                "            return Long.parseLong(uri.getLastPathSegment());\n" +
                                "        }\n" +
                                "        return idReminder;\n")
                        .build())
                //insertAttendees
                .addMethod(MethodSpec.methodBuilder("insertAttendees")
                        .addAnnotation(annotationRequiresPermission)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.LONG, "eventID")
                        .addParameter(String.class, "name")
                        .addParameter(String.class, "email")
                        .addCode("ContentValues values = new ContentValues();\n" +
                                "        values.put($T.ATTENDEE_NAME, name);\n" +
                                "        values.put(Attendees.ATTENDEE_EMAIL, email);\n" +
                                "        values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ATTENDEE);\n" +
                                "        values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);\n" +
                                "        values.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_INVITED);\n" +
                                "        values.put(Attendees.EVENT_ID, eventID);\n" +
                                "        Uri uri = context.getContentResolver().insert(Attendees.CONTENT_URI, values);\n", attendees)
                        .build())
                //getAllCalendarColumns
                .addMethod(MethodSpec.methodBuilder("getAllCalendarColumns")
                        .addAnnotation(annotationRequiresPermission)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addCode("String[] CALENDAR_QUERY_COLUMNS = {\n" +
                                "                Calendars._ID,\n" +
                                "                Calendars.NAME,\n" +
                                "                Calendars.VISIBLE,\n" +
                                "                Calendars.OWNER_ACCOUNT,\n" +
                                "                Calendars.ACCOUNT_NAME,\n" +
                                "                Calendars.ACCOUNT_TYPE\n" +
                                "        };\n" +
                                "\n" +
                                "        StringBuilder stringBuilder = new StringBuilder();\n" +
                                "        $1T<String> calendarID = new $2T<>();\n" +
                                "        Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, CALENDAR_QUERY_COLUMNS, null, null, null);\n" +
                                "\n" +
                                "        if (cursor != null) {\n" +
                                "            while (cursor.moveToNext()) {\n" +
                                "                String _id = cursor.getString(0);\n" +
                                "                String displayName = cursor.getString(1);\n" +
                                "                Boolean selected = !cursor.getString(2).equals(\"0\");\n" +
                                "                String ownerAccount = cursor.getString(3);\n" +
                                "                String accountName = cursor.getString(4);\n" +
                                "                String accountType = cursor.getString(5);\n" +
                                "                if (!accountName.contains(\"com.google\")) {\n" +
                                "                    calendarID.add(_id);\n" +
                                "                }\n" +
                                "                stringBuilder.append(\"Calendar: Id: \" + _id + \", Display Name: \" + displayName + \" ,Selected: \" + selected + \", AccountName: \" + accountName + \", OWNERAccount: \" + ownerAccount + \", AccountType: \" + accountType + \"\\n\");\n" +
                                "            }\n" +
                                "        }\n" +
                                "        return stringBuilder.toString();\n", List.class, ArrayList.class)
                        .build())
                //getAllEventColumns
                .addMethod(MethodSpec.methodBuilder("getAllEventColumns")
                        .addAnnotation(annotationRequiresPermission)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addParameter(String.class, "title")
                        .addCode("StringBuilder stringBuilder = new StringBuilder();\n" +
                                "String[] projection = new String[]{\n" +
                                "                Events._ID,\n" +
                                "                Events.TITLE,\n" +
                                "                Events.DTSTART,\n" +
                                "                Events.DTEND};\n" +
                                "String selection = Events.TITLE + \"= ?\";\n" +
                                "String[] selectionArgs = new String[]{title};\n" +
                                "Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);\n" +
                                "        if (cursor != null) {\n" +
                                "            while (cursor.moveToNext()) {\n" +
                                "                String _id = cursor.getString(0);\n" +
                                "                String titleEvent = cursor.getString(1);\n" +
                                "                String dtstart = cursor.getString(2);\n" +
                                "                String dtend = cursor.getString(3);\n" +
                                "\n" +
                                "                stringBuilder.append(\"Events: Id: \" + _id + \", title : \" + titleEvent + \" ,dtstart: \" + dtstart + \", dtend: \" + dtend + \"\\n\");\n" +
                                "            }\n" +
                                "        }\n" +
                                "return stringBuilder.toString();\n")
                        .build());

        TypeSpec classGenerate = classGenerateBuilder.build();
        JavaFile javaFile = JavaFile.builder(packageName, classGenerate).build();
        try {
            javaFile.writeTo(filer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
