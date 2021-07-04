package renderer.occlusion.cbuffer;


/**
 * Span class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Span {
    
    private static int currentId = 0;
    public final int id = ++currentId;
    
    public static enum Flag { START, END }
    public Flag flag;
    
    public int x1;
    public int x2;
    
    public Span left;
    public Span right;

    public Span(Flag flag) {
        this.flag = flag;
        if (flag == Flag.START) {
            set(-Integer.MAX_VALUE, -Integer.MAX_VALUE);
        }
        else if (flag == Flag.END) {
            set(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    }

    public Span(int x1, int x2) {
        set(x1, x2);
    }

    private void set(int x1, int x2) {
        this.x1 = x1;
        this.x2 = x2;
    }

    public boolean isInside(int x) {
        return x >= x1 && x <= x2;
    }
    
    public void insertLeft(int x1, int x2) {
        Span span = getFromCache(x1, x2);
        if (left == null) {
            left = span;
            span.right = this;
        }
        else {
            Span previousLeft = left;
            left = span;
            span.right = this;
            span.left = previousLeft;
            previousLeft.right = span;
        }
    }
    
    public void insertCircular(Span span) {
        if (left == null) {
            left = this;
        }
        if (right == null) {
            right = this;
        }
        Span lastSpan = left;
        lastSpan.right = span;
        span.left = lastSpan;
        span.right = this;
        left = span;
    }
    
    @Override
    public String toString() {
        return "Span{" + "id=" + id + '}';
    }
    
    // --- spans cache ---
    
    public static Span cachedSpans = new Span(Flag.START);
    
    public static Span getFromCache(int x1, int x2) {
        if (cachedSpans.right == null) {
            return new Span(x1, x2);
        }
        else {
            Span cachedSpan = cachedSpans.right;
            cachedSpans.right = cachedSpan.right;
            cachedSpan.left = null;
            cachedSpan.right = null;
            cachedSpan.x1 = x1;
            cachedSpan.x2 = x2;
            return cachedSpan;
        }
    }
    
    // obs.: se spans contem mais de um span, 
    //       o primeiro span.left deve conter o ultimo span
    //       (deve estar no formato circular)
    public static void saveToCache(Span spans) {
        Span lastSpan = spans.left;
        if (lastSpan == null) {
            lastSpan = spans;
        }
        Span rightSpans = cachedSpans.right;
        cachedSpans.right = spans;
        spans.left = cachedSpans;
        lastSpan.right = rightSpans;
        if (rightSpans != null) {
            rightSpans.left = lastSpan;
        }
    }
    
}
