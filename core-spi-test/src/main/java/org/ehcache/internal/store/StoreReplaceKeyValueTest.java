/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ehcache.internal.store;

import org.ehcache.config.Eviction;
import org.ehcache.exceptions.CacheAccessException;
import org.ehcache.spi.cache.Store;
import org.ehcache.spi.test.After;
import org.ehcache.spi.test.SPITest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test the {@link org.ehcache.spi.cache.Store#replace(Object, Object)} contract of the
 * {@link org.ehcache.spi.cache.Store Store} interface.
 * <p/>
 *
 * @author Aurelien Broszniowski
 */

public class StoreReplaceKeyValueTest<K, V> extends SPIStoreTester<K, V> {

  public StoreReplaceKeyValueTest(final StoreFactory<K, V> factory) {
    super(factory);
  }

  protected Store<K, V> kvStore;
  protected Store kvStore2;

  @After
  public void tearDown() {
    if (kvStore != null) {
//      kvStore.close();
      kvStore = null;
    }
    if (kvStore2 != null) {
//      kvStore2.close();
      kvStore2 = null;
    }
  }

  @SPITest
  public void replaceKeyAndValue()
      throws IllegalAccessException, InstantiationException, CacheAccessException {
    kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, Eviction
        .all(), null));

    K key = factory.createKey(1);
    V originalValue = factory.createValue(1L);

    kvStore.put(key, originalValue);

    V newValue = factory.createValue(2L);

    try {
      kvStore.replace(key, newValue);
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }

    assertThat(kvStore.get(key).value(), is(equalTo(newValue)));
  }

  @SPITest
  public void replaceReturnsOldValue()
      throws IllegalAccessException, InstantiationException, CacheAccessException {
    kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, Eviction.all(), null));

    K key = factory.createKey(1);
    V originalValue = factory.createValue(1);

    kvStore.put(key, originalValue);

    V newValue = factory.createValue(2);

    try {
      assertThat(kvStore.replace(key, newValue).value(), is(equalTo(originalValue)));
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SPITest
  public void replaceKeyNotMappedReturnsNull()
      throws IllegalAccessException, InstantiationException, CacheAccessException {
    kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, Eviction.all(), null));

    K key = factory.createKey(1);

    V newValue = factory.createValue(1);

    assertThat(kvStore.containsKey(key), is(false));

    try {
      assertThat(kvStore.replace(key, newValue), is(nullValue()));
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SPITest
  public void nullKeyThrowsException()
      throws IllegalAccessException, InstantiationException {
    kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));

    K key = null;
    V value = factory.createValue(1);

    try {
      kvStore.replace(key, value);
      throw new AssertionError("Expected NullPointerException because the key is null");
    } catch (NullPointerException e) {
      // expected
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SPITest
  public void nullValueThrowsException()
      throws IllegalAccessException, InstantiationException {
    kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));

    K key = factory.createKey(1);
    V value = null;

    try {
      kvStore.replace(key, value);
      throw new AssertionError("Expected NullPointerException because the value is null");
    } catch (NullPointerException e) {
      // expected
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SPITest
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void wrongKeyTypeThrowsException()
      throws IllegalAccessException, InstantiationException {
    kvStore2 = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));

    V value = factory.createValue(1);

    try {
      if (this.factory.getKeyType() == String.class) {
        kvStore2.replace(1.0f, value);
      } else {
        kvStore2.replace("key", value);
      }
      throw new AssertionError("Expected ClassCastException because the key is of the wrong type");
    } catch (ClassCastException e) {
      // expected
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SPITest
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void wrongValueTypeThrowsException()
      throws IllegalAccessException, InstantiationException {
    kvStore2 = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));

    K key = factory.createKey(1);

    try {
      if (this.factory.getValueType() == String.class) {
        kvStore2.replace(key, 1.0f);
      } else {
        kvStore2.replace(key, "value");
      }
      throw new AssertionError("Expected ClassCastException because the value is of the wrong type");
    } catch (ClassCastException e) {
      // expected
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }
}
