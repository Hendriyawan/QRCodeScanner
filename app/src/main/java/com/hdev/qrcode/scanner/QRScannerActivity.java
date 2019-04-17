package com.hdev.qrcode.scanner;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    @BindView(R.id.content_frame)
    FrameLayout contentFrame;
    private ZXingScannerView scannerView;
    private boolean cammeraGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initScannerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.startCamera();
        scannerView.setResultHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }


    @Override
    public void handleResult(Result result) {
        if (result != null) {
            showDialogResult(result.getText());
        }

    }

    //init scanner view
    private void initScannerView() {
        scannerView = new ZXingScannerView(this);
        //check sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (accessCameraGranted()) {
                contentFrame.addView(scannerView);
            } else {
                showToast("Check cammera permission first !");
                finish();
            }
        } else {
            contentFrame.addView(scannerView);
        }
    }

    //check cammera permission
    private boolean accessCameraGranted() {
        String[] permissions = {Manifest.permission.CAMERA};
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                cammeraGranted = true;
            }
        });
        return cammeraGranted;
    }

    //show Toast
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //show dialog result
    private void showDialogResult(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RESULT");
        builder.setMessage(content);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
        scannerView.resumeCameraPreview(this);
    }
}
