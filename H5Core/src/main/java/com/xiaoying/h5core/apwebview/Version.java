package com.xiaoying.h5core.apwebview;

public class Version {
    private static final int STORE_OFFSET = 16;

    public static final int getMajor(final int v) {
        return (v >> STORE_OFFSET);
    }

    public static final int getMinor(final int v) {
        return (v << STORE_OFFSET) >> STORE_OFFSET;
    }

    public static final String toString(final int v) {
        return "Version(major: " + getMajor(v) + ", minor: " + getMinor(v)
                + ")";
    }

    public static final int build(final int major, final int minor) {
        return (major << STORE_OFFSET) | minor;
    }

    public static class Major {
        public int compare(final int v1, final int v2) {
            final int v1Major = getMajor(v1);
            final int v2Major = getMajor(v2);
            if (v1Major == v2Major) {
                return 0;
            } else if (v1Major > v2Major) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public static class Minor {
        public int compare(final int v1, final int v2) {
            final int v1Minor = getMinor(v1);
            final int v2Minor = getMinor(v2);
            if (v1Minor == v2Minor) {
                return 0;
            } else if (v1Minor > v2Minor) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
