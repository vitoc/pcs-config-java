// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.microsoft.azure.iotsolutions.uiconfig.services.external.SimulationServiceClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpRequest;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpResponse;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import helpers.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class SimulationServiceClientTest {
    private String MockServiceUri = "http://mockhubManager";
    private IHttpClient mockHttpClient;
    private SimulationServiceClient client;
    private String content = "";

    @Before
    public void setUp() throws IOException {
        mockHttpClient = Mockito.mock(IHttpClient.class);
        try (InputStream is = this.getClass().getResourceAsStream("json/SimulationServiceClientTest.json")) {
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
        client = new SimulationServiceClient(
                mockHttpClient,
                new ServicesConfig(null, MockServiceUri, null, 0, 0));
        HashSet<String> result = this.client.GetDevicePropertyNamesAsync().toCompletableFuture().get();
        TreeSet<String> treeset = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        treeset.addAll(result);
        assertEquals(String.join(",", treeset), "address.NO,address.street,address1.NO,address1.street,Latitude,Latitude1,Location,Longitude,Type,Type1");
    }
}
