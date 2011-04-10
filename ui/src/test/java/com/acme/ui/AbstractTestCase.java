/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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
package com.acme.ui;

import java.io.File;

import org.jboss.arquillian.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.SchemaGenerationModeType;

/**
 * This class shares deployment method for all available tests.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public abstract class AbstractTestCase
{
   /**
    * Creates a WAR of a CDI-based application using ShrinkWrap
    * 
    * @return WebArchive to be tested
    */
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return ShrinkWrap.create(WebArchive.class, "cdi-login.war")
            .addClasses(Credentials.class, LoggedIn.class, Login.class, User.class, UsersProducer.class)            
            .addAsResource(new StringAsset(
                  Descriptors.create(PersistenceDescriptor.class)
                     .persistenceUnit("test")
                        .jtaDataSource("java:/H2DS")
                        .schemaGenerationMode(SchemaGenerationModeType.CREATE_DROP)
                        .showSql()
                      .exportAsString()
                  ), "META-INF/persistence.xml")
            .addAsResource("import.sql", "import.sql")
            .addAsWebResource(new File("src/main/webapp/index.html"))
            .addAsWebResource(new File("src/main/webapp/home.xhtml"))
            .addAsWebResource(new File("src/main/webapp/users.xhtml"))
            .addAsWebResource(new File("src/main/webapp/template.xhtml"))
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
            .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
   }
}
