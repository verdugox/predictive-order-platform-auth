package com.webinarnttdata.auth.adapters.in.rest;

import com.webinarnttdata.auth.application.services.AuthFacade;
import com.webinarnttdata.auth.application.services.UserService;
import com.webinarnttdata.auth.domain.User;
import com.webinarnttdata.auth.domain.patterns.AppLogger;
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

    // Obtienes el Logger Singleton
    private static final Logger logger = AppLogger.getInstance();

    @Inject
    UserService userService;
    // Inyectamos SecurityIdentity para ver los roles que Quarkus detecta
    @Inject
    SecurityIdentity identity;
    @Inject
    AuthFacade authFacade;

    @POST
    @Path("/register")
    public Uni<Response> register(User user) {
        logger.info(AppLogger.colorSuccess("Llamando al endpoint /register con usuario: " + user.username));
        return userService.register(user)
                .onItem().invoke(u -> logger.info(AppLogger.colorSuccess("Usuario registrado exitosamente: " + u.username)))
                .onFailure().invoke(e -> logger.error(AppLogger.colorError("Error al registrar usuario"), e))
                .onItem().transform(u -> Response.ok(u).build());
    }

    @GET
    @Path("/{username}")
    @RolesAllowed("admin") // Solo usuarios con rol 'admin' pueden acceder
    public Uni<Response> getByUsername(@PathParam("username") String username,
                                       @Context SecurityContext ctx) {
        logger.info(AppLogger.colorSuccess("Entrando a /api/users/" + username));
        logger.info(AppLogger.colorSuccess("Usuario autenticado: " + (ctx.getUserPrincipal() != null ? ctx.getUserPrincipal().getName() : "null")));

        // Mostrar todos los roles que Quarkus ve en el SecurityIdentity
        logger.info(AppLogger.colorSuccess("Roles detectados en SecurityIdentity:"));
        identity.getRoles().forEach(role -> logger.info(AppLogger.colorSuccess("ROL DETECTADO: " + role)));

        if (ctx.getUserPrincipal() == null) {
            logger.warn(AppLogger.colorWarning("No se encontró SecurityContext, probablemente sin token o inválido"));
            return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED).entity("Token inválido o no presente").build());
        }

        logger.info(AppLogger.colorInfo("Token válido. Verificando roles del usuario..."));

        boolean hasRole = ctx.isUserInRole("admin");
        logger.info(AppLogger.colorInfo("¿Usuario tiene rol 'admin'? -> " + hasRole));
        if (!hasRole) {
            logger.warn(AppLogger.colorWarning("Usuario autenticado NO tiene rol 'admin'. Retornando 403 Forbidden."));
            return Uni.createFrom().item(Response.status(Response.Status.FORBIDDEN).entity("No tienes permisos suficientes.").build());
        }

        logger.info(AppLogger.colorInfo("Usuario autorizado correctamente. Buscando usuario: " + username));

        return userService.findByUsername(username)
                .onItem().invoke(u -> {
                    if (u != null) {
                        logger.info(AppLogger.colorInfo("Usuario encontrado: " + u.username));
                    } else {
                        logger.warn(AppLogger.colorWarning("Usuario no encontrado: " + username));
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

    @GET
    @Path("/simulate-prediction")
    @RolesAllowed("admin")
    public String simulatePrediction(){
        return authFacade.callPrediction();
    }

    @GET
    @Path("/validate-user")
    @RolesAllowed("admin")
    public String validateUser() {
        User user = new User();
        user.setUsername("verdugox123");
        user.setRole("admin");

        boolean validByRole = authFacade.validateUserByRole(user);
        boolean validByUsername = authFacade.validateUserByUsername(user);

        return "Validación por Rol: " + validByRole + ", Validación por Username: " + validByUsername;
    }

    @GET
    @Path("/simulate-circuit-breaker")
    @RolesAllowed("admin")
    public String simulateCircuitBreaker() {
        return authFacade.simulatePredictionWithCircuitBreaker();
    }



}
