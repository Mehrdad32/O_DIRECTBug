package ir.matiran.o_directbugtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.logitud.ndkfileaccesstest.NDKFileAccessTest;

import java.io.File;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startWork();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
    }

    private void requestPermission() {
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
    }

    @SuppressLint("SetTextI18n")
    private void startWork() {
        TextView textView = (TextView) findViewById(R.id.result_text);
        textView.setText("Starting of test at " + LocalDateTime.now() +"\n");

        try {
            // Path
            String absolutePathOfExternalSD = getAbsolutePathOfExternalSD();
            if (absolutePathOfExternalSD.isEmpty()) {
                textView.append("\nExternal SD not found!");
                return;
            }

            // JNI
            NDKFileAccessTest ndkFileAccessTest = new NDKFileAccessTest();

            // First test, without O_DIRECT
            char res = ndkFileAccessTest.writeToFileWithoutODirect(absolutePathOfExternalSD + "/testFile.txt");
            if (res == 0) {
                textView.append(("\n1.) Write to SDCard without O_DIRECT fails... NOT NORMAL AT ALL, this should never happen!\n"));
            } else {
                textView.append(("\n1.) Write to SDCard without O_DIRECT succeeds, that's normal.\n"));
            }

            // Second test with O_DIRECT.
            // Run multiple times to avoid false positives (when the buffers are aligned by chance)
            int RUN_X_TIMES = 1000;
            char res2 = 0;
            for (int i = 0; i < RUN_X_TIMES; i++) {
                res2 += ndkFileAccessTest.writeToFileWithODirect(absolutePathOfExternalSD + "/testFile.txt");
            }

            if (res2 < RUN_X_TIMES) {
                textView.append("\n2.) Write to SDCard with O_DIRECT fails! O_DIRECT bug is NOT fixed.");
            } else {
                textView.append("\n2.) Write to SDCard with O_DIRECT succeeds! No Bug!");
            }

            textView.append("\n\nRUN_X_TIMES: " + RUN_X_TIMES);
            textView.append("\nFile Address: " + absolutePathOfExternalSD + "/testFile.txt");
        } catch (Exception e) {
            textView.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getAbsolutePathOfExternalSD() {
        String cardPath = "";

        File[] file = null;
        file = getExternalFilesDirs("");
        if (file.length > 1) {
            if (file[1] != null)
                cardPath = file[1].getAbsolutePath();
            else
                cardPath = "";
        } else
            cardPath = file[0].getAbsolutePath();

        return cardPath;
    }
}