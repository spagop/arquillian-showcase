package com.acme.cdi.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DocumentEventTestCase
{
   @Deployment
   public static Archive<?> createDeployment()
   {
      return ShrinkWrap.create(JavaArchive.class)
         .addPackage(Document.class.getPackage())
         .addManifestResource(EmptyAsset.INSTANCE, "beans.xml");
   }
   
   @Inject WordProcessor processor;
   
   @Inject PrintSpool spool;
   
   @Test
   public void print_spool_should_observe_print_event()
   {
      processor.create(5);
      processor.print();
      assertNotNull(spool.getDocumentsProcessed());
      assertEquals(1, spool.getDocumentsProcessed().size());
      assertEquals(1, spool.getDocumentsProcessed(JobSize.MEDIUM).size());
      processor.close();
      
      processor.create(100);
      processor.print();
      assertNotNull(spool.getDocumentsProcessed());
      assertEquals(2, spool.getDocumentsProcessed().size());
      assertEquals(1, spool.getDocumentsProcessed(JobSize.LARGE).size());
   }
}
