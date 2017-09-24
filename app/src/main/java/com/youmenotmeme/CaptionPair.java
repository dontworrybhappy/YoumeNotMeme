package com.youmenotmeme;

import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by doug on 9/23/17.
 */

public class CaptionPair implements Parcelable {
    public String top;
    public String bottom;

    public CaptionPair(String f, String s) {
        this.top = f;
        this.bottom = s;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(top);
        out.writeString(bottom);
    }

    public static final Parcelable.Creator<CaptionPair> CREATOR
            = new Parcelable.Creator<CaptionPair>() {
        public CaptionPair createFromParcel(Parcel in) {
            return new CaptionPair(in);
        }

        public CaptionPair[] newArray(int size) {
            return new CaptionPair[size];
        }
    };

    private CaptionPair(Parcel in) {
        this.top = in.readString();
        this.bottom = in.readString();
    }
}
