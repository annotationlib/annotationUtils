package com.lib.processor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation sẽ tạo ra CalendarContract xử lý các thao tác trên Calendar Device
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface CalendarContract {
    String accountName();

    String calendarName();

    String calendarColor() default "0xE6A627";

    String eventColor() default "0xf53f7b";
}
