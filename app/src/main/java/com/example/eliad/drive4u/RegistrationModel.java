package com.example.eliad.drive4u;

import android.databinding.InverseMethod;
import android.databinding.ObservableField;
import android.provider.ContactsContract.CommonDataKinds.Email;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class RegistrationModel {
    private Integer numLegalFields;
    public String mHeader;
    public ObservableField<String> firstName = new ObservableField<>();
    public ObservableField<String> lastName = new ObservableField<>();
    public ObservableField<String> iDNumber = new ObservableField<>();
    public ObservableField<String> dateOfBirth = new ObservableField<>();
    public ObservableField<String> phoneNumber = new ObservableField<>();
    public ObservableField<String> emailAddress = new ObservableField<>();

    public Boolean isLegalInput(){
//        Field[] fields = this.getClass().getFields();
//        ObservableField o = new ObservableField();
//        for(Field f : fields){
//            Type t = f.getGenericType();
//            if(t instanceof ObservableField){
//                try {
//                    ObservableField obsField = (ObservableField)f.get(o);
//                    if(!isLegalField(obsField)){
//                        return false;
//                    }
//                    Object val = obsField.get();
//                    if(val == null){
//                        return false;
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return true; // TODO: check input
    }

    private static Boolean isLegalField(ObservableField obsField){
        return true; // TODO: add logic
    }

    public RegistrationModel(String header){
        mHeader = header;
    }

    public void onFirstNameTextChanged(CharSequence text){
    }

    public void onLastNameTextChanged(CharSequence text){

    }

    public void onIdTextChanged(CharSequence text){

    }

    public void onDateTextChanged(CharSequence text){

    }

    public void onPhoneTextChanged(CharSequence text){

    }

    public void onEmailTextChanged(CharSequence text){

    }
}
