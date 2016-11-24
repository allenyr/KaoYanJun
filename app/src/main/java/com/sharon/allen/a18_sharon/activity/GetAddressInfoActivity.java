package com.sharon.allen.a18_sharon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.utils.GetAddressUtil;

import java.util.List;

public class GetAddressInfoActivity extends AppCompatActivity {
	
	List<String> addressList = null;
	boolean isCityChoose = false;
	String province = null;
	String city = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_info);
		
		final GetAddressUtil location = new GetAddressUtil(this);
		addressList = location.getProvinceList();
		
		
		ListView listView = (ListView)findViewById(R.id.listview);
		final ProvinceAdapter adapter = new ProvinceAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if (!isCityChoose) {
					province = addressList.get(arg2);
					addressList = location.getCityList(addressList.get(arg2));
					if (addressList.size() == 1) {
						Intent intent = new Intent();
						intent.putExtra("province", province);
						
						if (!addressList.get(0).equals(province)) {
							intent.putExtra("city", addressList.get(0));
							intent.putExtra("code", location.getCode(addressList.get(0)));
						}else{
							intent.putExtra("code", location.getCode(province));
						}
						
						setResult(Activity.RESULT_OK, intent);
						finish();
						return;
					}
					adapter.notifyDataSetChanged();
					isCityChoose = true;
				}else{
					city = addressList.get(arg2);
					Intent intent = new Intent();
					intent.putExtra("province", province);
					intent.putExtra("city", city);
					intent.putExtra("code", location.getCode(city));
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
				
			}
		});
	}
	
	class ProvinceAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return addressList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return addressList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = GetAddressInfoActivity.this.getLayoutInflater().inflate(R.layout.item_addressinfo, null);
				convertView = view;
				convertView.setTag(view);
			}else{
				view = (View)convertView.getTag();
			}
			TextView text = (TextView)view.findViewById(R.id.item_address_city);
			text.setText(addressList.get(position));
			return convertView;
		}
		
	}
	

}
