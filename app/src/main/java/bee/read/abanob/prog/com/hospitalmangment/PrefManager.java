package bee.read.abanob.prog.com.hospitalmangment;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {
     Context context;

    public PrefManager(Context context) {
        this.context = context;
    }

    public void saveLoginDetails(Hospital hospital) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Code", hospital.getId());
        editor.putString("Name", hospital.getName());
        editor.putString("lat",hospital.getLat());
        editor.putString("lng",hospital.getLng());
        editor.putString("createdAt",hospital.getCreatedAt());

        editor.commit();
    }


    public Hospital getHospital() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Hospital hospital=new Hospital();
        hospital.setId(sharedPreferences.getString("Code", ""));
        hospital.setName(sharedPreferences.getString("Name", ""));
        hospital.setLat(sharedPreferences.getString("lat", ""));
        hospital.setLng(sharedPreferences.getString("lng", ""));
        hospital.setCreatedAt(sharedPreferences.getString("createdAt", ""));

        return hospital;
    }

    public boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isEmailEmpty = sharedPreferences.getString("Code", "").isEmpty();
        return isEmailEmpty ;
    }

    public void LogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
     }

}