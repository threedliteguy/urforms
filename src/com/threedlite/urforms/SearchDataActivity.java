package com.threedlite.urforms;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.threedlite.urforms.data.Attribute;
import com.threedlite.urforms.data.Entity;
import com.threedlite.urforms.data.EntityDao;


public class SearchDataActivity extends BaseActivity {

	private boolean isNewInstall() {

		SQLiteDatabase database = sqlHelper.getWritableDatabase();
		try {
			EntityDao entityDao = new EntityDao(database);
			entities = entityDao.list();
			if (entities.size() == 0) {
				startActivity(new Intent(this, ManageFormsActivity.class));
				return true;
			}
		} finally {
			sqlHelper.close();
		}
		return false;

	}

	private List<Entity> entities = new ArrayList<Entity>();
	private ArrayAdapter<Entity> entityAdapter = null; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isNewInstall()) return;

		LinearLayout rootView = new LinearLayout(this);

		setupEntityList(rootView);
		setContentView(rootView);
	}

	private void setupEntityList(ViewGroup parent) {

		LinearLayout entityLayout = new LinearLayout(this);
		entityLayout.setOrientation(LinearLayout.VERTICAL);
		parent.addView(entityLayout);

		ListView entitylist = new ListView(this);

		entityAdapter =  new ArrayAdapter<Entity>(this,
				android.R.layout.simple_list_item_1,
				android.R.id.text1,
				entities);
		entitylist.setAdapter(entityAdapter);
		entitylist.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Entity entity = entities.get(position);
				launchSeachView(entity);
			}
		});
		entityLayout.addView(entitylist);

	}
	
	public static boolean isSearchable(Attribute attribute) {
		return attribute.isSearchable() && 
				(
				attribute.getDataType().equals(Attribute.STRING_TYPE) 
				|| attribute.getDataType().equals(Attribute.DATE_TYPE)
				);
	}

	private void launchSeachView(Entity entity) {
		Intent intent = new Intent(this, SearchViewActivity.class);
		intent.putExtra(ENTITY_NAME, entity.getName());
		startActivity(intent);
	}
}
