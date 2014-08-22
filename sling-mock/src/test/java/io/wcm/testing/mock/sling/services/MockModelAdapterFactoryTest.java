/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.testing.mock.sling.services;

import static org.junit.Assert.assertEquals;
import io.wcm.testing.mock.osgi.MockOsgiFactory;
import io.wcm.testing.mock.sling.MockSlingFactory;
import io.wcm.testing.mock.sling.servlet.MockSlingHttpServletRequest;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.inject.Inject;

import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.models.impl.injectors.OSGiServiceInjector;
import org.apache.sling.models.impl.injectors.RequestAttributeInjector;
import org.apache.sling.models.spi.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

@SuppressWarnings("javadoc")
public class MockModelAdapterFactoryTest {

  private ComponentContext componentContext;
  private BundleContext bundleContext;

  @Before
  public void setUp() throws Exception {
    componentContext = MockOsgiFactory.newComponentContext();
    bundleContext = componentContext.getBundleContext();
    MockSlingFactory.setAdapterManagerBundleContext(bundleContext);

    // register sling models adapter factory
    bundleContext.registerService(AdapterFactory.class.getName(), new MockModelAdapterFactory(componentContext), null);

    // register some injectors
    registerInjector(new RequestAttributeInjector(), 4000);
    OSGiServiceInjector osgiServiceInjector = new OSGiServiceInjector();
    osgiServiceInjector.activate(componentContext);
    registerInjector(osgiServiceInjector, 5000);
  }

  private void registerInjector(Injector injector, int serviceRanking) {
    Dictionary<String, Object> props = new Hashtable<>();
    props.put(Constants.SERVICE_RANKING, serviceRanking);
    bundleContext.registerService(Injector.class.getName(), injector, props);
  }

  @After
  public void tearDown() throws Exception {
    MockSlingFactory.clearAdapterManagerBundleContext();
  }

  // TODO: finalize implementation, then activate test
  @Test
  @Ignore
  public void testRequestAttribute() {
    MockSlingHttpServletRequest request = new MockSlingHttpServletRequest();
    request.setAttribute("prop1", "myValue");
    RequestAttributeModel model = request.adaptTo(RequestAttributeModel.class);
    assertEquals("myValue", model.getProp1());
  }

  private interface RequestAttributeModel {
    @Inject
    String getProp1();
  }

}