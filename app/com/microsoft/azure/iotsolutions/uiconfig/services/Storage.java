// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroup;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Logo;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Theme;
import play.libs.Json;

import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class Storage implements IStorage {

    static String SolutionCollectionId = "solution-settings";
    static String ThemeKey = "theme";
    static String LogoKey = "logo";
    static String UserCollectionId = "user-settings";
    static String DeviceGroupCollectionId = "deviceGroups";
    private final IStorageAdapterClient client;

    @Inject
    public Storage(IStorageAdapterClient client) {
        this.client = client;
    }

    private static <T> String toJson(T o) {
        return Json.stringify(Json.toJson(o));
    }

    private static <A> A fromJson(String json, Class<A> clazz) {
        return Json.fromJson(Json.parse(json), clazz);
    }

    @Override
    public CompletionStage<Object> getThemeAsync() {
        try {
            return client.getAsync(SolutionCollectionId, ThemeKey).thenApplyAsync(m ->
                    fromJson(m.getData(), Object.class)
            );
        } catch (Exception ex) {
            return CompletableFuture.supplyAsync(() -> Theme.Default);
        }
    }

    @Override
    public CompletionStage<Object> setThemeAsync(Object theme) throws BaseException {
        String value = toJson(theme);
        return client.updateAsync(SolutionCollectionId, ThemeKey, value, "*").thenApplyAsync(m ->
                fromJson(m.getData(), Object.class)
        );
    }

    @Override
    public CompletionStage<Object> getUserSetting(String id) {
        try {
            return client.getAsync(UserCollectionId, id).thenApplyAsync(m ->
                    fromJson(m.getData(), Object.class)
            );
        } catch (Exception ex) {
            return CompletableFuture.supplyAsync(() -> new Object());
        }
    }

    @Override
    public CompletionStage<Object> setUserSetting(String id, Object setting) throws BaseException {
        String value = toJson(setting);
        return client.updateAsync(UserCollectionId, id, value, "*").thenApplyAsync(m ->
                fromJson(m.getData(), Object.class)
        );
    }

    @Override
    public CompletionStage<Logo> getLogoAsync() {
        try {
            return client.getAsync(SolutionCollectionId, LogoKey)
                    .handle((m, error) -> {
                        if (error != null) {
                            return Logo.Default;
                        } else {
                            return fromJson(m.getData(), Logo.class);
                        }
                    });
        } catch (BaseException ex) {
            throw new CompletionException("Unable to get logo", ex);
        }
    }

    @Override
    public CompletionStage<Logo> setLogoAsync(Logo model) throws BaseException {
        String value = toJson(model);
        return client.updateAsync(SolutionCollectionId, LogoKey, value, "*").thenApplyAsync(m ->
                fromJson(m.getData(), Logo.class)
        );
    }

    @Override
    public CompletionStage<Iterable<DeviceGroup>> getAllDeviceGroupsAsync() throws BaseException {
        return client.getAllAsync(DeviceGroupCollectionId).thenApplyAsync(m -> {
            return StreamSupport.stream(m.Items.spliterator(), false).map(Storage::createGroup).collect(Collectors.toList());
        });
    }

    @Override
    public CompletionStage<DeviceGroup> getDeviceGroupAsync(String id) throws BaseException {
        return client.getAsync(DeviceGroupCollectionId, id).thenApplyAsync(m -> {
            return createGroup(m);
        });
    }

    @Override
    public CompletionStage<DeviceGroup> createDeviceGroupAsync(DeviceGroup input) throws BaseException {
        String value = toJson(input);
        return client.createAsync(DeviceGroupCollectionId, value).thenApplyAsync(m ->
                createGroup(m)
        );
    }

    @Override
    public CompletionStage<DeviceGroup> updateDeviceGroupAsync(String id, DeviceGroup input, String etag) throws BaseException {
        String value = toJson(input);
        return client.updateAsync(DeviceGroupCollectionId, id, value, etag).thenApplyAsync(m ->
                createGroup(m)
        );
    }

    @Override
    public CompletionStage deleteDeviceGroupAsync(String id) throws BaseException {
        return client.deleteAsync(DeviceGroupCollectionId, id);
    }

    private static DeviceGroup createGroup(ValueApiModel input) {
        DeviceGroup output = fromJson(input.getData(), DeviceGroup.class);
        output.setId(input.getKey());
        output.setETag(input.getETag());
        return output;
    }
}
