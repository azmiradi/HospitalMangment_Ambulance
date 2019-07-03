package bee.read.abanob.prog.com.hospitalmangment;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGSaver;
import androidx.appcompat.app.AppCompatActivity;
import bee.read.abanob.prog.com.hospitalmangment.Fragment.IncrementBlood;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ViewQR extends AppCompatActivity {
   ImageView imageView;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qr);
        imageView=findViewById(R.id.image);

        if (IncrementBlood.bitmap!=null)
        {
            imageView.setImageBitmap(IncrementBlood.bitmap);
        }
    }

    public void Save(View view) {
        boolean save;
        String result;
        try {
            save = QRGSaver.save(savePath,IncrementBlood.inputValue+"qr", IncrementBlood.bitmap, QRGContents.ImageType.IMAGE_JPEG);
            result = save ? "Image Saved" : "Image Not Saved";
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
