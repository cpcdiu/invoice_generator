/*
 * This file is part of the Invoice Generator project.
 * Copyright (c) 2025 Daffodil Intl. University Computer Programming Club
 * Author: Md. Mojahid <https://github.com/mojahid2021>
 * Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

package com.mojahid.invoicegenerator.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mojahid.invoicegenerator.R;

import java.io.IOException;

public class SettingsFragment extends Fragment {

    private static final String TEMP_DB_NAME = "InvoiceSettings";
    private static final String CURRENCY_SYMBOL = "currency_symbol";
    private static final String TAX_RATE = "tax_rate";
    private static final String BUSINESS_NAME = "business_name";
    private static final String BUSINESS_ADDRESS = "business_address";
    private static final String LOGO_URI = "logo_uri";
    private ImageView imagePreview;
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        imagePreview = view.findViewById(R.id.imagePreview);
        Button uploadImageBtn = view.findViewById(R.id.uploadImageBtn);
        Button saveButton = view.findViewById(R.id.saveButton);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;

                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        try {
                            requireContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // API 28+
                                Drawable drawable = ImageDecoder.decodeDrawable(
                                        ImageDecoder.createSource(requireContext().getContentResolver(), uri)
                                );
                                imagePreview.setImageDrawable(drawable);
                            } else {
                                Bitmap bitmap = BitmapFactory.decodeStream(
                                        requireContext().getContentResolver().openInputStream(uri)
                                );
                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                imagePreview.setImageDrawable(drawable);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImageBtn.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        loadSettings(view);
        saveButton.setOnClickListener(v -> saveSettings(view));
        return view;
    }

    private void loadSettings(View view) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(TEMP_DB_NAME, Context.MODE_PRIVATE);

        EditText currencySymbolEdit = view.findViewById(R.id.currency_symbol);
        EditText taxRateEdit = view.findViewById(R.id.tax_rate);
        EditText businessNameEdit = view.findViewById(R.id.business_name);
        EditText businessAddressEdit = view.findViewById(R.id.business_address);

        currencySymbolEdit.setText(sharedPreferences.getString(CURRENCY_SYMBOL, ""));
        taxRateEdit.setText(sharedPreferences.getString(TAX_RATE, ""));
        businessNameEdit.setText(sharedPreferences.getString(BUSINESS_NAME, ""));
        businessAddressEdit.setText(sharedPreferences.getString(BUSINESS_ADDRESS, ""));

        String logoUriStr = sharedPreferences.getString(LOGO_URI, null);
        if (logoUriStr != null) {
            Uri logoUri = Uri.parse(logoUriStr);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // API 28+
                    Drawable drawable = ImageDecoder.decodeDrawable(
                            ImageDecoder.createSource(requireContext().getContentResolver(), logoUri)
                    );
                    imagePreview.setImageDrawable(drawable);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeStream(
                            requireContext().getContentResolver().openInputStream(logoUri)
                    );
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    imagePreview.setImageDrawable(drawable);
                }
                selectedImageUri = logoUri;
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Unable to load saved image", Toast.LENGTH_SHORT).show();
                imagePreview.setImageResource(R.drawable.image_placeholder);
            }
        }
    }

    private void saveSettings(View view) {
        EditText currencySymbolEdit = view.findViewById(R.id.currency_symbol);
        EditText taxRateEdit = view.findViewById(R.id.tax_rate);
        EditText businessNameEdit = view.findViewById(R.id.business_name);
        EditText businessAddressEdit = view.findViewById(R.id.business_address);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(TEMP_DB_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(CURRENCY_SYMBOL, currencySymbolEdit.getText().toString());
        editor.putString(TAX_RATE, taxRateEdit.getText().toString());
        editor.putString(BUSINESS_NAME, businessNameEdit.getText().toString());
        editor.putString(BUSINESS_ADDRESS, businessAddressEdit.getText().toString());

        if (selectedImageUri != null) {
            editor.putString(LOGO_URI, selectedImageUri.toString());
        }

        editor.apply();

        Toast.makeText(getContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
    }
}