package org.example.resource;

import java.net.URI;
import java.util.List;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.AtualizaStatusVeiculoRequest;
import org.example.dto.VeiculoRequest;
import org.example.dto.VeiculoResponse;
import org.example.model.VeiculoStatus;
import org.example.model.Veiculo;

@Path("/veiculos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VeiculoResource
{
    public VeiculoResource()
    {
    }

    @Transactional
    @POST
    public Response criar(VeiculoRequest dto)
    {
        try
        {
            Veiculo veiculo = new Veiculo(dto.brand(), dto.model(), dto.year(), dto.engine());
            veiculo.persist();

            return Response.created(URI.create("/veiculos/" + veiculo.getId())).entity(new VeiculoResponse(veiculo.getId())).build();
        }
        catch(IllegalArgumentException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    public List<Veiculo> listar(@DefaultValue("") @QueryParam("carTitle") String carTitle)
    {
        if (carTitle.equalsIgnoreCase(""))
        {
            return Veiculo.listAll();
        }
        else
        {
            return Veiculo.buscarPorCarTitle(carTitle);
        }
    }

    @GET
    @Path("/{id}")
    public Response get_por_id(@PathParam("id") Long id)
    {
        Veiculo veiculo = Veiculo.find("id", id).firstResult();

        if (veiculo != null)
        {
            return Response.ok(veiculo).build();
        }
        else
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Transactional
    @PATCH
    @Path("/{id}")
    public Response alterarStatus(@PathParam("id") Long id, AtualizaStatusVeiculoRequest requisicao)
    {
        Veiculo veiculo = Veiculo.find("id", id).firstResult();

        if (veiculo != null)
        {
            try
            {
                veiculo.atualizaStatus(requisicao.status());

                veiculo.persist();

                return Response.noContent().build();
            }
            catch(IllegalArgumentException e)
            {
                return Response.status(409).build();
            }
        }
        else
        {
            return Response.status(404).build();
        }
    }

    @Transactional
    @PUT
    @Path("/{id}")
    public Response atualiza(Veiculo veiculo)
    {
        Veiculo veiculoAtual = Veiculo.find("id", veiculo.getId()).firstResult();

        if (veiculoAtual != null)
        {
            try
            {
                veiculo.setId(veiculoAtual.getId());
                veiculo.persist();

                return Response.accepted().build();
            }
            catch(IllegalArgumentException e)
            {
                return Response.status(409).build();
            }
        }
        else
        {
            return Response.status(404).build();
        }
    }

    @Transactional
    @DELETE
    @Path("/{id}")
    public Response deleta(@PathParam("id") Long id)
    {
        Veiculo veiculo = Veiculo.find("id", id).firstResult();

        if (veiculo != null)
        {
            if(veiculo.getStatus().equals(VeiculoStatus.RENTED))
            {
                return Response.status(400).build();
            }
            else
            {
                veiculo.delete();

                return Response.noContent().build();
            }
        }
        else
        {
            return Response.status(404).build();
        }
    }
}