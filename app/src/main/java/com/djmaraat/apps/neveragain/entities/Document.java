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

package com.djmaraat.apps.neveragain.entities;

import android.content.Context;

import com.djmaraat.apps.neveragain.helpers.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by djanemaraat on 20/11/2016.
 */
public class Document {
    /**
     * An array of sample (dummy) items.
     */
    public static List<DocumentItem> ITEMS = new ArrayList<DocumentItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DocumentItem> ITEM_MAP = new HashMap<String, DocumentItem>();

//    private static final int COUNT = 25;

//    static {
//        for (int i = 1; i <= COUNT; i++) {
////            addItem(createDummyItem(i));
//        }
//        Utilities.loadJSONFromAsset()
//
//    }

    public Document(Context context) {
        ITEMS = Utilities.loadJSONFromAsset(context);
        for (int i = 1; i <= ITEMS.size(); i++) {
            ITEM_MAP.put(ITEMS.get(i).id, ITEMS.get(i));
        }
    }

//    private static void addItemToMap(DocumentItem item) {
//        ITEM_MAP.put(item.id, item);
//    }

//    private static DocumentItem createDummyItem(int position) {
//        return new DocumentItem(String.valueOf(position), "Item " + position, makeDetails(position));
//    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

//    /**
//     * A dummy item representing a piece of content.
//     */
//    public static class DocumentItem {
//        public final String id;
//        public final String content;
//        public final String details;
//        public final int img;
//
//        public DocumentItem(String id, String content, String details, int img) {
//            this.id = id;
//            this.content = content;
//            this.details = details;
//            this.img = img;
//        }
//
//        @Override
//        public String toString() {
//            return content;
//        }
//    }
}
