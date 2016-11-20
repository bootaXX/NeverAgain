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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by djanemaraat on 20/11/2016.
 */

public  class DocumentItem implements Parcelable {

    private int mData;

    public String id;
    public String title;
    public String details;
    public int img;

    public DocumentItem(String id, String title, String details, int img) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.img = img;
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(title);
        out.writeString(details);
        out.writeInt(img);

        out.writeInt(mData);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<DocumentItem> CREATOR = new Parcelable.Creator<DocumentItem>() {
        public DocumentItem createFromParcel(Parcel in) {
            return new DocumentItem(in);
        }

        public DocumentItem[] newArray(int size) {
            return new DocumentItem[size];
        }
    };

    private DocumentItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        details = in.readString();
        img = in.readInt();

        mData = in.readInt();
    }
}