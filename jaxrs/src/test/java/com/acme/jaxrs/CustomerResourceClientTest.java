package com.acme.jaxrs;

import static org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.ProviderType.HIBERNATE;
import static org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.SchemaGenerationModeType.CREATE_DROP;

import java.net.URL;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.arquillian.api.ArquillianResource;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.acme.jaxrs.model.Customer;
import com.acme.jaxrs.resource.CustomerResource;

@RunWith(Arquillian.class)
public class CustomerResourceClientTest
{
   private static final String REST_PATH = "rest";
   
   @Deployment(testable = false)
   public static Archive<?> createDeployment() 
   {
      return ShrinkWrap.create(WebArchive.class)
            .addPackage(Customer.class.getPackage())
            .addClasses(CustomerResource.class)
            .addDirectory("WEB-INF/lib") // RESTEASY-507
            .addAsWebInfResource("import.sql", "classes/import.sql")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            
            .addAsWebInfResource(new StringAsset(
                  Descriptors.create(PersistenceDescriptor.class)
                     .version("1.0")
                     .persistenceUnit("sellmore")
                        .provider(HIBERNATE)   
                        .jtaDataSource("java:/DefaultDS")
                        .schemaGenerationMode(CREATE_DROP)
                        .showSql()
                     .exportAsString()
               ), "classes/META-INF/persistence.xml")
               
            .setWebXML(new StringAsset(
                  Descriptors.create(WebAppDescriptor.class)
                     .version("3.0")
                     .contextParam("resteasy.scan", "true")
                     .contextParam("resteasy.servlet.mapping.prefix", REST_PATH)
                        .exportAsString()
            ));
   }

   @Test
   public void testGetCustomerByIdUsingClientRequest(@ArquillianResource URL base) throws Exception 
   {
      // GET http://localhost:8080/test/rest/customer/1
      ClientRequest request = new ClientRequest(new URL(base, REST_PATH + "/customer/1").toExternalForm());
      request.header("Accept", MediaType.APPLICATION_XML);

      // we're expecting a String back
      ClientResponse<String> responseObj = request.get(String.class);

      Assert.assertEquals(200, responseObj.getStatus());
      System.out.println("GET /customer/1 HTTP/1.1\n\n" + responseObj.getEntity());
      
      Assert.assertEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
      		"<customer><id>1</id><name>Acme Corporation</name></customer>", 
      		responseObj.getEntity());
   }
}
