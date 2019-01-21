package com.example.eliad.drive4u.helpers;

import com.example.eliad.drive4u.models.User;

import java.util.LinkedList;
import java.util.List;

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
