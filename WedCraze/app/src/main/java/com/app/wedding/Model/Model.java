package com.app.wedding.Model;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by amsyt007 on 21/4/16.
 */
public class Model extends JsonModel {
    public Model(String data) {
        super(data);
    }

    public Model() {
    }


    public String getToken(){
        return  getString(TOKEN);
    }
    public Model(JSONObject model) {
        super(model);
    }

    public String getName(){
        return getString(NAME);
    }
    public String getId(){
        return getString(ID);
    }
    public String getAbout(){
        return getString(ABOUT);
    }
    public String getRelation(){
        return getString(RELATION);
    }
    public String getDateTime(){
        return getString(DATE_TIME);
    }
    public String getTitle(){
        return getString(TITLE);
    }public String getAddress1(){
        return getString(ADDRESS1);
    }public String getAddress2(){
        return getString(ADDRESS2);
    }public String getState(){
        return getString(STATE);
    }public String getCity(){
        return getString(CITY);
    }public String getPincode(){
        return getString(PINCODE);
    }public String getCountry(){
        return getString(COUNTRY);
    }
    public String getImagePath(){
        return getString(PATH);
    }
    public String getCreatedBy(){
        return getString(CREATED_BY);
    }public String getComment(){
        return getString(COMMENT);
    }public String getCreatedOn(){
        return getString(CREATED_ON);
    }public String getProfileImage(){
        return getString(PROFILE_IMAGE);
    }public String getUrl(){
        return getString(URL);
    }
    public String getMessage(){
        return getString(MESSAGE);
    } public String getText(){
        return getString(TEXT);
    }public boolean isLike(){
        return getBool(ISLIKE);
    } public String getUserName(){
        return getString(USER_NAME);
    }public String getProfilePic(){
        return getString(PROFILE_PIC);
    } public String getVenuee(){
        return getString(VENUE);
    }public int getComments(){
        return getInt(COMMENTS);
    }public int getLikes(){
        return getInt(LIKES);
    }

    public Model[] getPeopleModel() {
        return getArray(getString(PEOPLE));
    }
    public Model[] getPagesArray() {
        return getArray(getString(PAGES));
    }
}
