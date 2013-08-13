package com.example.bigthought;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class BigThought extends Activity {

	final int RESULT_LOAD_IMAGE = 1;
	final int PIC_CROP = 2;
	private Uri picUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button openButton = (Button) findViewById(R.id.openButton);
		openButton.setOnClickListener(openButtonOnClickListener);
	}

	public OnClickListener openButtonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

			startActivityForResult(i, RESULT_LOAD_IMAGE);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == RESULT_LOAD_IMAGE){
			//get the Uri for the captured image
			picUri = data.getData();
			//carry out the crop operation
			crop(picUri);
		}
		//user is returning from cropping the image
		else if(requestCode == PIC_CROP){
			//get the returned data
			Bundle extras = data.getExtras();
			//get the cropped bitmap
			Bitmap thePic = extras.getParcelable("data");
			//retrieve a reference to the ImageView
			ImageView picView = (ImageView)findViewById(R.id.imageView1);
			//display the returned cropped image
			picView.setImageBitmap(thePic);
		}

	}

	private void crop(Uri picUri) {
		try {
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(picUri, "image/*");
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			// indicate output X and Y
			cropIntent.putExtra("outputX", 100);
			cropIntent.putExtra("outputY", 100);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);

			startActivityForResult(cropIntent, PIC_CROP);
		} catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Your device sucks";
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.big_thought, menu);
		return true;
	}

}
