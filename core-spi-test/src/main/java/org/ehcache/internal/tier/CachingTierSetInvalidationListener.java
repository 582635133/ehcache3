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

package org.ehcache.internal.tier;

import org.ehcache.expiry.Expirations;
import org.ehcache.spi.cache.Store;
import org.ehcache.spi.cache.tiering.CachingTier;
import org.ehcache.spi.test.After;
import org.ehcache.spi.test.Before;
import org.ehcache.spi.test.SPITest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the {@link CachingTier#clear()} contract of the
 * {@link CachingTier CachingTier} interface.
 * <p/>
 *
 * @author Aurelien Broszniowski
 */
public class CachingTierSetInvalidationListener<K, V> extends CachingTierTester<K, V> {

  private CachingTier tier;

  public CachingTierSetInvalidationListener(final CachingTierFactory<K, V> factory) {
    super(factory);
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
    if (tier != null) {
//      tier.close();
      tier = null;
    }
  }

  @SPITest
  @SuppressWarnings("unchecked")
  public void cantSetInvalidationListenerMoreThanOnce() {

    V originalValue = factory.createValue(1);

    final Store.ValueHolder<V> valueHolder = mock(Store.ValueHolder.class);
    when(valueHolder.value()).thenReturn(originalValue);

    tier = factory.newCachingTier(factory.newConfiguration(factory.getKeyType(), factory.getValueType(),
        1L, null, null, Expirations.noExpiration()));

    CachingTier.InvalidationListener invalidationListener = mock(CachingTier.InvalidationListener.class);
    tier.setInvalidationListener(invalidationListener);

    try {
      tier.setInvalidationListener(invalidationListener);
      throw new AssertionError();
    } catch (IllegalStateException e) {
      // expected
    }
  }
}
