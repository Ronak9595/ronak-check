package com.myapplication.ronakcheck;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private TextView deviceInfoTextView;
    private TextView batteryInfoTextView;
    private TextView networkInfoTextView;
    private TextView systemInfoTextView;
    private TextView cameraInfoTextView;
    private TextView soundInfoTextView;
    private TextView displayInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceInfoTextView = findViewById(R.id.device_info_text_view);
        batteryInfoTextView = findViewById(R.id.battery_info_text_view);
        networkInfoTextView = findViewById(R.id.network_info_text_view);
        systemInfoTextView = findViewById(R.id.system_info_text_view);
        cameraInfoTextView = findViewById(R.id.camera_info_text_view);
        soundInfoTextView = findViewById(R.id.sound_info_text_view);
        displayInfoTextView = findViewById(R.id.display_info_text_view);

        // Check and request runtime permissions for READ_PHONE_STATE and CAMERA
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            } else {
                // Permission already granted, proceed with accessing device information
                displayDeviceInformation();
                displaySystemInformation();
                checkCamera();
            }
        } else {
            // For devices below Android 10, no need for runtime permissions
            displayDeviceInformation();
            displaySystemInformation();
            checkCamera();
        }

        // Retrieve and display battery information
        displayBatteryInformation();

        // Retrieve and display network status
        displayNetworkStatus();

        // Check sound
        checkSound();

        // Display screen information
        displayScreenInformation();
    }

    private void displayDeviceInformation() {
        // Get device information
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceInfo = "Device Name: " + Build.MANUFACTURER + " " + Build.MODEL + "\n";
        deviceInfo += "Model Number: " + Build.MODEL + "\n";
        deviceInfo += "Device ID (ANDROID_ID): " + (deviceId != null ? deviceId : "N/A");

        deviceInfoTextView.setText(deviceInfo);
    }

    private void displayBatteryInformation() {
        // Get battery information
        String batteryInfo = "Battery Information: " + getBatteryStatus();
        batteryInfoTextView.setText(batteryInfo);
    }

    private String getBatteryStatus() {
        // Implement battery status retrieval
        // For simplicity, returning a static message
        return "Battery Status: 80%";
    }

    private void displayNetworkStatus() {
        // Get network status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String networkStatus = "Network Status: " + (networkInfo != null && networkInfo.isConnected() ? "Connected" : "Disconnected");
        networkInfoTextView.setText(networkStatus);
    }

    private void displaySystemInformation() {
        // Get system information
        String systemInfo = "Android Version: " + Build.VERSION.RELEASE + "\n";
        systemInfo += "CPU: " + Build.HARDWARE + "\n";
        systemInfo += "RAM: " + getTotalRAM() + " GB\n";
        systemInfo += "Internal Storage: " + getTotalInternalStorage() + " GB\n";
        systemInfo += "Kernel Version: " + getKernelVersion();

        systemInfoTextView.setText(systemInfo);
    }

    private String getTotalRAM() {
        // Get total RAM in GB
        long totalMemory = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("MemTotal:")) {
                    String[] tokens = line.split("\\s+");
                    totalMemory = Long.parseLong(tokens[1]) / (1024 * 1024); // Convert to GB
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(totalMemory);
    }

    private String getTotalInternalStorage() {
        // Get total internal storage in GB
        File path = getFilesDir();
        long totalInternalStorage = path.getTotalSpace() / (1024 * 1024 * 1024); // Convert to GB
        return String.valueOf(totalInternalStorage);
    }

    private String getKernelVersion() {
        // Get kernel version
        String kernelVersion = "N/A";
        try {
            Process process = Runtime.getRuntime().exec("uname -srm");
            InputStreamReader reader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            bufferedReader.close();
            kernelVersion = line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kernelVersion;
    }

    private void checkCamera() {
        // Check if the device has a camera
        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            cameraInfoTextView.setText("Camera: Available");
        } else {
            cameraInfoTextView.setText("Camera: Not Available");
        }
    }

    private void checkSound() {
        // Check sound
        // Implement as per your requirements
        soundInfoTextView.setText("Sound: Not implemented");
    }

    private void displayScreenInformation() {
        // Display screen information
        WindowManager windowManager = getWindowManager();
        String displayInfo = "Display Resolution: " + windowManager.getDefaultDisplay().getWidth() + "x" + windowManager.getDefaultDisplay().getHeight();
        displayInfoTextView.setText(displayInfo);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing device information
                displayDeviceInformation();
                displaySystemInformation();
                checkCamera();
            } else {
                // Permission denied, handle the case where permissions are denied by the user
                // You may display a message or take appropriate action here
            }
        }
    }
}
