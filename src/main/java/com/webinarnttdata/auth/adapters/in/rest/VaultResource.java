package com.webinarnttdata.auth.adapters.in.rest;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
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
    @Counted(value = "vault.secret.count", description = "Número de veces que se consultó un secreto")
    @Timed(value = "vault.secret.timer", description = "Tiempo en obtener un secreto")
    public String getSecret() {
        Map<String, String> secret = kvSecretEngine.readSecret("myapp");
        return "Password de DB: " + secret.get("database.password");
    }
}
