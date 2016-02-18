package com.douban.book.reader.content.page;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;

public class Position implements Parcelable, Comparable<Position> {
    public static final Creator<Position> CREATOR;
    public static final Position NOT_FOUND;
    public static final String TAG;
    public int packageId;
    public int packageIndex;
    public int paragraphId;
    public int paragraphIndex;
    public int paragraphOffset;

    static {
        TAG = Position.class.getSimpleName();
        NOT_FOUND = new Position();
        CREATOR = new Creator<Position>() {
            public Position createFromParcel(Parcel in) {
                return new Position(in);
            }

            public Position[] newArray(int size) {
                return new Position[size];
            }
        };
    }

    public Position() {
        this.packageId = 0;
        this.paragraphId = 0;
        this.packageIndex = -1;
        this.paragraphIndex = -1;
        this.paragraphOffset = 0;
    }

    public Position(Position position) {
        set(position);
    }

    public Position(Parcel in) {
        this.packageId = in.readInt();
        this.paragraphId = in.readInt();
        this.packageIndex = in.readInt();
        this.paragraphIndex = in.readInt();
        this.paragraphOffset = in.readInt();
    }

    public void set(Position position) {
        this.packageId = position.packageId;
        this.paragraphId = position.paragraphId;
        this.packageIndex = position.packageIndex;
        this.paragraphIndex = position.paragraphIndex;
        this.paragraphOffset = position.paragraphOffset;
    }

    public long getActualPosition() {
        if (isValid()) {
            return ((((long) this.packageIndex) << 48) + (((long) this.paragraphIndex) << 32)) + ((long) this.paragraphOffset);
        }
        return -1;
    }

    public boolean isValid() {
        return (this.packageIndex == -1 || this.paragraphIndex == -1) ? false : true;
    }

    public boolean isInRange(Range range) {
        return Range.isValid(range) && compareTo(range.startPosition) >= 0 && compareTo(range.endPosition) <= 0;
    }

    public static boolean isValid(Position position) {
        return position != null && position.isValid();
    }

    public static Position max(Position position1, Position position2) {
        if (isValid(position1) && isValid(position2)) {
            return position1.compareTo(position2) < 0 ? position2 : position1;
        } else {
            if (isValid(position1)) {
                return position1;
            }
            return isValid(position2) ? position2 : NOT_FOUND;
        }
    }

    public static Position min(Position position1, Position position2) {
        if (isValid(position1) && isValid(position2)) {
            return position1.compareTo(position2) > 0 ? position2 : position1;
        } else {
            if (isValid(position1)) {
                return position1;
            }
            return isValid(position2) ? position2 : NOT_FOUND;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.packageId);
        parcel.writeInt(this.paragraphId);
        parcel.writeInt(this.packageIndex);
        parcel.writeInt(this.paragraphIndex);
        parcel.writeInt(this.paragraphOffset);
    }

    public int compareTo(@NonNull Position opponent) {
        return (int) Math.max(Math.min(getActualPosition() - opponent.getActualPosition(), 2147483647L), -2147483648L);
    }

    public boolean equals(Position opponent) {
        return compareTo(opponent) == 0;
    }

    public String toString() {
        return String.format("pk%d(%d)+pr%d(%d)+%d", new Object[]{Integer.valueOf(this.packageIndex), Integer.valueOf(this.packageId), Integer.valueOf(this.paragraphIndex), Integer.valueOf(this.paragraphId), Integer.valueOf(this.paragraphOffset)});
    }
}
