package com.datum.redsoft.controller;

import com.datum.redsoft.dto.request.CompanyCreateRequest;
import com.datum.redsoft.dto.request.CompanyUpdateRequest;
import com.datum.redsoft.dto.response.CompanyDTO;
import com.datum.redsoft.service.CompanyService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestionar empresas
 */
@Path("/api/companies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyController {

    @Inject
    CompanyService companyService;

    /**
     * Obtiene todas las empresas
     */
    @GET
    public Response getAllCompanies() {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        return Response.ok(companies).build();
    }

    /**
     * Obtiene una empresa por ID
     */
    @GET
    @Path("/{id}")
    public Response getCompanyById(@PathParam("id") Long id) {
        Optional<CompanyDTO> company = companyService.getCompanyById(id);
        
        if (company.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Empresa no encontrada con ID: " + id)
                    .build();
        }
        
        return Response.ok(company.get()).build();
    }

    /**
     * Obtiene empresas por país
     */
    @GET
    @Path("/by-country/{countryId}")
    public Response getCompaniesByCountryId(@PathParam("countryId") Long countryId) {
        List<CompanyDTO> companies = companyService.getCompaniesByCountryId(countryId);
        return Response.ok(companies).build();
    }

    /**
     * Busca empresas por nombre
     */
    @GET
    @Path("/search")
    public Response searchCompaniesByName(@QueryParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El parámetro 'name' es requerido")
                    .build();
        }
        
        List<CompanyDTO> companies = companyService.searchCompaniesByName(name);
        return Response.ok(companies).build();
    }

    /**
     * Crea una nueva empresa
     */
    @POST
    public Response createCompany(@Valid CompanyCreateRequest request) {
        try {
            CompanyDTO company = companyService.createCompany(request);
            return Response.status(Response.Status.CREATED)
                    .entity(company)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Actualiza una empresa existente
     */
    @PUT
    @Path("/{id}")
    public Response updateCompany(@PathParam("id") Long id, @Valid CompanyUpdateRequest request) {
        try {
            Optional<CompanyDTO> updatedCompany = companyService.updateCompany(id, request);
            
            if (updatedCompany.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Empresa no encontrada con ID: " + id)
                        .build();
            }
            
            return Response.ok(updatedCompany.get()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Elimina una empresa
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCompany(@PathParam("id") Long id) {
        boolean deleted = companyService.deleteCompany(id);
        
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Empresa no encontrada con ID: " + id)
                    .build();
        }
        
        return Response.noContent().build();
    }
}