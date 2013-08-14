package com.example.bigthought;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class BigThought extends Activity {

	final int RESULT_LOAD_IMAGE = 1;
	final int PIC_CROP = 2;
	final int CAMERA_CAPTURE = 3;
	final int PHOTO_PICKED = 4;
	private Uri picUri;
	private Bitmap temp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button openButton = (Button) findViewById(R.id.openButton);
		openButton.setOnClickListener(openButtonOnClickListener);

		Button shareButton = (Button) findViewById(R.id.shareButton);
		shareButton.setOnClickListener(shareButtonOnClickListener);

		Button cameraButton = (Button) findViewById(R.id.cameraButton);
		cameraButton.setOnClickListener(cameraButtonOnClickListener);
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

	public OnClickListener cameraButtonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			// use standard intent to capture an image
			Intent captureIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			// we will handle the returned data in onActivityResult
			startActivityForResult(captureIntent, CAMERA_CAPTURE);

		}
	};

	public OnClickListener shareButtonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			share();
		}
	};

	private void share() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

		Bitmap pic = temp;
		String path = Images.Media.insertImage(getContentResolver(), pic,
				"blah", null);
		Uri uri = Uri.parse(path);

		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"Subject here");
		sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
		sharingIntent.setType("image/*");
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE) {
			// get the Uri for the captured image
			picUri = data.getData();
			// carry out the crop operation
			crop(picUri);
		}
		// user is returning from cropping the image
		else if (requestCode == PIC_CROP) {
			// get the returned data
			Bundle extras = data.getExtras();
			// get the cropped bitmap
			Bitmap thePic = extras.getParcelable("data");
			Bitmap bmp = postProcessing(this, thePic, "Inspring as ****");
			try {
				// File f = new File(this.getCacheDir(),"text.jpg");
				// f.createNewFile();
				// //Convert bitmap to byte array
				// ByteArrayOutputStream bos = new ByteArrayOutputStream();
				// bmp.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for
				// PNG*/, bos);
				// byte[] bitmapdata = bos.toByteArray();
				//
				// //write the bytes in file
				// FileOutputStream fos = new FileOutputStream(f);
				// fos.write(bitmapdata);
				// fos.close();
				Bitmap out = bmp;
				String path = Environment.getExternalStorageDirectory()
						.toString();
				Log.d("TestFileName", "HelloNham" + path);
				OutputStream fOut = null;
				File file = new File(path, "/test/hellomoto123.jpg");
				Boolean bol=file.createNewFile();
				Log.d("TestFileName", "bool" + bol.toString());
				String src = file.getAbsolutePath();
				Log.d("TestFileName", "HelloNham" + src);

				fOut = new FileOutputStream(file);

				out.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
				fOut.flush();
				fOut.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			// retrieve a reference to the ImageView

			ImageView picView = (ImageView) findViewById(R.id.imageView1);
			// display the returned cropped image
			picView.setImageBitmap(bmp);
		}

		else if (requestCode == CAMERA_CAPTURE) {
			// get the Uri for the captured image
			picUri = data.getData();
			// carry out the crop operation
			crop(picUri);
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
			cropIntent.putExtra("outputX", 300);
			cropIntent.putExtra("outputY", 300);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);

			// End test
			// done cropping, return out the result
			startActivityForResult(cropIntent, PIC_CROP);
		} catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Your device sucks";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	public Bitmap postProcessing(Context mContext, Bitmap bitmap, String mText) {
		try {
			Resources resources = mContext.getResources();
			float scale = resources.getDisplayMetrics().density;
			// Bitmap bitmap = BitmapFactory.decodeResource(resources,
			// resourceId);

			android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
			// set default bitmap config if none
			if (bitmapConfig == null) {
				bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
			}
			// resource bitmaps are imutable,
			// so we need to convert it to mutable one

			bitmap = bitmap.copy(bitmapConfig, true);
			// bitmap=vintage(bitmap);
			// Test frame
			Bitmap frame = Bitmap.createBitmap(330, 330,
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(frame);
			canvas.drawBitmap(bitmap, 15, 15, null);
			// done testing frame

			// Canvas canvas = new Canvas(bitmap);
			// new antialised Paint
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			// text color - #3D3D3D
			paint.setColor(getResources().getColor(R.color.light));
			// text size in pixels
			paint.setTextSize((int) (12 * scale));
			paint.setAlpha(100);
			// text shadow
			// paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

			// draw text to the Canvas center
			Rect bounds = new Rect();
			paint.getTextBounds(mText, 0, mText.length(), bounds);
			int x = (bitmap.getWidth() - bounds.width()) / 6;
			int y = (bitmap.getHeight() + bounds.height()) / 5;

			canvas.drawText(mText, x * scale, y * scale, paint);

			return frame;
		} catch (Exception e) {
			// TODO: handle exception

			return null;
		}

	}

	public Bitmap vintage(Bitmap bitmap) {
		android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
		// set default bitmap config if none
		if (bitmapConfig == null) {
			bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
		}
		bitmap = bitmap.copy(bitmapConfig, true);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		for (int i = 1; i < 10; i++) {
			int x = (int) (Math.random() * 301);
			int y = (int) (Math.random() * 302);
			bitmap.setPixel(x, y, bitmap.getPixel(x + 1, y + 1));
		}
		return bitmap;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.big_thought, menu);
		return true;
	}

}
