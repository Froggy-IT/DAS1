import java.util.*;

public class Main{
    private static final int INSERTION_SORT_THRESHOLD = 16;
    private static long comparisons = 0;
    private static int maxDepth = 0;

    public static void resetMetrics() {
        comparisons = 0;
        maxDepth = 0;
    }

    public static long getComparisons() { return comparisons; }
    public static int getMaxDepth() { return maxDepth; }

    public static void mergeSort(int[] a) {
        int[] buffer = new int[a.length];
        mergeSort(a, buffer, 0, a.length - 1, 1);
    }

    private static void mergeSort(int[] a, int[] buffer, int l, int r, int depth) {
        maxDepth = Math.max(maxDepth, depth);
        if (r - l + 1 <= INSERTION_SORT_THRESHOLD) {
            insertionSort(a, l, r);
            return;
        }
        int m = (l + r) >>> 1;
        mergeSort(a, buffer, l, m, depth + 1);
        mergeSort(a, buffer, m + 1, r, depth + 1);
        merge(a, buffer, l, m, r);
    }

    private static void merge(int[] a, int[] buffer, int l, int m, int r) {
        int i = l, j = m + 1, k = l;
        while (i <= m && j <= r) {
            comparisons++;
            buffer[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
        }
        while (i <= m) buffer[k++] = a[i++];
        while (j <= r) buffer[k++] = a[j++];
        System.arraycopy(buffer, l, a, l, r - l + 1);
    }

    private static void insertionSort(int[] a, int l, int r) {
        for (int i = l + 1; i <= r; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= l && a[j] > key) {
                comparisons++;
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    public static void quickSort(int[] a) {
        quickSort(a, 0, a.length - 1);
    }

    private static void quickSort(int[] a, int l, int r) {
        Random rand = new Random();
        while (l < r) {
            maxDepth++;
            int pivotIndex = l + rand.nextInt(r - l + 1);
            int pivot = a[pivotIndex];
            int i = l, j = r;
            while (i <= j) {
                while (a[i] < pivot) { comparisons++; i++; }
                while (a[j] > pivot) { comparisons++; j--; }
                if (i <= j) {
                    int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
                    i++; j--;
                }
            }
            if (j - l < r - i) {
                if (l < j) quickSort(a, l, j);
                l = i;
            } else {
                if (i < r) quickSort(a, i, r);
                r = j;
            }
        }
    }

    public static int select(int[] a, int k) {
        return select(a, 0, a.length - 1, k);
    }

    private static int select(int[] a, int l, int r, int k) {
        while (true) {
            if (l == r) return a[l];
            int pivot = medianOfMedians(a, l, r);
            int pivotIndex = partition(a, l, r, pivot);
            if (k == pivotIndex) return a[k];
            if (k < pivotIndex) r = pivotIndex - 1;
            else l = pivotIndex + 1;
        }
    }

    private static int medianOfMedians(int[] a, int l, int r) {
        int n = r - l + 1;
        if (n < 5) {
            Arrays.sort(a, l, r + 1);
            return a[l + n / 2];
        }
        int medCount = 0;
        for (int i = l; i <= r; i += 5) {
            int subR = Math.min(i + 4, r);
            Arrays.sort(a, i, subR + 1);
            int median = a[i + (subR - i) / 2];
            a[l + medCount++] = median;
        }
        return medianOfMedians(a, l, l + medCount - 1);
    }

    private static int partition(int[] a, int l, int r, int pivot) {
        int i = l, j = r;
        while (i <= j) {
            while (a[i] < pivot) i++;
            while (a[j] > pivot) j--;
            if (i <= j) {
                int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
                i++; j--;
            }
        }
        return i - 1;
    }

    public static double closestPair(Point[] points) {
        Arrays.sort(points, Comparator.comparingDouble(p -> p.x));
        return closest(points, 0, points.length - 1, new Point[points.length]);
    }

    private static double closest(Point[] pts, int l, int r, Point[] buf) {
        if (r - l <= 3) {
            double min = Double.POSITIVE_INFINITY;
            for (int i = l; i <= r; i++)
                for (int j = i + 1; j <= r; j++)
                    min = Math.min(min, pts[i].dist(pts[j]));
            Arrays.sort(pts, l, r + 1, Comparator.comparingDouble(p -> p.y));
            return min;
        }
        int m = (l + r) >>> 1;
        double d1 = closest(pts, l, m, buf);
        double d2 = closest(pts, m + 1, r, buf);
        double d = Math.min(d1, d2);
        mergeByY(pts, l, m, r, buf);
        int cnt = 0;
        for (int i = l; i <= r; i++)
            if (Math.abs(pts[i].x - pts[m].x) < d) buf[cnt++] = pts[i];
        for (int i = 0; i < cnt; i++)
            for (int j = i + 1; j < cnt && (buf[j].y - buf[i].y) < d; j++)
                d = Math.min(d, buf[i].dist(buf[j]));
        return d;
    }

    private static void mergeByY(Point[] pts, int l, int m, int r, Point[] buf) {
        int i = l, j = m + 1, k = 0;
        while (i <= m && j <= r)
            buf[k++] = (pts[i].y <= pts[j].y) ? pts[i++] : pts[j++];
        while (i <= m) buf[k++] = pts[i++];
        while (j <= r) buf[k++] = pts[j++];
        System.arraycopy(buf, 0, pts, l, k);
    }

    public static class Point {
        public final double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
        public double dist(Point p) { return Math.hypot(x - p.x, y - p.y); }
    }

    public static void main(String[] args) {
        int[] arr = {5,3,8,4,2,7,1,6};
        resetMetrics();
        mergeSort(arr);
        System.out.println("MergeSort result: " + Arrays.toString(arr));
        System.out.println("Comparisons: " + comparisons + " Max depth: " + maxDepth);
    }
}