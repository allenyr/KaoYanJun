package com.sharon.allen.a18_sharon.json;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharon.allen.a18_sharon.model.User;


public class JsonUtil {
	public List<?> StringFromJson (String jsondata)
	{     
		Type listType = new TypeToken<List<?>>(){}.getType();
		Gson gson=new Gson();
		ArrayList<?> list=gson.fromJson(jsondata, listType);
		return list;
	}
}
