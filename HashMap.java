package ru.geekbrains.lesson4;

/**
 * Структура хэш-таблицы
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class HashMap <K, V> {

    //region Публичные методы

    public V put(K key, V value){
        if (buckets.length * LOAD_FACTOR <= size)
            recalculate();

        int index = calculateBucketIndex(key);
        Bucket bucket = buckets[index];
        if (bucket == null){
            bucket = new Bucket();
            buckets[index] = bucket;
        }

        Entity entity = new Entity();
        entity.key = key;
        entity.value = value;

        V buf = (V)bucket.add(entity);
        if (buf == null){
            size++;
        }
        return buf;
    }

    //endregion

    //region Методы

    private void recalculate(){
        size = 0;
        Bucket<K, V>[] old = buckets;
        buckets = new Bucket[old.length * 2];
        for (int i = 0; i < old.length; i++){
            Bucket<K, V> bucket = old[i];
            if (bucket != null){
                Bucket.Node node = bucket.head;
                while (node != null){
                    put((K)node.value.key, (V)node.value.value);
                    node = node.next;
                }
            }
        }
    }

    private int calculateBucketIndex(K key){
       return Math.abs(key.hashCode()) % buckets.length;
    }

    //endregion

    //region Конструкторы

    public HashMap(){
        buckets = new Bucket[INIT_BUCKET_COUNT];
    }

    public HashMap(int initCount){
        buckets = new Bucket[initCount];
    }

    //endregion

    //region Вспомогательные структуры

    /**
     * Элемент хэш-таблицы
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Bucket<K, V> bucket : buckets) {
            if (bucket != null) {
                Node currentNode = bucket.head;
                while (currentNode != null) {
                    sb.append(currentNode.value.key).append(": ").append(currentNode.value.value).append("\n");
                    currentNode = currentNode.next;
                }
            }
        }
        return sb.toString();
    }
    class Entity{

        /**
         * Ключ
         */
        K key;

        /**
         * Значение
         */
        V value;

    }

    /**
     * Элемент массива (связный список) из которого состоит хэш-таблица
     */
    class Bucket<KK,VV>{


        /**
         * Указатель на первый элемент связного списка
         */
        private Node head;

        /**
         * Узел связного списка
         */
        class Node{

            /**
             * Ссылка на следующий узел (если имеется)
             */
            Node next;

            /**
             * Значение узла
             */
            Entity value;

        }

        public V add(Entity entity){
            Node node = new Node();
            node.value = entity;

            if (head == null){
                head = node;
                return null;
            }

            Node currentNode = head;
            while (true){
                if (currentNode.value.key.equals(entity.key)){
                    V buf = (V)currentNode.value.value;
                    currentNode.value.value = entity.value;
                    return buf;
                }
                if (currentNode.next != null){
                    currentNode = currentNode.next;
                }
                else {
                    currentNode.next = node;
                    return null;
                }
            }
        }


    }

    //endregion

    //region Поля

    /**
     * Массив бакетов (связных списков)
     */
    private Bucket[] buckets;
    private int size;

    //endregion

    //region Константы

    private static final int INIT_BUCKET_COUNT = 16;
    private static final double LOAD_FACTOR = 0.5;

    //endregion
}
//2****. (Дополнительная, необязательная задача, для тех, кому очень скучно) Добавить возможность перебора всех
// элементов нашей структуры данных, необходимо добавить несколько элементов, а затем перебрать все элементы
// структуры HashTable используя цикл foreach. Подумайте, возможно вам стоит обратиться к интерфейсу Iterable и в
// рамках имплементации подобного интерфейса создать объект типа Iterator, далее, вы реализуете метод next и hasNext,
// наделите способностью нашу структуру HashMap быть перечисляемой.
// Накидал вариант, но что-то запутался!!!

public class Main {
    public class HashMap<K, V> implements Iterable<Map.Entry<K, V>> {

        // ...

        private class HashMapIterator implements Iterator<Map.Entry<K, V>> {

            private int bucketIndex;
            private Node currentNode;

            public HashMapIterator() {
                bucketIndex = 0;
                currentNode = null;
                findNextNode();
            }

            public boolean hasNext() {
                return currentNode != null;
            }

            public Map.Entry<K, V> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Map.Entry<K, V> entry = currentNode.value;
                currentNode = currentNode.next;
                findNextNode();
                return entry;
            }

            private void findNextNode() {
                while (currentNode == null && bucketIndex < buckets.length) {
                    Bucket<K, V> bucket = buckets[bucketIndex];
                    if (bucket != null) {
                        currentNode = bucket.head;
                    }
                    bucketIndex++;
                }
            }
        }

        // ...

    }

    class Bucket<K, V> {

        // ...

        class Node {
            Map.Entry<K, V> value;
            Node next;

            Node(Map.Entry<K, V> value, Node next) {
                this.value = value;
                this.next = next;
            }
        }

        // ...

    }

    public static void main(String[] args) {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        for (Map.Entry<Integer, String> entry : map) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}