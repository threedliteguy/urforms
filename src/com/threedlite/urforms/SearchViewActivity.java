package com.threedlite.urforms;

import java.util.List;

import com.threedlite.urforms.data.Attribute;
import com.threedlite.urforms.data.Entity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchViewActivity extends BaseActivity {
	LinearLayout searchViewLayoutContents;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		searchViewLayoutContents = new LinearLayout(this);
		searchViewLayoutContents.setOrientation(LinearLayout.VERTICAL);

		setContentView(searchViewLayoutContents);
		Bundle bundle = getIntent().getExtras();

		final Entity newEntity = new Entity();
		newEntity.setName(bundle.getString(ENTITY_NAME));
		newEntity.setAttributes(getAttributes(newEntity));

		Button btnAddNew = new Button(this);
		btnAddNew.setText("Add new " + newEntity.getName());
		View.OnClickListener ocl = new View.OnClickListener() {
			public void onClick(View v) {
				startEdit(newEntity);
			}
		};
		btnAddNew.setOnClickListener(ocl);
		searchViewLayoutContents.addView(btnAddNew);

		Button btnSearch = new Button(this);
		btnSearch.setText("Search for " + newEntity.getName());
		btnSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				doSearch(newEntity);
			}
		});
		searchViewLayoutContents.addView(btnSearch);

		List<Attribute> attributes = getAttributes(newEntity);
		for (Attribute attribute : attributes) {
			if (isSearchable(attribute)) {

				TextView tvDesc = new TextView(this);
				tvDesc.setText(attribute.getAttributeDesc());
				searchViewLayoutContents.addView(tvDesc);
				EditText etValue = new EditText(this);
				etValue.setTag("S_" + attribute.getAttributeName());
				searchViewLayoutContents.addView(etValue);

			}
		}

	}

	private void doSearch(Entity entity) {

		Intent intent = new Intent(this, SearchResultsActivity.class);
		Bundle bundle = new Bundle();

		for (Attribute attribute : getAttributes(entity)) {
			if (isSearchable(attribute)) {
				String tag = "S_" + attribute.getAttributeName();
				EditText ev = (EditText) searchViewLayoutContents.findViewWithTag(tag);
				bundle.putString(attribute.getAttributeName(), ev.getText().toString());
			}
		}
		bundle.putString(EnterDataActivity.ENTITY_NAME, entity.getName());

		intent.putExtras(bundle);
		startActivity(intent);

	}

	public static boolean isSearchable(Attribute attribute) {
		return attribute.isSearchable()
				&& (
						attribute.getDataType().equals(Attribute.STRING_TYPE) 
						|| attribute.getDataType().equals(Attribute.DATE_TYPE)
					);
	}

}
