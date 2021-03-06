// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers;

import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.StatusApiModel;
import play.mvc.Result;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

/**
 * Service health check endpoint.
 */
@Singleton
public final class StatusController {
    /**
     * @return Service health details.
     */
    public Result index() {
        return ok(toJson(new StatusApiModel(true, "Alive and well")));
    }
}
