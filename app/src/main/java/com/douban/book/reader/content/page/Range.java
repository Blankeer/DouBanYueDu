package com.douban.book.reader.content.page;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.alipay.sdk.protocol.h;
import com.douban.book.reader.content.Book;
import java.util.Arrays;
import java.util.List;
import se.emilsjolander.stickylistheaders.R;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class Range implements Parcelable {
    public static final Creator<Position> CREATOR;
    public static final Range EMPTY;
    public Position endPosition;
    public Position startPosition;

    /* renamed from: com.douban.book.reader.content.page.Range.2 */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$douban$book$reader$content$page$Range$Topology;

        static {
            $SwitchMap$com$douban$book$reader$content$page$Range$Topology = new int[Topology.values().length];
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.FULL_COVER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.FRONT_COVER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.FRONT_EXTENT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.FRONT_SHRINK.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.BACK_COVER.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.BACK_EXTENT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.BACK_SHRINK.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.AHEAD.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.BEHIND.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.LEFT_JOIN.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.RIGHT_JOIN.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.INSIDE.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$page$Range$Topology[Topology.EQUAL.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
        }
    }

    public enum Topology {
        AHEAD,
        LEFT_JOIN,
        FRONT_COVER,
        BACK_COVER,
        RIGHT_JOIN,
        BEHIND,
        INSIDE,
        BACK_EXTENT,
        BACK_SHRINK,
        FRONT_EXTENT,
        FRONT_SHRINK,
        FULL_COVER,
        EQUAL
    }

    static {
        EMPTY = new Range(Position.NOT_FOUND, Position.NOT_FOUND);
        CREATOR = new Creator<Position>() {
            public Position createFromParcel(Parcel in) {
                return new Position(in);
            }

            public Position[] newArray(int size) {
                return new Position[size];
            }
        };
    }

    public Range(Position startPos, Position endPos) {
        if (startPos == null) {
            startPos = Position.NOT_FOUND;
        }
        this.startPosition = startPos;
        if (endPos == null) {
            endPos = Position.NOT_FOUND;
        }
        this.endPosition = endPos;
    }

    public Range(Range another) {
        this(another.startPosition, another.endPosition);
    }

    public boolean isValid() {
        return Position.isValid(this.startPosition) && Position.isValid(this.endPosition) && this.endPosition.compareTo(this.startPosition) >= 0;
    }

    public void updateStartPosition(Position start) {
        if (isPositionAfterRange(this, start)) {
            this.endPosition = this.startPosition;
        }
        this.startPosition = start;
    }

    public void updateEndPosition(Position end) {
        if (isPositionBeforeRange(this, end)) {
            this.startPosition = this.endPosition;
        }
        this.endPosition = end;
    }

    public Topology compareTopology(Range opponent) {
        if (!isValid(opponent)) {
            return Topology.AHEAD;
        }
        int diffStart = this.startPosition.compareTo(opponent.startPosition);
        int diffStartEnd = this.startPosition.compareTo(opponent.endPosition);
        int diffEnd = this.endPosition.compareTo(opponent.endPosition);
        int diffEndStart = this.endPosition.compareTo(opponent.startPosition);
        Topology topology = Topology.EQUAL;
        if (diffStart >= 0 || diffEnd >= 0) {
            if (diffStart <= 0 || diffEnd <= 0) {
                if (diffStart < 0 && diffEnd > 0) {
                    return Topology.FULL_COVER;
                }
                if (diffStart > 0 && diffEnd < 0) {
                    return Topology.INSIDE;
                }
                if (diffStart == 0 && diffEnd > 0) {
                    return Topology.BACK_EXTENT;
                }
                if (diffStart == 0 && diffEnd < 0) {
                    return Topology.BACK_SHRINK;
                }
                if (diffStart < 0 && diffEnd == 0) {
                    return Topology.FRONT_EXTENT;
                }
                if (diffStart <= 0 || diffEnd != 0) {
                    return topology;
                }
                return Topology.FRONT_SHRINK;
            } else if (diffStartEnd > 1) {
                return Topology.BEHIND;
            } else {
                if (diffStartEnd == 1) {
                    return Topology.RIGHT_JOIN;
                }
                return Topology.BACK_COVER;
            }
        } else if (diffEndStart < -1) {
            return Topology.AHEAD;
        } else {
            if (diffEndStart == -1) {
                return Topology.LEFT_JOIN;
            }
            return Topology.FRONT_COVER;
        }
    }

    public static boolean isValid(Range range) {
        return range != null && range.isValid();
    }

    public static Range normalize(Range range) {
        if (isValid(range)) {
            return range;
        }
        if (range == null) {
            return EMPTY;
        }
        return new Range(range.endPosition, range.startPosition);
    }

    public static Range merge(Range range1, Range range2) {
        if (isValid(range1) && isValid(range2)) {
            Topology topology = range1.compareTopology(range2);
            if (topology == Topology.AHEAD || topology == Topology.BEHIND) {
                return EMPTY;
            }
            return new Range(Position.min(range1.startPosition, range2.startPosition), Position.max(range1.endPosition, range2.endPosition));
        } else if (isValid(range1)) {
            return range1;
        } else {
            return isValid(range2) ? range2 : EMPTY;
        }
    }

    public static List<Range> cut(int worksId, Range original, Range cut) {
        if (isValid(original) && isValid(cut)) {
            Book book = Book.get(worksId);
            switch (AnonymousClass2.$SwitchMap$com$douban$book$reader$content$page$Range$Topology[original.compareTopology(cut).ordinal()]) {
                case dx.b /*1*/:
                    return Arrays.asList(new Range[]{new Range(original.startPosition, book.getPrevPosition(cut.startPosition)), new Range(book.getNextPosition(cut.endPosition), original.endPosition)});
                case dx.c /*2*/:
                case dx.d /*3*/:
                case dx.e /*4*/:
                    return Arrays.asList(new Range[]{new Range(original.startPosition, book.getPrevPosition(cut.startPosition))});
                case dj.f /*5*/:
                case ci.g /*6*/:
                case ci.h /*7*/:
                    return Arrays.asList(new Range[]{new Range(book.getNextPosition(cut.endPosition), original.endPosition)});
                case h.g /*8*/:
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_stackFromBottom /*11*/:
                    return Arrays.asList(new Range[]{original});
                default:
                    return Arrays.asList(new Range[0]);
            }
        }
        return Arrays.asList(new Range[]{original});
    }

    public static Range enlarge(Range original, Position position) {
        if (isPositionBeforeRange(original, position)) {
            return new Range(position, original.endPosition);
        }
        if (isPositionAfterRange(original, position)) {
            return new Range(original.startPosition, position);
        }
        return original;
    }

    public static boolean isPositionBeforeRange(Range range, Position position) {
        return isValid(range) && Position.isValid(position) && position.compareTo(range.startPosition) < 0;
    }

    public static boolean isPositionAfterRange(Range range, Position position) {
        return isValid(range) && Position.isValid(position) && position.compareTo(range.endPosition) > 0;
    }

    public static boolean contains(Range range, Position position) {
        return isValid(range) && Position.isValid(position) && position.compareTo(range.startPosition) >= 0 && position.compareTo(range.endPosition) <= 0;
    }

    public static boolean intersects(Range range1, Range range2) {
        if (!isValid(range1) || !isValid(range2)) {
            return false;
        }
        switch (AnonymousClass2.$SwitchMap$com$douban$book$reader$content$page$Range$Topology[range1.compareTopology(range2).ordinal()]) {
            case dx.b /*1*/:
            case dx.c /*2*/:
            case dx.d /*3*/:
            case dx.e /*4*/:
            case dj.f /*5*/:
            case ci.g /*6*/:
            case ci.h /*7*/:
            case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
            case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                return true;
            default:
                return false;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.startPosition, flags);
        dest.writeParcelable(this.endPosition, flags);
    }

    public String toString() {
        return String.format("{%s - %s}", new Object[]{this.startPosition, this.endPosition});
    }
}
