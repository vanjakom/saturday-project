package com.mungolab.djvm.common;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class LanguageUtils {
    static List<Supplier<Boolean>> tests = new LinkedList<>();


    // https://www.pgrs.net/2015/04/23/partial-function-application-in-java-8/
    public static <IN, ARG, OUT> Function<IN, OUT> partial(BiFunction<ARG, IN, OUT> f, ARG arg) {
        return (in) -> { return f.apply(arg, in); };
    }

    // https://stackoverflow.com/questions/33242577/how-do-i-turn-a-java-enumeration-into-a-stream
    public static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        new Iterator<T>() {
                            public T next() {
                                return e.nextElement();
                            }
                            public boolean hasNext() {
                                return e.hasMoreElements();
                            }
                        },
                        Spliterator.ORDERED), false);
    }

    public static <T> List<T> dropLast(List<T> list) {
        List<T> newList = new LinkedList<>();

        T buffer = null;
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (buffer != null) {
                newList.add(buffer);
            }

            buffer = iterator.next();
        }

        return newList;
    }

    public static <T> T first(Collection<T> list) {
        if (list != null && list.size() > 0) {
            return list.iterator().next();
        } else {
            return null;
        }
    }

    public static <T> Collection<T> rest(Collection<T> list) {
        if (list != null && list.size() > 1) {
            List<T> newList = new LinkedList<>();
            
            Iterator<T> iterator = list.iterator();
            iterator.next();

            while (iterator.hasNext()) {
                newList.add(iterator.next());
            }

            return newList;
        } else {
            return new LinkedList<>();
        }
    }

    static {
        tests.add(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                List<String> list = listOf("a", "b", "c");
                if ("a".equals(LanguageUtils.first(list))) {
                    Collection<String> rest1 = LanguageUtils.rest(list);
                    if ("b".equals(LanguageUtils.first(rest1))) {
                        Collection<String> rest2 = LanguageUtils.rest(rest1);
                        if ("c".equals(LanguageUtils.first(rest2))) {
                            Collection<String> rest3 = LanguageUtils.rest(rest2);
                            return
                                    LanguageUtils.first(rest3) == null &&
                                    LanguageUtils.rest(rest3).size() == 0;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        });
    }

    public static <KEY, VAL1, VAL2> Map<KEY, Pair<VAL1, VAL2>> merge(
            Map<KEY, VAL1> original,
            Map<KEY, VAL2> lookup) {

        Map<KEY, Pair<VAL1, VAL2>> result = new HashMap<>();

        // iterate first map
        for (Map.Entry<KEY, VAL1> entry: original.entrySet()) {
            result.put(entry.getKey(), pairOf(entry.getValue(), lookup.get(entry.getKey())));
        }

        // iterate second map to make sure there are no unique keys to second map
        for (Map.Entry<KEY, VAL2> entry: lookup.entrySet()) {
            if (!result.containsKey(entry.getKey())) {
                result.put(entry.getKey(), pairOf(null, entry.getValue()));
            }
        }

        return result;
    }

    static {
        tests.add(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                Map<String, String> names = LanguageUtils.mapOf(
                        "Name1", "Surname1",
                        "Name2", "Surname2");
                Map<String, Long> ids = LanguageUtils.mapOf(
                        "Name2", 2L,
                        "Name3", 3L);

                Map<String, Pair<String, Long>> namesWithSurnamesAndIds = merge(names, ids);

                return
                        namesWithSurnamesAndIds.size() == 3 &&

                                namesWithSurnamesAndIds.get("Name1").getRight() == null &&
                                "Surname1".equals(namesWithSurnamesAndIds.get("Name1").getLeft()) &&

                                namesWithSurnamesAndIds.get("Name2").getRight() == 2L &&
                                "Surname2".equals(namesWithSurnamesAndIds.get("Name2").getLeft()) &&

                                namesWithSurnamesAndIds.get("Name3").getRight() == 3L &&
                                namesWithSurnamesAndIds.get("Name3").getLeft() == null;
            }
        });
    }



    public static <IN, OUT> OUT reduce(Stream<IN> stream, BiFunction<OUT, IN, OUT> fn, OUT initial) {
        final Reference<OUT> result = new Reference<>(initial);
        stream.forEach(entry -> {
            result.set(fn.apply(result.get(), entry)); });
        return result.get();
    }

    public static class Reference<T> {
        private T value;

        public Reference(T value) {
            this.value = value;
        }

        T get() {
            return this.value;
        }

        void set(T value) {
            this.value = value;
        }
    }

    static {
        tests.add(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                List<String> list = LanguageUtils.listOf("a", "b", "c");
                List<String> result = LanguageUtils.dropLast(list);

                // todo
                return result.remove(0).equals("a") && result.remove(0).equals("b") && result.size() == 0;
            }
        });
    }

    public static <VALUE> List<VALUE> listOf(VALUE v1) {
        List<VALUE> list = new LinkedList<>();
        list.add(v1);
        return list;
    }


    public static <VALUE> List<VALUE> listOf(VALUE v1, VALUE v2) {
        List<VALUE> list = new LinkedList<>();
        list.add(v1);
        list.add(v2);
        return list;
    }

    public static <VALUE> List<VALUE> listOf(VALUE v1, VALUE v2, VALUE v3) {
        List<VALUE> list = new LinkedList<>();
        list.add(v1);
        list.add(v2);
        list.add(v3);
        return list;
    }

    public static <KEY, VALUE> Map<KEY, VALUE> mapOf(KEY k1, VALUE v1) {
        Map<KEY, VALUE> data = new HashMap<>();
        data.put(k1, v1);
        return data;
    }

    public static <KEY, VALUE> Map<KEY, VALUE> mapOf(
            KEY k1, VALUE v1,
            KEY k2, VALUE v2) {
        Map<KEY, VALUE> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        return data;
    }

    public static <KEY, VALUE> Map<KEY, VALUE> mapOf(
            KEY k1, VALUE v1,
            KEY k2, VALUE v2,
            KEY k3, VALUE v3) {
        Map<KEY, VALUE> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        data.put(k3, v3);
        return data;
    }

    public static <KEY, VALUE> Map<KEY, VALUE> mapOf(
            KEY k1, VALUE v1,
            KEY k2, VALUE v2,
            KEY k3, VALUE v3,
            KEY k4, VALUE v4) {
        Map<KEY, VALUE> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        data.put(k3, v3);
        data.put(k4, v4);
        return data;
    }

    public static <KEY, VALUE> Map<KEY, VALUE> mapOf(
            KEY k1, VALUE v1,
            KEY k2, VALUE v2,
            KEY k3, VALUE v3,
            KEY k4, VALUE v4,
            KEY k5, VALUE v5) {
        Map<KEY, VALUE> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        data.put(k3, v3);
        data.put(k4, v4);
        data.put(k5, v5);
        return data;
    }

    public static <T> T[] arrayOf(T... values) {
        return values;
    }

    public static class Pair<LEFT, RIGHT> extends ListProxy<Object> {
        public Pair(LEFT left, RIGHT right) {
            super(LanguageUtils.<Object>listOf(left, right));
        }

        public LEFT getLeft() {
            return (LEFT)this.get(0);
        }

        public RIGHT getRight() {
            return (RIGHT)this.get(1);
        }
    }

    public static <LEFT, RIGHT> Pair<LEFT, RIGHT> pairOf(LEFT left, RIGHT right) {
        return new Pair<>(left, right);
    }

    public static String join(Collection<String> parts, String separator) {
        return String.join(separator, parts);

        /*
        StringBuilder sb = new StringBuilder();

        boolean notFirst = false;
        for (String part: parts) {
            if (notFirst) {
                sb.append(separator);
            }
            sb.append(part);
            notFirst = true;
        }

        return sb.toString();
        */
    }

    

    public static class ListProxy<VALUE> implements List<VALUE> {
        private List<VALUE> instance;

        public ListProxy(List<VALUE> instance) {
            this.instance = instance;
        }

        @Override
        public int size() {
            return this.instance.size();
        }

        @Override
        public boolean isEmpty() {
            return this.instance.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.instance.contains(o);
        }

        @Override
        public Iterator<VALUE> iterator() {
            return this.instance.iterator();
        }

        @Override
        public Object[] toArray() {
            return this.instance.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.instance.toArray(a);
        }

        @Override
        public boolean add(VALUE value) {
            return this.instance.add(value);
        }

        @Override
        public boolean remove(Object o) {
            return this.instance.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.instance.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends VALUE> c) {
            return this.instance.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends VALUE> c) {
            return this.instance.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return this.instance.retainAll(c);
        }

        @Override
        public void clear() {
            this.instance.clear();
        }

        @Override
        public VALUE get(int index) {
            return this.instance.get(index);
        }

        @Override
        public VALUE set(int index, VALUE element) {
            return this.instance.set(index, element);
        }

        @Override
        public void add(int index, VALUE element) {
            this.instance.add(index, element);
        }

        @Override
        public VALUE remove(int index) {
            return this.instance.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return this.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return this.instance.lastIndexOf(o);
        }

        @Override
        public ListIterator<VALUE> listIterator() {
            return this.instance.listIterator();
        }

        @Override
        public ListIterator<VALUE> listIterator(int index) {
            return this.instance.listIterator(index);
        }

        @Override
        public List<VALUE> subList(int fromIndex, int toIndex) {
            return this.instance.subList(fromIndex, toIndex);
        }
    }

    public static class MapProxy<KEY, VALUE> implements Map<KEY, VALUE> {
        private Map<KEY, VALUE> instance;

        public MapProxy(Map<KEY, VALUE> instance) {
            this.instance = instance;
        }

        @Override
        public int size() {
            return this.instance.size();
        }

        @Override
        public boolean isEmpty() {
            return this.instance.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.instance.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.instance.containsValue(value);
        }

        @Override
        public VALUE get(Object key) {
            return this.instance.get(key);
        }

        @Override
        public VALUE put(KEY key, VALUE value) {
            return this.instance.put(key, value);
        }

        @Override
        public VALUE remove(Object key) {
            return this.instance.remove(key);
        }

        @Override
        public void putAll(Map<? extends KEY, ? extends VALUE> map) {
            this.instance.putAll(map);
        }

        @Override
        public void clear() {
            this.instance.clear();
        }

        @Override
        public Set<KEY> keySet() {
            return this.instance.keySet();
        }

        @Override
        public Collection<VALUE> values() {
            return this.instance.values();
        }

        @Override
        public Set<Entry<KEY, VALUE>> entrySet() {
            return this.entrySet();
        }
    }

    public static URL fileToUrl(File file) {
        try {
            return new URL("file://" + file.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Unable to create URL for file: " + file.getAbsolutePath(), e);
        }
    }
    
    // execute all tests
    static {
        tests.forEach(test -> {
            if (!test.get()) {
                throw new RuntimeException("LanguageUtils: Test failed");
            }
        });
    }

    public static void main(String[] args) {
        System.out.println("Just to invoke tests");
    }
}
