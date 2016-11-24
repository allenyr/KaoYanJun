package com.sharon.allen.a18_sharon.json;

import java.util.List;
import com.google.gson.Gson;

public class WriteJson {

	public String getJsonData(List<?> list)
	{
		//此处要注意，时常会出现说找不到Gson类的情况，这时我们只需要将导入的包和系统提供换换顺序就行了
		Gson gson=new Gson();
		//利用google提供的gson将一个list集合写成json形式的字符串
		String jsonstring=gson.toJson(list);
		return jsonstring;
	}

}
