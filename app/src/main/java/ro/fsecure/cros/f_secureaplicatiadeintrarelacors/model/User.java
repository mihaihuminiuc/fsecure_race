package ro.fsecure.cros.f_secureaplicatiadeintrarelacors.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.utils.CommonUtils;

/**
 * Created by humin on 6/13/2017.
 */

public class User implements Serializable {



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRaceType() {
        return raceType;
    }

    public void setRaceType(String raceType) {
        this.raceType = raceType;
    }

    public String getTeeSheetSize() {
        return teeSheetSize;
    }

    public void setTeeSheetSize(String teeSheetSize) {
        this.teeSheetSize = teeSheetSize;
    }

    public String getRaceKit() {
        return raceKit;
    }

    public void setRaceKit(String raceKit) {
        this.raceKit = raceKit;
    }

    public String[] getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(String[] categoryList) {
        this.categoryList = categoryList;
    }

    public String[] getTeeShirtList() {
        return teeShirtList;
    }

    public void setTeeShirtList(String[] teeShirtList) {
        this.teeShirtList = teeShirtList;
    }

    public String[] getRaceTypeList() {
        return raceTypeList;
    }

    public void setRaceTypeList(String[] raceTypeList) {
        this.raceTypeList = raceTypeList;
    }

    public String[] getKitTypeList() {
        return kitTypeList;
    }

    public void setKitTypeList(String[] kitTypeList) {
        this.kitTypeList = kitTypeList;
    }

    public int userId;
    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
    public String company;
    public String function;
    public String category;
    public String raceType;
    public String teeSheetSize;
    public String raceKit;
    public String contestNumber;

    public String[] categoryList;
    public String[] teeShirtList;
    public String[] raceTypeList;
    public String[] kitTypeList;

    public User(JSONObject jsonObject) throws JSONException{

        if (jsonObject.has("id") && !jsonObject.isNull("id") && !jsonObject.getString("id").equalsIgnoreCase("null")) {
            userId = jsonObject.getInt("id");
        }

        if (jsonObject.has("part_last_name") && !jsonObject.isNull("part_last_name") && !jsonObject.getString("part_last_name").equalsIgnoreCase("null")) {
            lastName = jsonObject.getString("part_last_name");
        }

        if (jsonObject.has("part_first_name") && !jsonObject.isNull("part_first_name") && !jsonObject.getString("part_first_name").equalsIgnoreCase("null")) {
            firstName = jsonObject.getString("part_first_name");
        }

        if (jsonObject.has("email") && !jsonObject.isNull("email") && !jsonObject.getString("email").equalsIgnoreCase("null")) {
            email = jsonObject.getString("email");
        }

        if (jsonObject.has("telephone") && !jsonObject.isNull("telephone") && !jsonObject.getString("telephone").equalsIgnoreCase("null")) {
            phoneNumber = jsonObject.getString("telephone");
        }

        if (jsonObject.has("company") && !jsonObject.isNull("company") && !jsonObject.getString("company").equalsIgnoreCase("null")) {
            company = jsonObject.getString("company");
        }

        if (jsonObject.has("function") && !jsonObject.isNull("function") && !jsonObject.getString("function").equalsIgnoreCase("null")) {
            function = jsonObject.getString("function");
        }

        if (jsonObject.has("category") && !jsonObject.isNull("category") && !jsonObject.getString("category").equalsIgnoreCase("null")) {
            category = jsonObject.getString("category");
        }

        if (jsonObject.has("race") && !jsonObject.isNull("race") && !jsonObject.getString("race").equalsIgnoreCase("null")) {
            raceType = jsonObject.getString("race");
        }

        if (jsonObject.has("size") && !jsonObject.isNull("size") && !jsonObject.getString("size").equalsIgnoreCase("null")) {
            teeSheetSize = jsonObject.getString("size");
        }

        if (jsonObject.has("type") && !jsonObject.isNull("type") && !jsonObject.getString("type").equalsIgnoreCase("null")) {
            raceKit = jsonObject.getString("type");
        }

        if (jsonObject.has("numar_participare") && !jsonObject.isNull("numar_participare") && !jsonObject.getString("numar_participare").equalsIgnoreCase("null")) {
            contestNumber = jsonObject.getString("numar_participare");
        }

        if (jsonObject.has("category_list") && !jsonObject.isNull("category_list") && !jsonObject.getString("category_list").equalsIgnoreCase("null")) {
            categoryList = CommonUtils.parseStandardListsForName(jsonObject.getString("category_list"), "category_list");
        }

        if (jsonObject.has("tee_shirt_list") && !jsonObject.isNull("tee_shirt_list") && !jsonObject.getString("tee_shirt_list").equalsIgnoreCase("null")) {
            teeShirtList = CommonUtils.parseStandardListsForName(jsonObject.getString("category_list"), "tee_shirt_list");
        }

        if (jsonObject.has("race_type_list") && !jsonObject.isNull("race_type_list") && !jsonObject.getString("race_type_list").equalsIgnoreCase("null")) {
            raceTypeList = CommonUtils.parseStandardListsForName(jsonObject.getString("category_list"), "race_type_list");
        }

        if (jsonObject.has("kit_type_list") && !jsonObject.isNull("kit_type_list") && !jsonObject.getString("kit_type_list").equalsIgnoreCase("null")) {
            kitTypeList = CommonUtils.parseStandardListsForName(jsonObject.getString("category_list"), "kit_type_list");
        }


    }
}
