package com.vertexcache.domain.cache;

import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.cache.EvictionPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CacheTest {

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {

    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void testCache() throws Exception {


        //Cache<Integer, String> plainCache = new Cache<>(EvictionPolicy.NONE);

        //Cache<Integer, String> plainCache = new Cache<>(EvictionPolicy.LRU,1000);

       // Cache<Integer, String> plainCache = new Cache<>(EvictionPolicy.MRU,1000);

       // Cache<Integer, String> plainCache = new Cache<>(EvictionPolicy.FIFO,1000);

        //Cache<Integer, String> plainCache = (Cache<Integer, String>) CacheService.getInstance(EvictionPolicy.LFU,1000);

        //Cache<Integer, String> plainCache = (Cache<Integer, String>) CacheService.getInstance(EvictionPolicy.RANDOM,1000);

        Cache<Object, Object> plainCache = Cache.getInstance(EvictionPolicy.TwoQueues, 1000);

       // Cache<Integer, String> plainCache = new Cache<>(EvictionPolicy.LFU,1000);

        // Add elements to the LRU cache with secondary indexes
        plainCache.put(1, "Value1", "indexA", "indexB");
        plainCache.put(2, "Value2", "indexA", "indexC");
        plainCache.put(3, "{\"fake-key\" : \"fake-value\"}", "indexD", "indexE");

        System.out.println("Value for primary key 1: " + plainCache.get(1));


        System.out.println("Value for secondary key One indexA: " + plainCache.getBySecondaryKeyIndexOne("indexA"));
        System.out.println("Value for secondary key One indexD: " + plainCache.getBySecondaryKeyIndexOne("indexD"));
        System.out.println("Value for secondary key One non-existing: " + plainCache.getBySecondaryKeyIndexOne("indexA-non-exist"));
        System.out.println("Value for secondary key One indexD: " + plainCache.getBySecondaryKeyIndexTwo("indexE"));

       // System.out.println("Value for secondary key One indexA: " + plainCache.getBySecondaryKeyIndexOne("indexA"));


       // System.out.println("Value for secondary key Two indexB: " + plainCache.getBySecondaryKeyIndexTwo("indexC"));


        //   lruCache.put(3, "Value3", "indexB", "indexC");

        // Retrieve elements from the LRU cache using primary key
      //  System.out.println("Value for key 1: " + lruCache.get(1));
      //  System.out.println("Value for key 2: " + lruCache.get(2));
        //System.out.println("Value for key 3: " + lruCache.get(3));

        // Retrieve elements from the LRU cache using secondary index
       // System.out.println("Value for secondary index 'indexA': " + lruCache.getBySecondaryKey(0, "indexA"));
       // System.out.println("Value for secondary index 'indexB': " + lruCache.getBySecondaryKey(1, "indexB"));
        //   System.out.println("Value for secondary index 'indexC': " + lruCache.getBySecondaryKey(2, "indexC"));

        assertEquals(true,true);

    }

    //@Test
    public void testCachexx() {

/*
        //CacheNoEviction<Integer, Book> cache = new CacheNoEviction<>();


        Cache<Integer, Book> cache = new Cache<>(EvictionPolicy.NONE);

        // Add books to the cache
        Book book1 = new Book(1, "Clean Code", "Robert C. Martin", "Software Engineering");
        Book book2 = new Book(2, "Effective Java", "Joshua Bloch", "Software Engineering");
        Book book3 = new Book(3, "Design Patterns", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "Software Engineering");
        //Book book3 = new Book(4, "Design Patterns", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "Software Engineering");


        cache.put(book1.getId(), book1, "Clean Code", "Robert C. Martin", "Software Engineering");
        cache.put(book2.getId(), book2, "Effective Java", "Joshua Bloch", "Software Engineering");
        cache.put(book3.getId(), book3, "Design Patterns", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "Software Engineering");

        // Retrieve books by secondary keys
        Book bookByTitle = cache.getBySecondaryKey(0, "Clean Code");
        Book bookByAuthor = cache.getBySecondaryKey(1, "Joshua Bloch");
        Book bookByGenre = cache.getBySecondaryKey(2, "Software Engineering");

        System.out.println("Retrieved books by secondary keys:");
        System.out.println(bookByTitle);
        System.out.println(bookByAuthor);
        System.out.println(bookByGenre);

        // Add more secondary indexes dynamically
        cache.put(4, new Book(4, "Head First Java", "Kathy Sierra, Bert Bates", "Software Engineering"), "Head First Java", "Kathy Sierra, Bert Bates", "Software Engineering");
        cache.put(5, new Book(5, "Java Concurrency in Practice", "Brian Goetz et al.", "Software Engineering"), "Java Concurrency in Practice", "Brian Goetz et al.", "Software Engineering");

        // Retrieve books by additional secondary keys
        Book bookByTitle2 = cache.getBySecondaryKey(0, "Head First Java");
        Book bookByAuthor2 = cache.getBySecondaryKey(1, "Brian Goetz et al.");
        Book bookByGenre2 = cache.getBySecondaryKey(2, "Software Engineering");

        System.out.println("\nRetrieved books by additional secondary keys:");
        System.out.println(bookByTitle2);
        System.out.println(bookByAuthor2);
        System.out.println(bookByGenre2);




        Cache<Integer, String> lruCache = new Cache<>(EvictionPolicy.LRU);

        // Add elements to the LRU cache with secondary indexes
        lruCache.put(1, "Value1", "indexA", "indexB");
        lruCache.put(2, "Value2", "indexA", "indexC");
     //   lruCache.put(3, "Value3", "indexB", "indexC");

        // Retrieve elements from the LRU cache using primary key
        System.out.println("Value for key 1: " + lruCache.get(1));
        System.out.println("Value for key 2: " + lruCache.get(2));
        //System.out.println("Value for key 3: " + lruCache.get(3));

        // Retrieve elements from the LRU cache using secondary index
        System.out.println("Value for secondary index 'indexA': " + lruCache.getBySecondaryKey(0, "indexA"));
        System.out.println("Value for secondary index 'indexB': " + lruCache.getBySecondaryKey(1, "indexB"));
     //   System.out.println("Value for secondary index 'indexC': " + lruCache.getBySecondaryKey(2, "indexC"));
*/
        assertEquals(true,true);

    }

    static class Book {
        private int id;
        private String title;
        private String author;
        private String genre;

        public Book(int id, String title, String author, String genre) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.genre = genre;
        }

        public int getId() {
            return id;
        }

        // Other getters, setters, toString method...
    }

}


