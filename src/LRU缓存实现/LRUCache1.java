package LRU缓存实现;


import java.util.HashMap;

/**
 * Created by liuzhao on 14-5-12.
 */
public class LRUCache1<K, V> {

    private final int MAX_CACHE_SIZE;
    private Entry first;
    private Entry last;

    private HashMap<K, Entry<K, V>> hashMap;

    public LRUCache1(int cacheSize) {
        MAX_CACHE_SIZE = cacheSize;
        hashMap = new HashMap<K, Entry<K, V>>();
    }
    /**
     * 放入-之前有，则从链路中拿出来放在第一位
     * @param key
     * @param value
     */
    public void put(K key, V value) {
        Entry entry = getEntry(key);
        if (entry == null) {
            if (hashMap.size() >= MAX_CACHE_SIZE) {
                hashMap.remove(last.key);
                removeLast();
            }
            entry = new Entry();
            entry.key = key;
        }
        entry.value = value;
        moveToFirst(entry);
        hashMap.put(key, entry);
    }

    public V get(K key) {
        Entry<K, V> entry = getEntry(key);
        if (entry == null) return null;
        moveToFirst(entry);
        return entry.value;
    }

    public void remove(K key) {
        Entry entry = getEntry(key);
        if (entry != null) {
            if (entry.pre != null) entry.pre.next = entry.next;
            if (entry.next != null) entry.next.pre = entry.pre;
            if (entry == first) first = entry.next;
            if (entry == last) last = entry.pre;
        }
        hashMap.remove(key);
    }
    /**
     * 吧该节点从链路中拿出来放入第一个节点
     * @param entry
     */
    private void moveToFirst(Entry entry) {
        if (entry == first) return;
        //吧该节点从联络中拿出来
        //如果有上个节点  则吧上个节点的下个节点指向该节点的下个节点
        if (entry.pre != null) entry.pre.next = entry.next;
        //如果有下个节点则吧下个节点的上个节点指向该节点的上个节点
        if (entry.next != null) entry.next.pre = entry.pre;
        if (entry == last) last = last.pre;

        
        if (first == null || last == null) {
            first = last = entry;
            return;
        }
        //吧该节点放入第一个节点
        entry.next = first;
        first.pre = entry;
        first = entry;
        entry.pre = null;
    }

    private void removeLast() {
        if (last != null) {
            last = last.pre;
            if (last == null) first = null;
            else last.next = null;
        }
    }


    private Entry<K, V> getEntry(K key) {
        return hashMap.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Entry entry = first;
        while (entry != null) {
            sb.append(String.format("%s:%s ", entry.key, entry.value));
            entry = entry.next;
        }
        return sb.toString();
    }

    class Entry<K, V> {
        public Entry pre;
        public Entry next;
        public K key;
        public V value;
    }
}