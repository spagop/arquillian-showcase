/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acme.multinode.grid;

import javax.inject.Inject;

import junit.framework.Assert;

import org.infinispan.Cache;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.OperateOnDeployment;
import org.jboss.arquillian.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * MultiContainerTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class ClusteredCacheTestCase
{
   @Deployment(testable = false) @TargetsContainer("container.standby")
   public static WebArchive createStandbyDeployment()
   {
      return Deployments.createStandByServer();
   }
   
   @Deployment(name = "dep.active-1") @TargetsContainer("container.active-1")
   public static WebArchive createTestDeployment()
   {
      return Deployments.createActiveClient();
   }

   @Deployment(name = "dep.active-2") @TargetsContainer("container.active-2")
   public static WebArchive createTestDeployment2()
   {
      return Deployments.createActiveClient();
   }
   
   @Inject
   private Cache<String, Integer> cache;
   
   @Test @OperateOnDeployment("dep.active-1")
   public void callActive1() throws Exception 
   {
      int count = incrementCache(cache);
      System.out.println("Cache incremented, current count: " + count);
      Assert.assertEquals(1, count);
   }
   
   @Test @OperateOnDeployment("dep.active-2")
   public void callActive2() throws Exception 
   {
      int count = incrementCache(cache);
      System.out.println("Cache incremented, current count: " + count);
      Assert.assertEquals(2, count);
   }

   @Test @OperateOnDeployment("dep.active-2")
   public void callActive3() throws Exception 
   {
      int count = incrementCache(cache);
      System.out.println("Cache incremented, current count: " + count);
      Assert.assertEquals(3, count);
   }

   @Test @OperateOnDeployment("dep.active-1")
   public void callActive4() throws Exception 
   {
      int count = incrementCache(cache);
      System.out.println("Cache incremented, current count: " + count);
      Assert.assertEquals(4, count);
   }

   @Test @OperateOnDeployment("dep.active-2")
   public void callActive5() throws Exception 
   {
      int count = incrementCache(cache);
      System.out.println("Cache incremented, current count: " + count);
      Assert.assertEquals(5, count);
   }

   private Integer incrementCache(Cache<String, Integer> cache)
   {
      String key = "counter";
      Integer counter = cache.get(key);
      Integer newCounter;
      if (counter != null)
      {
         newCounter = counter.intValue() + 1;
      }
      else
      {
         newCounter = 1;
      }
      cache.put(key, newCounter);
      return newCounter;
   }
}
