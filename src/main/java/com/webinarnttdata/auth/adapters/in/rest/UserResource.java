package com.webinarnttdata.auth.adapters.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webinarnttdata.auth.adapters.out.kafka.AuthEventProducer;
import com.webinarnttdata.auth.application.services.AuthFacade;
import com.webinarnttdata.auth.application.services.UserService;
import com.webinarnttdata.auth.domain.User;
import com.webinarnttdata.auth.domain.patterns.AppLogger;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

@Path("api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    // Obtienes el Logger Singleton
    private static final Logger logger = AppLogger.getInstance();

    @Inject
    Tracer tracer;

    @Inject
    UserService userService;
    // Inyectamos SecurityIdentity para ver los roles que Quarkus detecta
    @Inject
    SecurityIdentity identity;
    @Inject
    AuthFacade authFacade;
    @Inject
    AuthEventProducer producer;
    @Inject
    ReactiveRedisDataSource reactiveRedisDS;
    ReactiveValueCommands<String, String> redis;

    @PostConstruct
    void init() {
        redis = reactiveRedisDS.value(String.class);
    }

    public void someMethod() {
        Span span = tracer.spanBuilder("getUserFromDB").startSpan();
        try {
            // tu lógica
        } finally {
            span.end();
        }
    }

    @POST
    @Path("/register")
    @Counted(value = "users.register.count", description = "Número de usuarios registrados")
    @Timed(value = "users.register.timer", description = "Tiempo en registrar un usuario")
    public Uni<Response> register(User user) {
        logger.info(AppLogger.colorSuccess("Llamando al endpoint /register con usuario: " + user.username));
        return userService.register(user)
                .onItem().invoke(u -> logger.info(AppLogger.colorSuccess("Usuario registrado exitosamente: " + u.username)))
                .onFailure().invoke(e -> logger.error(AppLogger.colorError("Error al registrar usuario"), e))
                .onItem().transform(u -> Response.ok(u).build());
    }

    @GET
    @Path("/{username}")
    @Counted(value = "users.find.count", description = "Número de veces que se buscó un usuario")
    @Timed(value = "users.find.timer", description = "Tiempo en buscar un usuario por username")
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

    @GET
    @Path("/send-event")
    @RolesAllowed("admin")
    public String sendEventKafka(@QueryParam("msg")String message) {
        if (message == null || message.isEmpty()) {
            return "Mensaje no proporcionado";
        }
        producer.sendEvent(message);
        logger.info(AppLogger.colorSuccess("Evento enviado a Kafka: " + message));
        return "Evento enviado a  kafka correctamente: " + message;
    }

    @GET
    @Path("/redis/{id}")
    public Uni<Response> getUser(@PathParam("id") String id) {
        logger.info(AppLogger.colorInfo("Consultando usuario con ID: " + id));

        String cacheKey = "user:" + id;

        // 1. Intentar desde Redis
        return redis.get(cacheKey)
                .onItem().ifNotNull().transform(cachedJson -> {
                    logger.info(AppLogger.colorSuccess("Usuario encontrado en cache Redis"));
                    return Response.ok(cachedJson).build();
                })
                .onItem().ifNull().switchTo(() -> {
                    // 2. Si no está en cache, buscar en MongoDB
                    return User.findById(new ObjectId(id))
                            .onItem().ifNotNull().transformToUni(user -> {
                                try {
                                    String jsonUser = new ObjectMapper().writeValueAsString(user);
                                    logger.info(AppLogger.colorInfo("Usuario encontrado en MongoDB, cacheando en Redis..."));
                                    return redis.set(cacheKey, jsonUser)
                                            .replaceWith(Response.ok(jsonUser).build());
                                } catch (Exception e) {
                                    logger.error(AppLogger.colorError("Error serializando User"), e);
                                    return Uni.createFrom().item(Response.ok(user).build());
                                }
                            })
                            .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
                });
    }


}
