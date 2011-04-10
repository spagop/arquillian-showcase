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
package com.acme.jms.descriptor;

import org.jboss.shrinkwrap.descriptor.api.Node;
import org.jboss.shrinkwrap.descriptor.impl.base.NodeProviderImplBase;
import org.jboss.shrinkwrap.descriptor.impl.base.XMLExporter;
import org.jboss.shrinkwrap.descriptor.spi.DescriptorExporter;

/**
 * JMSDescriptorImpl
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class JMSDescriptorImpl extends NodeProviderImplBase implements JMSDescriptor
{
   // -------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   private final Node configuration;

   // -------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   public JMSDescriptorImpl(String descriptorName)
   {
      this(descriptorName, new Node("configuration")
               .attribute("xmlns", "urn:hornetq")
               .attribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
               .attribute("xsi:schemaLocation", "urn:hornetq ../schemas/hornetq-jms.xsd"));
   }

   public JMSDescriptorImpl(String descriptorName, Node configuration)
   {
      super(descriptorName);
      this.configuration = configuration;
   }

   /* (non-Javadoc)
    * @see com.acme.jms.descriptor.JMSDescriptor#qeueu(java.lang.String, java.lang.String)
    */
   @Override
   public JMSDescriptor queue(String name, String jndi)
   {
      configuration.getOrCreate("queue@name=" + name)
         .attribute("name", name)
            .getOrCreate("entry").attribute("name", jndi);
      
      return this;
   }

   /* (non-Javadoc)
    * @see com.acme.jms.descriptor.JMSDescriptor#topic(java.lang.String, java.lang.String)
    */
   @Override
   public JMSDescriptor topic(String name, String jndi)
   {
      configuration.getOrCreate("topic@name=" + name)
      .attribute("name", name)
         .getOrCreate("entry").attribute("name", jndi);

      return this;
   }
   
   // -------------------------------------------------------------------------------------||
   // Required Impl - NodeProvider --------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.descriptor.spi.NodeProvider#getRootNode()
    */
   @Override
   public Node getRootNode()
   {
      return configuration;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.descriptor.impl.base.NodeProviderImplBase#getExporter()
    */
   @Override
   protected DescriptorExporter getExporter()
   {
      return new XMLExporter();
   }
}
