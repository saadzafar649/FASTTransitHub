package com.example.fasttransithub.User;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.fasttransithub.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class user_home_Fragment extends Fragment {

    private ImageView qrCodeIV;
    Bitmap bitmap;
    public user_home_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.fragment_user_home_, container, false);
       qrCodeIV=view.findViewById(R.id.user_Qr);
        initQRCode();
//        String myText = "Something";
//        //initializing MultiFormatWriter for QR code
//        MultiFormatWriter mWriter = new MultiFormatWriter();
//        try {
//            //BitMatrix class to encode entered text and set Width & Height
//            BitMatrix mMatrix = mWriter.encode(myText, BarcodeFormat.QR_CODE, 400,400);
//            BarcodeEncoder mEncoder = new BarcodeEncoder();
//            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
//            qrCodeIV.setImageBitmap(mBitmap);//Setting generated QR code to imageView
//            // to hide the keyboard
//            //InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            //manager.hideSoftInputFromWindow(etText.getApplicationWindowToken(), 0);
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
        return view;
    }
    private void initQRCode() {
        String name = "Name : ";

        StringBuilder textToSend = new StringBuilder();
        textToSend.append(name);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textToSend.toString(), BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeIV.setImageBitmap(bitmap);
            qrCodeIV.setVisibility(View.VISIBLE);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}