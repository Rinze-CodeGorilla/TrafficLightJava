package traffic;

public class CircularQueue {

    private int head = 0;
    private final int maxSize;
    private int size = 0;
    private int open = 0;

    private final String[] backing;

    public CircularQueue(int maxSize) {
        this.maxSize = maxSize;
        this.backing = new String[maxSize];
    }

    public void enqueue(String item) {
        if (isFull())
            throw new RuntimeException("Queue is full");
        backing[(head + size++) % maxSize] = item;
        if (isEmpty())
            open = head;
    }

    public String dequeue() {
        if (isEmpty())
            throw new RuntimeException("Queue is empty");
        size--;
        return backing[head++ % maxSize];
    }

    public int maxSize() {
        return maxSize;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == maxSize;
    }

    public RoadWaitState[] getState() {
        var result = new RoadWaitState[size];
        for (int i = 0; i < size; i++) {
            var name = backing[(head + i) % maxSize];
            var waitIntervals = head + i - open;
            if (waitIntervals < 0) waitIntervals += size;
            result[i] = new RoadWaitState(name, waitIntervals);
        }
        return result;
    }

    public void openNext() {
        if (++open >= head + size) {
            open -= size;
        }
    }
}
