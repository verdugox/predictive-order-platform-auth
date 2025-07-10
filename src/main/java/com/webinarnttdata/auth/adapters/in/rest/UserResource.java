package com.webinarnttdata.auth.adapters.in.rest;

import com.webinarnttdata.auth.application.UserService;
import com.webinarnttdata.auth.domain.User;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.logging.Logger;

@Path("api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Inject
    UserService userService;

    // Inyectamos SecurityIdentity para ver los roles que Quarkus detecta
    @Inject
    SecurityIdentity identity;

    @POST
    @Path("/register")
    public Uni<Response> register(User user) {
        LOG.info("Llamando al endpoint /register con usuario: " + user.username);
        return userService.register(user)
                .onItem().invoke(u -> LOG.info("Usuario registrado exitosamente: " + u.username))
                .onFailure().invoke(e -> LOG.error("Error al registrar usuario", e))
                .onItem().transform(u -> Response.ok(u).build());
    }

    @GET
    @Path("/{username}")
    @RolesAllowed("admin") // Solo usuarios con rol 'admin' pueden acceder
    public Uni<Response> getByUsername(@PathParam("username") String username,
                                       @Context SecurityContext ctx) {
        LOG.info("Entrando a /api/users/" + username);
        LOG.info("Usuario autenticado: " + (ctx.getUserPrincipal() != null ? ctx.getUserPrincipal().getName() : "null"));

        // Mostrar todos los roles que Quarkus ve en el SecurityIdentity
        LOG.info("Roles detectados en SecurityIdentity:");
        identity.getRoles().forEach(role -> LOG.info("ROL DETECTADO: " + role));

        if (ctx.getUserPrincipal() == null) {
            LOG.warn("No se encontró SecurityContext, probablemente sin token o inválido");
            return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED).entity("Token inválido o no presente").build());
        }

        LOG.info("Token válido. Verificando roles del usuario...");

        boolean hasRole = ctx.isUserInRole("admin");
        LOG.info("¿Usuario tiene rol 'admin'? -> " + hasRole);
        if (!hasRole) {
            LOG.warn("Usuario autenticado NO tiene rol 'admin'. Retornando 403 Forbidden.");
            return Uni.createFrom().item(Response.status(Response.Status.FORBIDDEN).entity("No tienes permisos suficientes.").build());
        }

        LOG.info("Usuario autorizado correctamente. Buscando usuario: " + username);

        return userService.findByUsername(username)
                .onItem().invoke(u -> {
                    if (u != null) {
                        LOG.info("Usuario encontrado: " + u.username);
                    } else {
                        LOG.warn("Usuario no encontrado: " + username);
                    }
                })
                .onItem().transform(u -> {
                    if (u == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    } else {
                        return Response.ok(u).build();
                    }
                });
    }

}
