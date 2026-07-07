package com.amos_tech_code.zoner.devices.controller;

import com.amos_tech_code.zoner.devices.dto.request.RegisterDeviceRequest;
import com.amos_tech_code.zoner.devices.dto.response.DeviceResponse;
import com.amos_tech_code.zoner.devices.services.DeviceService;
import com.amos_tech_code.zoner.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registerDevice(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody RegisterDeviceRequest request
    ) {

        deviceService.register(user.id(), request);

    }

    @GetMapping
    public List<DeviceResponse> getDevices(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return deviceService.getDevices(user.id());
    }

    @DeleteMapping("/current")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterDevice(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam @Valid String deviceId
    ) {
        deviceService.unregister(user.id(), deviceId);
    }

}
