/*
 * This file is part of the Invoice Generator project.
 * Copyright (c) 2025 Daffodil Intl. University Computer Programming Club
 * Author: Md. Mojahid <https://github.com/mojahid2021>
 * Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */
package com.mojahid.invoicegenerator.history;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mojahid.invoicegenerator.R;

public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }
}