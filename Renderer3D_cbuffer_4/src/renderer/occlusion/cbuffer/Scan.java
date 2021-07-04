package renderer.occlusion.cbuffer;

import renderer.occlusion.cbuffer.Span.Flag;

/**
 * Scan class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Scan {
    
    private boolean full = false;
    
    private Span rootSpan = new Span(Flag.START);
    public Span endSpan = new Span(Flag.END);
    
    private Span cbuffer = rootSpan;
    private Span visibleSpans = null;
    
    private int xMin;
    private int xMax;
    
    public Scan(int xMin, int xMax) {
        rootSpan.right = endSpan;
        endSpan.left = rootSpan;
        this.xMin = xMin;
        this.xMax = xMax;
    }

    public boolean isFull() {
        return full;
    }

    public Span getCbuffer() {
        return cbuffer;
    }
    
    // --- visible spans ---
    
    public void clearVisibleSpans() {
        if (visibleSpans != null) {
            Span.saveToCache(visibleSpans);
        }
        visibleSpans = null;
    }
    
    private void addVisibleSpan(int x1, int x2) {
        if (x1 > x2) {
            // ignora
        }
        else if (visibleSpans == null) {
            visibleSpans = Span.getFromCache(x1, x2);
        }
        else {
            Span visibleSpan = Span.getFromCache(x1, x2);
            visibleSpans.insertCircular(visibleSpan);
        }
    }
    
    // --- c buffer ---
    
    public void clear() {
        if (rootSpan.right != endSpan) {
            Span toCache1 = rootSpan.right;
            Span toCache2 = endSpan.left;
            toCache1.left = toCache2;
            toCache2.right = toCache1;
            Span.saveToCache(toCache1);
        }
        rootSpan.right = endSpan;
        endSpan.left = rootSpan;
        full = false;
    }
    
    public Span insert(int x1, int x2) {
        clearVisibleSpans();
        Span span1 = null;
        Span span2 = null;
        Span currentSpan = rootSpan.right;
        while (currentSpan != null) {
            if (x2 < currentSpan.x1){
                addVisibleSpan(x1, x2);
                currentSpan.insertLeft(x1, x2);
                return visibleSpans;
            }
            else if (currentSpan.isInside(x1) && currentSpan.isInside(x2)){
                return null; // total occlusion
            }
            else if (x1 <= (currentSpan.x2 + 1)){
                span1 = currentSpan;
                if (x1 < currentSpan.x1) {
                    addVisibleSpan(x1, currentSpan.x1 - 1);
                    currentSpan.x1 = x1;
                }
                break;
            }
            currentSpan = currentSpan.right;
        }
        
        span2 = currentSpan;
        currentSpan = currentSpan.right;
        while (x2 >= (currentSpan.x1 - 1)) {
            addVisibleSpan(span2.x2 + 1, currentSpan.x1 - 1);
            span2 = currentSpan;
            currentSpan = currentSpan.right;
        }
        
        if (x2 > span2.x2) {
            addVisibleSpan(span2.x2 + 1, x2);
            span2.x2 = x2;
        }
        
        if (span1 != span2) {
            Span toCache = span1.right;
            span1.right = span2.right;
            span2.right.left = span1;
            span1.x2 = span2.x2;
            toCache.left = span2;
            span2.right = toCache;
            Span.saveToCache(toCache);
        }
        
        return visibleSpans;
    }
    
    public void checkFull() {
        if (rootSpan.right != endSpan) {
            full = (rootSpan.right.x1 <= xMin + 1) && (rootSpan.right.x2 >= xMax - 1);
        }
    }
    
}
