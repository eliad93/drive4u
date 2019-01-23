package com.base.eliad.drive4u.helpers;

import com.base.eliad.drive4u.models.User;

public final class ConditionsHelper {
    public enum Order{
        DESCENDING,
        ASCENDING;
    }
    private ConditionsHelper(){}
    public static Boolean imageStringValid(String imageString){
        return imageString != null && !imageString.isEmpty() &&
                imageString.equals(User.DEFAULT_IMAGE_KEY);
    }
}
