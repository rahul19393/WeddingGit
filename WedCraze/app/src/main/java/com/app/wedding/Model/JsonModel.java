package com.app.wedding.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonModel implements NamePair{
	private JSONObject jObject;
	private String m;
	
	public JsonModel(String data){
		// TODO Auto-generated constructor stub		
		try {
			jObject = new JSONObject(data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JsonModel() {
		// TODO Auto-generated constructor stub
	}

	public JSONObject getJsonObject(){
		return jObject;
	}

	public Iterator<String> getObjectKeys(){
		return jObject != null ? jObject.keys() : null;
	}


	public JsonModel(JSONObject model) {
		jObject = model;
	}

	public Iterator<?> getKeys(){
		return jObject.keys();
	}
	
	public boolean isKeyNull(String key){
		return jObject.isNull(key);
	}
	public JSONObject getObject(String key){
		JSONObject obj = null;
		try {
			obj = jObject.getJSONObject(key);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return obj;
		
	}

	public JSONArray getJSONArray(String key){
		JSONArray array = null;
		try {
			array = jObject == null ? null  : jObject.getJSONArray(key);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return array;

	}

	public Model[] getArray(String data) {
		try {
			JSONArray jArray = new JSONArray(data);
			Model[] concatArray = new Model[jArray.length()];
			for (int i = 0; i < concatArray.length; i++) {
				Model c = new Model(jArray.getJSONObject(i));
				concatArray[i] = c;
			}
			return concatArray;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	public Model[] getArray(JSONArray array) {
		if (array == null)
			return null;

		try {
			Model[] concatArray = new Model[array.length()];
			for (int i = 0; i < concatArray.length; i++) {
				Model jm = new Model(array.getJSONObject(i));
				concatArray[i] = jm;
			}
			return concatArray;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public String getString(String key){
		String value = "";
		if(jObject != null && jObject.has(key)){
			try {
				value = jObject.getString(key);
			} catch (JSONException e) {e.printStackTrace();}
		}	
		
		return value;
	}

	public long getLong(String key){
		long value = 0;
		if(jObject != null && jObject.has(key)){
			try {
				value = jObject.getLong(key);
			} catch (JSONException e) {e.printStackTrace();}
		}
		return value;
	}

	public boolean getBool(String key){
		boolean b = false;
		if(jObject != null && jObject.has(key)){
			try {
				b = jObject.getBoolean(key);
			} catch (JSONException e) {e.printStackTrace();}
		}

		return b;
	}

	public int getInt(String key){
		int value = 0;
		if(jObject != null && jObject.has(key)){
			try {
				value = jObject.getInt(key);
			} catch (JSONException e) {e.printStackTrace();}
		}
		return value;
		
	}

	public double getDouble(String key){
		double value = 0;
		if(jObject != null && jObject.has(key)){
			try {
				value = jObject.getDouble(key);
			} catch (JSONException e) {e.printStackTrace();}
		}
		return value;

	}


}