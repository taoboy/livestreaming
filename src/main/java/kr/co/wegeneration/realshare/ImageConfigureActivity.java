package kr.co.wegeneration.realshare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.widget.CircularNetworkImageView;
import kr.co.wegeneration.realshare.widget.MLRoundedImageView;
import kr.co.wegeneration.realshare.widget.TransparentCircle;

/**
 * Created by user on 2015-11-17.
 */
public class ImageConfigureActivity extends AppCompatActivity implements SubsamplingScaleImageView.OnImageEventListener{

    SubsamplingScaleImageView imageView;
    TransparentCircle transparentCircle;
    Button check;
    String userId ="";
    CircularNetworkImageView mlRoundedImageView;

    String path;

    private float postScale;

    private boolean myImage;

    private final int UPLOAD_PHOTO = 100;
    private final int TAKE_PHOTO = 200;
    private ParseUser user;

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
        image.deleteOnExit();
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_configure);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myImage = getIntent().getBooleanExtra("myImage", false);
        userId  = getIntent().getStringExtra("imageId");
        transparentCircle = (TransparentCircle)findViewById(R.id.transparentCircle);
        check = (Button)findViewById(R.id.check);

        transparentCircle.setVisibility(View.INVISIBLE);
        check.setVisibility(View.INVISIBLE);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //create bitmap that doesn't occupy memory to avoidout of memory error
                BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
                tmpOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                tmpOptions.inJustDecodeBounds = true;
                Bitmap temp = BitmapFactory.decodeFile(path, tmpOptions);

                int rotation = getImageOrientation(path);

                int tmpWidth = tmpOptions.outWidth;
                int tmpHeight = tmpOptions.outHeight;

                int oomScale = 1;

                if(tmpWidth > 2048 || tmpHeight > 2048){
                    double oomScaleD = Math.max((double)tmpWidth / 2048, (double)tmpHeight / 2048);
                    oomScale = (int)Math.round(oomScaleD);
                }

                oomScale *= 4;

                Log.e("TAG", "width : " + tmpWidth + "//" + "height : " + tmpHeight + " // " + "scale : " + oomScale);

                PointF center = imageView.getCenter();
                Float scale = imageView.getScale();

                int w = transparentCircle.getWidth();
                int h = transparentCircle.getHeight();
                int r = w > h ? h/2 : w/2;
                int d = r * 2;

                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = oomScale;
                Bitmap bitmapOrg = Bitmap.createBitmap(BitmapFactory.decodeFile(path, options), 0, 0, tmpWidth / oomScale, tmpHeight / oomScale, matrix, true);;

//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//
//                } else {
//                    bitmapOrg = Bitmap.createBitmap(BitmapFactory.decodeFile(path, options), 0, 0, tmpWidth / oomScale, tmpHeight / oomScale, matrix, true);
//                }
                int width = bitmapOrg.getWidth();
                int height = bitmapOrg.getHeight();

                Float scaleCalc = scale / postScale;

                int newR = (int)(r / scaleCalc);

                Paint paint = new Paint();
                paint.setFilterBitmap(true);

                Bitmap bmp = Bitmap.createBitmap(d, d, Bitmap.Config.RGB_565);

                double radiusCalcFactor = Math.max((double)width / (double)transparentCircle.getWidth(), (double)height / (double)transparentCircle.getHeight());
                newR = (int)((double)newR * radiusCalcFactor);

                int centerNewX = (int) center.x / oomScale;
                int centerNewY = (int) center.y / oomScale;

                Log.e("TAG", "x ::: " + centerNewX + "//  y ::: " + centerNewY);

                Rect rectOrg = new Rect(centerNewX - newR, centerNewY - newR, centerNewX + newR, centerNewY + newR);

                Rect rectNew = new Rect(0, 0, d, d);

                Canvas c = new Canvas(bmp);
                c.drawBitmap(bitmapOrg, rectOrg, rectNew, paint);

                FileOutputStream fileOutputStream = null;

                try {
                    fileOutputStream = new FileOutputStream(path);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);


                    try {

                                File file = new File(path);
                                //File file = createImageFile();
                                ParseNetController.uploadfILEToS3(getBaseContext(), ParseUser.getCurrentUser().getString(Define.DB_USER_ID), "photo", file);

                                file.deleteOnExit();
                                /*byte[] image;
                                image = readInFile(path);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddkkmm", Locale.getDefault());
                                ParseFile file = new ParseFile("profile_" + simpleDateFormat.format(new Date()) + ".png", image);
                                file.saveInBackground();*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if(fileOutputStream != null){
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                SharedPreferences pref = RSPreference.getPreference(ImageConfigureActivity.this);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("profileImage", path);
                editor.apply();

                bmp.recycle();
                bmp = null;

                bitmapOrg.recycle();
                bitmapOrg = null;

                Intent resultIntent = new Intent();
                resultIntent.putExtra("path", path);
                setResult(RESULT_OK, resultIntent);

                finish();
            }
        });

        mlRoundedImageView = (CircularNetworkImageView)findViewById(R.id.imageView);
        imageView = (SubsamplingScaleImageView)findViewById(R.id.image);

        if(getIntent().getStringExtra("imageName").equals("")){
            imageView.setImage(ImageSource.resource(R.drawable.default_profile_image));
        } else {

            //TODO :: image from imageName - now, from device
            imageView.setImage(ImageSource.uri(getIntent().getStringExtra("imageName")));
        }
        imageView.setOnImageEventListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(myImage) {
            getMenuInflater().inflate(R.menu.menu_image, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_upload) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, UPLOAD_PHOTO);
            return true;
        } else if(id == R.id.action_take){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddkkmm", Locale.getDefault());
            path = Environment.getExternalStorageDirectory() + "/" + "profile_" + simpleDateFormat.format(new Date()) + ".png";
            File photo = new File(path);

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            startActivityForResult(intent, TAKE_PHOTO);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private byte[] readInFile(String path) throws IOException {
        // TODO Auto-generated method stub
        byte[] data = null;
        File file = new File(path);
        InputStream input_stream = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        data = new byte[16384]; // 16K
        int bytes_read;
        while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytes_read);
        }
        input_stream.close();
        return buffer.toByteArray();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == UPLOAD_PHOTO){

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddkkmm", Locale.getDefault());
                path = Environment.getExternalStorageDirectory() + "/" + "profile_" + simpleDateFormat.format(new Date()) + ".png";

                Uri selectedImageUri = data.getData();
                String old_path = getPath(selectedImageUri);

                File src = new File(old_path);
                File dst = new File(path);
                try {
                    copy(src, dst);
                }catch (Exception e) {e.printStackTrace(); }

                imageView.setImage(ImageSource.uri(path));

                switch (getImageOrientation(path)){
                    case 270:
                        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_270);
                        break;
                    case 180:
                        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_180);
                        break;
                    case 90:
                        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_90);
                        break;
                }

                check.setVisibility(View.VISIBLE);
                transparentCircle.setVisibility(View.VISIBLE);

            } else  if(requestCode == TAKE_PHOTO){


                imageView.setImage(ImageSource.uri(path));
                switch (getImageOrientation(path)){
                    case 270:
                        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_270);
                        break;
                    case 180:
                        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_180);
                        break;
                    case 90:
                        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_90);
                        break;
                }

                check.setVisibility(View.VISIBLE);
                transparentCircle.setVisibility(View.VISIBLE);
            }
        }
    }

    public static int getImageOrientation(String imagePath){
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /**
     * From here, override methods of
     * implements SubsamplingScaleImageView.OnImageEventListener
     */
    @Override
    public void onReady() {

    }

    @Override
    public void onImageLoaded() {
        postScale = imageView.getScale();

        //TODO :: fix image to fit in screen?
    }

    @Override
    public void onPreviewLoadError(Exception e) {

    }

    @Override
    public void onImageLoadError(Exception e) {

    }

    @Override
    public void onTileLoadError(Exception e) {

    }

    private static void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable)d).getBitmap();
            b.recycle();
        }

        //d.setCallback(null);
    }

    @Override
    public void onDestroy() {
        Log.d("ImageConfigureActity", "onDestroy");

        //recycleBitmap(transparentCircle);
        //recycleBitmap(imageView);
        recycleBitmap(mlRoundedImageView);

        super.onDestroy();
    }
}
