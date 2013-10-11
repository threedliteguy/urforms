package com.threedlite.urforms;

import com.threedlite.urforms.data.Attribute;
import com.threedlite.urforms.data.AttributeDao;
import com.threedlite.urforms.data.Entity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AttributeDetailsActivity extends BaseActivity {
	
	private Entity currentEntity = null;
	private Attribute currentAttribute = null;
	private int currentPosition;
	
	private EditText etFieldName = null;
	private EditText etFieldDescription = null; 
	private Spinner spDatatype  = null;
	private EditText etRefType = null;
	private CheckBox cbKey = null;
	private CheckBox cbRequired = null;
	private CheckBox cbSearchable = null;
	private CheckBox cbListable = null;
	private CheckBox cbEntityDescription = null;
	private EditText etChoices = null;
	private EditText etValidationRegex = null;
	private EditText etValidationExample = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout fieldDetails = new LinearLayout(this);
		fieldDetails.setOrientation(LinearLayout.VERTICAL);
		setContentView(fieldDetails);
		
		Button btnSave = new Button(this);
		btnSave.setText("Save Field Definition");
		btnSave.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				saveFieldDetails();
			}
		});
		fieldDetails.addView(btnSave);
		
		TextView tvFieldName = new TextView(this);
		tvFieldName.setText("Field Name");
		fieldDetails.addView(tvFieldName);
		etFieldName = new EditText(this);
		fieldDetails.addView(etFieldName);

		TextView tvFieldDescription = new TextView(this);
		tvFieldDescription.setText("Field Description");
		fieldDetails.addView(tvFieldDescription);
		etFieldDescription = new EditText(this);
		fieldDetails.addView(etFieldDescription);

		TextView tvDatatype = new TextView(this);
		tvDatatype.setText("Data type");
		fieldDetails.addView(tvDatatype);
		spDatatype = new Spinner(this);
		ArrayAdapter<CharSequence> adapter = 
				new ArrayAdapter<CharSequence>(this,
						android.R.layout.simple_spinner_item,
						Attribute.DATA_DESCS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spDatatype.setAdapter(adapter);
    	fieldDetails.addView(spDatatype);
    	
		TextView tvRefType = new TextView(this);
		tvRefType.setText("Form name (if reference type), or Form name Field name (if referencedBy type)");
		fieldDetails.addView(tvRefType);
		etRefType = new EditText(this);
		fieldDetails.addView(etRefType);

    	cbKey = new CheckBox(this);
    	cbKey.setText("Key");
    	fieldDetails.addView(cbKey);
		
    	cbRequired = new CheckBox(this);
    	cbRequired.setText("Required");
    	fieldDetails.addView(cbRequired);
		
    	cbSearchable = new CheckBox(this);
    	cbSearchable.setText("Search");
    	fieldDetails.addView(cbSearchable);
		
    	cbListable = new CheckBox(this);
    	cbListable.setText("List");
    	fieldDetails.addView(cbListable);
		
    	cbEntityDescription = new CheckBox(this);
    	cbEntityDescription.setText("Short Desc");
    	fieldDetails.addView(cbEntityDescription);
		
		TextView tvChoices = new TextView(this);
		tvChoices.setText("Choices");
		fieldDetails.addView(tvChoices);
		etChoices = new EditText(this);
		fieldDetails.addView(etChoices);

		TextView tvValidation = new TextView(this);
		tvValidation.setText("Validation regex");
		fieldDetails.addView(tvValidation);
		etValidationRegex = new EditText(this);
		fieldDetails.addView(etValidationRegex);
    	
		TextView tvExample = new TextView(this);
		tvExample.setText("Validation example");
		fieldDetails.addView(tvExample);
		etValidationExample = new EditText(this);
		fieldDetails.addView(etValidationExample);
		
		Bundle bundle = getIntent().getExtras();

		currentEntity = new Entity();
		currentEntity.setName(bundle.getString(ENTITY_NAME));
		currentEntity.setAttributes(getAttributes(currentEntity));
		currentPosition = bundle.getInt("position");
		currentAttribute = getAttributes(currentEntity).get(currentPosition);
		readFieldDetails(currentAttribute);
	}
	
	private void saveFieldDetails() {
		if (currentAttribute == null) return;
		currentAttribute.setAttributeName(etFieldName.getText().toString());
		currentAttribute.setAttributeDesc(etFieldDescription.getText().toString());
		int position = spDatatype.getSelectedItemPosition();
		currentAttribute.setDataType(Attribute.DATA_TYPES[position]);
		currentAttribute.setRefEntityName(etRefType.getText().toString());
		currentAttribute.setPrimaryKeyPart(cbKey.isChecked());
		currentAttribute.setRequired(cbRequired.isChecked());
		currentAttribute.setSearchable(cbSearchable.isChecked());
		currentAttribute.setListable(cbListable.isChecked());
		currentAttribute.setEntityDescription(cbEntityDescription.isChecked());
		currentAttribute.setChoices(etChoices.getText().toString());
		currentAttribute.setValidationRegex(etValidationRegex.getText().toString());
		currentAttribute.setValidationExample(etValidationExample.getText().toString());
		
		try {
			currentAttribute = new AttributeDao(sqlHelper.getWritableDatabase()).save(currentAttribute);
			makeToast("Saved.");
			finish();
		} finally {
			sqlHelper.close();
		}
	}
	
	private void readFieldDetails(Attribute attribute) {
		etFieldDescription.setText(attribute.getAttributeDesc());
		etFieldName.setText(attribute.getAttributeName());
		int position = 0;
		for (int i = 0; i < Attribute.DATA_TYPES.length; i++) {
			if (Attribute.DATA_TYPES[i].equals(attribute.getDataType())) {
				position = i;
				break;
			}
		}
		spDatatype.setSelection(position);
		etRefType.setText(attribute.getRefEntityName());
		cbKey.setChecked(attribute.isPrimaryKeyPart());
		cbRequired.setChecked(attribute.isRequired());
		cbSearchable.setChecked(attribute.isSearchable());
		cbListable.setChecked(attribute.isListable());
		cbEntityDescription.setChecked(attribute.isEntityDescription());
		etChoices.setText(attribute.getChoices());
		etValidationRegex.setText(attribute.getValidationRegex());
		etValidationExample.setText(attribute.getValidationExample());
	}

}