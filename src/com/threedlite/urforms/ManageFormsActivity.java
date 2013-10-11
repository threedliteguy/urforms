package com.threedlite.urforms;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.threedlite.urforms.data.Entity;
import com.threedlite.urforms.data.EntityDao;
import com.threedlite.urforms.data.SampleDataPopulator;

public class ManageFormsActivity extends BaseActivity {

	private List<Entity> entities = new ArrayList<Entity>();
	private ArrayAdapter<Entity> entityAdapter = null; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LinearLayout rootView = new LinearLayout(this);
	
		setupEntityList(rootView);
		setContentView(rootView);

		populateInitialData();

	}
	
	private void setupEntityList(ViewGroup parent) {
		
		LinearLayout entityLayout = new LinearLayout(this);
		entityLayout.setOrientation(LinearLayout.VERTICAL);
		parent.addView(entityLayout);
		
		Button btnAddEntity = new Button(this);
		btnAddEntity.setText("Add Form");
		btnAddEntity.requestFocus();
		btnAddEntity.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				addEntity();
			}
		});
		entityLayout.addView(btnAddEntity);
	
		
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
				launchAttributesList(entity);
			}
		});
		entityLayout.addView(entitylist);
		
	}
	
	private void launchAttributesList(Entity entity) {
		Intent intent = new Intent(this, AttributesListActivity.class);
		intent.putExtra(ENTITY_NAME, entity.getName());
		startActivity(intent);
	}

	private void populateInitialData() {

		entities.addAll(getEntities());
		if (entities.size() == 0) {
			new SampleDataPopulator().addSampleData(sqlHelper);
			entities.addAll(getEntities());
		}

	}

	
	private void addEntity() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add Form");
		alert.setMessage("Enter new form name");

		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String name = input.getText().toString();
			if (name.length() == 0) return;
			for (Entity entity: entities) if (entity.getName().equals(name)) return; // duplicate
		  
		  	Entity entity = new Entity();
		  	entity.setName(name);
		  	try {
		  		entity = new EntityDao(sqlHelper.getWritableDatabase()).save(entity);
		  	} finally {
		  		sqlHelper.close();
		  	}
		  	entities.add(entity);
		  	entityAdapter.notifyDataSetChanged();
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    
		  }
		});

		alert.show();
	}
}
