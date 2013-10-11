package com.threedlite.urforms;

import java.util.ArrayList;
import java.util.List;

import com.threedlite.urforms.data.Attribute;
import com.threedlite.urforms.data.AttributeDao;
import com.threedlite.urforms.data.Entity;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AttributesListActivity extends BaseActivity {
	LinearLayout attributesListLayout;
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private ArrayAdapter<Attribute> attributeAdapter = null;
	private Entity currentEntity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		attributesListLayout = new LinearLayout(this);
		attributesListLayout.setOrientation(LinearLayout.VERTICAL);
		
		setContentView(attributesListLayout);
		Bundle bundle = getIntent().getExtras();

		currentEntity = new Entity();
		currentEntity.setName(bundle.getString(ENTITY_NAME));
		currentEntity.setAttributes(getAttributes(currentEntity));
		attributes.addAll(getAttributes(currentEntity));
		
		Button btnAddAttribute = new Button(this);
		btnAddAttribute.setText("Add Field");
		btnAddAttribute.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
			   addAttribute();
			}
		});
		attributesListLayout.addView(btnAddAttribute);

		ListView attributelist = new ListView(this);
		attributeAdapter = new ArrayAdapter<Attribute>(this,
				android.R.layout.simple_list_item_1,
				android.R.id.text1,
				attributes);
		attributelist.setAdapter(attributeAdapter);
		attributelist.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchAttributeDetails(position);
			}
		});
		attributesListLayout.addView(attributelist);
	}
	
	private void launchAttributeDetails(int position) {
		Intent intent = new Intent(this, AttributeDetailsActivity.class);
		intent.putExtra(ENTITY_NAME, currentEntity.getName());
		intent.putExtra("position", position);
		startActivity(intent);
	}
	
	private void addAttribute() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add Field");
		alert.setMessage("Enter new field name");

		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String name = input.getText().toString();
			if (name.length() == 0) return;
			int maxOrder = 0;
			for (Attribute attribute: attributes) {
				if (attribute.getAttributeName().equals(name)) return; // duplicate
				if (attribute.getDisplayOrder() > maxOrder) maxOrder = attribute.getDisplayOrder();
			}
		  
		  	Attribute attribute = new Attribute();
		  	attribute.setAttributeName(name);
		  	attribute.setAttributeDesc(name);
		  	attribute.setDisplayOrder(maxOrder+1);
		  	attribute.setEntityName(currentEntity.getName());
		  	try {
		  		attribute = new AttributeDao(sqlHelper.getWritableDatabase()).save(attribute);
		  	} finally {
		  		sqlHelper.close();
		  	}
		  	attributes.add(attribute);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    
		  }
		});

		alert.show();
	}
}