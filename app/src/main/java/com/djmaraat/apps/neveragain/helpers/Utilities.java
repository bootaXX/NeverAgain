/*
 * Copyright 2016 Dandeljane Maraat
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.djmaraat.apps.neveragain.helpers;

import android.content.Context;
import android.util.Log;

import com.djmaraat.apps.neveragain.entities.DocumentItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by djanemaraat on 20/11/2016.
 */
public class Utilities {
    public static ArrayList<DocumentItem> loadJSONFromAsset(Context context) {
        ArrayList<DocumentItem> documentItemArrayList = new ArrayList<>();
        String json;
        try {
            InputStream is = context.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray docus = obj.getJSONArray("docus");

            for (int i = 0; i < docus.length(); i++) {
                JSONObject jdocuItem = docus.getJSONObject(i);
                DocumentItem documentItem = new DocumentItem(Integer.toString(jdocuItem.optInt("_id")), jdocuItem.optString("title"), jdocuItem.optString("content"), jdocuItem.optInt("_id"));

                //Add your values in your `ArrayList` as below:
                documentItemArrayList.add(documentItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("LOGTEST", "json error" + e);
        }

        return documentItemArrayList;
    }
}
