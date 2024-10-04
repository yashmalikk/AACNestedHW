package edu.grinnell.csc207.util;

import java.util.Arrays;

/**
 * A basic implementation of Associative Arrays with keys of type K
 * and values of type V. Associative Arrays store key/value pairs
 * and permit you to look up values by key.
 *
 * @param <K> the key type
 * @param <V> the value type
 *
 * @author Yash Malik
 * @author Samuel A. Rebelsky
 */
public class AssociativeArray<K, V> {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The default capacity of the initial array.
   */
  static final int DEFAULT_CAPACITY = 16;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The size of the associative array (the number of key/value pairs).
   */
  int size;

  /**
   * The array of key/value pairs.
   */
  KVPair<K, V>[] pairs;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new, empty associative array.
   */
  @SuppressWarnings("unchecked")
  public AssociativeArray() {
    this.pairs = (KVPair<K, V>[]) new KVPair[DEFAULT_CAPACITY]; // Initialize pairs
    this.size = 0;
  } // AssociativeArray()  // End of constructor

  /**
   * Creates a deep copy of the AssociativeArray it is called with.
   *
   * @return a deep copy of the array.
   */
  public AssociativeArray<K, V> clone() {
    AssociativeArray<K, V> newArray = new AssociativeArray<>();
    newArray.pairs = Arrays.copyOf(this.pairs, this.size);
    for (int i = 0; i < this.size; i++) {
      newArray.pairs[i] = this.pairs[i].clone(); // Deep copy of KVPair
    } // iterate over array.
    newArray.size = this.size;
    return newArray;
  } // clone()  // End of clone method

  /**
   * @return gives us the Array in a string.
   */
  public String toString() {
    StringBuilder returnVal = new StringBuilder();
    for (int i = 0; i < this.size; i++) {
      if (i > 0) {
        returnVal.append(", ");
      } // start with ,
      returnVal.append(pairs[i].toString()); // Use KVPair's toString
    } // iterate over array.
    return "{" + returnVal + "}";
  } // toString()  // End of toString method

  /**
   *
   * @param key key that will have value.
   * @param value value to be stored with key.
   * @throws NullKeyException if null-key is passed.
   */
  public void set(K key, V value) throws NullKeyException {
    if (key == null) {
      throw new NullKeyException("Key cannot be null");
    } // NullKeyException check
    for (int i = 0; i < this.size; i++) {
      if (pairs[i].key.equals(key)) {
        pairs[i].val = value; // Update existing key
        return;
      } // Update key found
    } // iterate over pairs
    if (size >= pairs.length) {
      expand();
    } // Check if expand is needed
    pairs[size++] = new KVPair<>(key, value); // Add new key/value pair
  } // set(K,V)  // End of set method

  /**
   *
   * @param key to return the value held.
   * @return gives the value associated with key.
   * @throws KeyNotFoundException if key is not found.
   */
  public V get(K key) throws KeyNotFoundException {
    if (key == null) {
      throw new KeyNotFoundException("Key cannot be null");
    } // NullKeyException check
    for (int i = 0; i < this.size; i++) {
      if (pairs[i].key.equals(key)) {
        return pairs[i].val; // Return value
      } // Key found
    } // iterate over pairs
    throw new KeyNotFoundException("Key not found: " + key);
  } // get(K)  // End of get method

  /**
   *
   * @param key to check if key is in Array.
   * @return true if key is present.
   */
  public boolean hasKey(K key) {
    if (key == null) {
      return false;
    } // Null key check
    for (int i = 0; i < this.size; i++) {
      if (pairs[i].key.equals(key)) {
        return true; // Key found
      } // Key found
    } // iterate over pairs
    return false;
  } // hasKey(K)  // End of hasKey method

  /**
   *
   * @param key to remove from array.
   */
  public void remove(K key) {
    if (key == null) {
      return;
    } // Null key check
    for (int i = 0; i < this.size; i++) {
      if (pairs[i].key.equals(key)) {
        System.arraycopy(pairs, i + 1, pairs, i, size - i - 1);
        pairs[--size] = null; // Clear last entry
        return;
      } // Key found and removed
    } // iterate over pairs
  } // remove(K)  // End of remove method

  /**
   * @return gives us the size of array.
   */
  public int size() {
    return this.size;
  } // size()  // End of size method

  /**
   * Expands the array if needed.
   */
  void expand() {
    this.pairs = Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // expand()  // End of expand method

  /**
   *
   * @param key to find in array
   * @return the index of key
   * @throws KeyNotFoundException if key is not found.
   */
  int find(K key) throws KeyNotFoundException {
    if (key == null) {
      throw new KeyNotFoundException("Key cannot be null");
    } // NullKeyException check
    for (int i = 0; i < this.size; i++) {
      if (pairs[i].key.equals(key)) {
        return i; // Key found
      } // Key found
    } // iterate over pairs
    throw new KeyNotFoundException("Key not found: " + key);
  } // find(K)  // End of find method
} // class AssociativeArray  // End of AssociativeArray class
