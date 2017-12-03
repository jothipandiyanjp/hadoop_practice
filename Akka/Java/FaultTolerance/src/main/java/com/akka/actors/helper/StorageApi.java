package com.akka.actors.helper;

public class StorageApi {


    public static class Store {
      public final Entry entry;
 
      public Store(Entry entry) {
        this.entry = entry;
      }
 
      public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(), entry);
      }
    }
 
    public static class Entry {
      public final String key;
      public final long value;
 
      public Entry(String key, long value) {
        this.key = key;
        this.value = value;
      }
 
      public String toString() {
        return String.format("%s(%s, %s)", getClass().getSimpleName(), key, value);
      }
    }
 
    public static class Get {
      public final String key;
 
      public Get(String key) {
        this.key = key;
      }
 
      public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(), key);
      }
    }
}
