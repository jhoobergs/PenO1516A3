package be.cwa3.nightgame.Utils;

import android.content.Context;

import java.lang.reflect.Field;
import java.util.List;

import be.cwa3.nightgame.R;

/**
 * Created by jesse on 7/11/2015.
 */
public class ErrorUtil {
    public static String getErrorText(Context context, List<Integer> errors){
        String errorText= "";
        for (int error: errors) {
            String resource = String.format("error%d", error);
            int id= getId(resource, R.string.class);
            errorText = errorText.concat(context.getString(id));
        }
        return  errorText;
    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }
}
