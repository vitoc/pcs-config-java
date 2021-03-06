// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;

@ImplementedBy(DeviceTelemetryClient.class)
public interface IDeviceTelemetryClient {
    CompletionStage UpdateRuleAsync(RuleApiModel rule, String etag) throws UnsupportedEncodingException, ExternalDependencyException, URISyntaxException;
}
