package au.com.reece;

import com.atlassian.bamboo.specs.util.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class CheckRequired {
    public boolean checkRequired() {
        for (Field field : this.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof Required) {
                    try {
                        if (field.get(this) == null) {
                            Logger.getLogger(this.getClass()).info("Missing required yaml value '" + field.getName() + "'");
                            return false;
                        }
                    } catch (IllegalAccessException e) {
                        // literally no idea how this could ever happen
                    }
                }
            }
        }
        return true;
    }
}
