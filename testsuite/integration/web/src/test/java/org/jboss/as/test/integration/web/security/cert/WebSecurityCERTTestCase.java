/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.test.integration.web.security.cert;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.categories.CommonCriteria;
import org.jboss.as.test.integration.web.security.SecuredServlet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Unit test for CLIENT-CERT authentication.
 *
 * @author <a href="mailto:mmoyses@redhat.com">Marcus Moyses</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(WebCERTTestsSetup.class)
@Category(CommonCriteria.class)
@Ignore("[WFLY-15749] Update WebSecurityCERTTestCase to use Elytron SSLContext.")
public class WebSecurityCERTTestCase {

    @ArquillianResource
    private ManagementClient mgmtClient;

    @Deployment
    public static WebArchive deployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "web-secure-client-cert.war");
        war.addClass(SecuredServlet.class);

        war.addAsWebInfResource(WebSecurityCERTTestCase.class.getPackage(), "jboss-web.xml", "jboss-web.xml");
        war.addAsWebInfResource(WebSecurityCERTTestCase.class.getPackage(), "web.xml", "web.xml");

        return war;
    }

    private static CloseableHttpClient getHttpsClient(String alias) {
        try {
            // Ensure the protocol enabled here matches what was configured on server side
            // to avoid failures on IBM JDK
//            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
//            JBossJSSESecurityDomain jsseSecurityDomain = new JBossJSSESecurityDomain("client-cert");
//            jsseSecurityDomain.setKeyStorePassword(WebCERTTestsSetup.getPassword());
//            URL keystore = WebCERTTestsSetup.getClientKeystoreFile().toURI().toURL();
//            jsseSecurityDomain.setKeyStoreURL(keystore.getPath());
//            jsseSecurityDomain.setClientAlias(alias);
//            jsseSecurityDomain.reloadKeyAndTrustStore();
//            KeyManager[] keyManagers = jsseSecurityDomain.getKeyManagers();
//            TrustManager[] trustManagers = jsseSecurityDomain.getTrustManagers();
//            ctx.init(keyManagers, trustManagers, null);
//            HostnameVerifier verifier = (string, ssls) -> true;
//            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx, verifier);
//            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//                    .register("https", ssf)
//                    .build();
//
//            HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
//
//            return HttpClientBuilder.create()
//                    .setSSLSocketFactory(ssf)
//                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
//                    .setConnectionManager(ccm)
//                    .build();
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Creating of HTTPS client instance has failed.", e);
        }
    }

    @Test
    public void testClientCertSuccessfulAuth() throws Exception {
        makeCall("test client", 200);
    }

    @Test
    public void testClientCertUnsuccessfulAuth() throws Exception {
        makeCall("test client 2", 403);
    }

    protected void makeCall(String alias, int expectedStatusCode) throws Exception {
        try (CloseableHttpClient httpclient = getHttpsClient(alias)) {
            HttpGet httpget = new HttpGet("https://" + mgmtClient.getMgmtAddress() + ":" +
                    WebCERTTestsSetup.HTTPS_PORT + "/web-secure-client-cert/secured/");
            HttpResponse response = httpclient.execute(httpget);

            StatusLine statusLine = response.getStatusLine();
            assertEquals(expectedStatusCode, statusLine.getStatusCode());
            EntityUtils.consume(response.getEntity());
        }
    }
}
