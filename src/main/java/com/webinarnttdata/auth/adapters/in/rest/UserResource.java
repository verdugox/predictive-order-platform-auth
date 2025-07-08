package com.webinarnttdata.auth.adapters.in.rest;

import com.webinarnttdata.auth.application.UserService;
import com.webinarnttdata.auth.domain.User;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.awt.*;

@Path("api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    @Path("/register")
    public Uni<Response> register(User user) {
        return userService.register(user)
                .onItem().transform(u -> Response.ok(u).build());
    }

    @GET
    @Path("/{username}")
    public Uni<Response> getByUsername(User user){
        return userService.register(user)
                .onItem().ifNotNull().transform(u -> Response.ok(u).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND)::build);
    }

}
