// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.microsoft.azure.iotsolutions.uiconfig.services.external.IothubManagerServiceClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpRequest;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpResponse;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceTwinName;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import helpers.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.io.*;
import java.net.URISyntaxException;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class IothubManagerServiceClientTest {

    private String MockServiceUri = "http://mockhubManager";
    private IHttpClient mockHttpClient;
    private IothubManagerServiceClient client;
    private String content = "";

    @Before
    public void setUp() throws IOException {
        mockHttpClient = Mockito.mock(IHttpClient.class);
        try (InputStream is = this.getClass().getResourceAsStream("json/IothubManagerServiceClientTest.json")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line = reader.readLine();
                while (line != null) {
                    content += line;
                    line = reader.readLine();
                }
            }
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getDeviceTwinNamesAsyncTest() throws URISyntaxException, ExecutionException, InterruptedException {
        HttpResponse response = new HttpResponse(200, null, content);
        Mockito.when(mockHttpClient.getAsync(Mockito.any(HttpRequest.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> response));
        client = new IothubManagerServiceClient(
                mockHttpClient,
                new ServicesConfig(null, null, MockServiceUri, 0, 0));
        DeviceTwinName result = this.client.GetDeviceTwinNamesAsync().toCompletableFuture().get();
        assertEquals(String.join(",", new TreeSet<String>(result.getTags())), "device1e,device1f.f,device2e,device2f.f,g");
        assertEquals(String.join(",", new TreeSet<String>(result.getReportedProperties())), "c,device1a,device1b.b,device2a,device2b.b");
    }
}
