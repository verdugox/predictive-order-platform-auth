package com.webinarnttdata.auth.adapters.in.rest;

import io.quarkus.vault.VaultKVSecretEngine;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.Map;

@Path("/vault")
public class VaultResource {

    @Inject
    VaultKVSecretEngine kvSecretEngine;

    @GET
    public String getSecret() {
        Map<String, String> secret = kvSecretEngine.readSecret("myapp");
        return "Password de DB: " + secret.get("database.password");
    }
}
