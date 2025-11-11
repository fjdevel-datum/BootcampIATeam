package com.datum.redsoft.controller;

import com.datum.redsoft.dto.request.CountryCreateRequest;
import com.datum.redsoft.dto.request.CountryUpdateRequest;
import com.datum.redsoft.dto.response.CountryDTO;
import com.datum.redsoft.service.CountryService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestionar países
 */
@Path("/api/countries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CountryController {

    @Inject
    CountryService countryService;

    /**
     * Obtiene todos los países
     */
    @GET
    public Response getAllCountries() {
        List<CountryDTO> countries = countryService.getAllCountries();
        return Response.ok(countries).build();
    }

    /**
     * Obtiene un país por ID
     */
    @GET
    @Path("/{id}")
    public Response getCountryById(@PathParam("id") Long id) {
        Optional<CountryDTO> country = countryService.getCountryById(id);
        
        if (country.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("País no encontrado con ID: " + id)
                    .build();
        }
        
        return Response.ok(country.get()).build();
    }

    /**
     * Obtiene un país por código ISO
     */
    @GET
    @Path("/iso/{isoCode}")
    public Response getCountryByIsoCode(@PathParam("isoCode") String isoCode) {
        Optional<CountryDTO> country = countryService.getCountryByIsoCode(isoCode);
        
        if (country.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("País no encontrado con código ISO: " + isoCode)
                    .build();
        }
        
        return Response.ok(country.get()).build();
    }

    /**
     * Busca países por nombre
     */
    @GET
    @Path("/search")
    public Response searchCountriesByName(@QueryParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El parámetro 'name' es requerido")
                    .build();
        }
        
        List<CountryDTO> countries = countryService.searchCountriesByName(name);
        return Response.ok(countries).build();
    }

    /**
     * Crea un nuevo país
     */
    @POST
    public Response createCountry(@Valid CountryCreateRequest request) {
        try {
            CountryDTO country = countryService.createCountry(request);
            return Response.status(Response.Status.CREATED)
                    .entity(country)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Actualiza un país existente
     */
    @PUT
    @Path("/{id}")
    public Response updateCountry(@PathParam("id") Long id, @Valid CountryUpdateRequest request) {
        try {
            Optional<CountryDTO> updatedCountry = countryService.updateCountry(id, request);
            
            if (updatedCountry.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("País no encontrado con ID: " + id)
                        .build();
            }
            
            return Response.ok(updatedCountry.get()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Elimina un país
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCountry(@PathParam("id") Long id) {
        boolean deleted = countryService.deleteCountry(id);
        
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("País no encontrado con ID: " + id)
                    .build();
        }
        
        return Response.noContent().build();
    }
}