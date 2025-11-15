package br.gov.caixa.api.investimentos.exception.handler;

import br.gov.caixa.api.investimentos.dto.common.ErrorResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebApplicationExceptionHandlerTest {

    private WebApplicationExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new WebApplicationExceptionHandler();
    }

    @Test
    void toResponse_badRequest_jsonParsingError() {
        WebApplicationException ex = mock(WebApplicationException.class);
        Response response = Response.status(Response.Status.BAD_REQUEST).build();
        when(ex.getResponse()).thenReturn(response);
        when(ex.getMessage()).thenReturn("Malformed JSON request");

        Response result = handler.toResponse(ex);
        ErrorResponse entity = (ErrorResponse) result.getEntity();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus());
        assertNotNull(entity);
        assertTrue(entity.message().toLowerCase().contains("json"));
    }

    @Test
    void toResponse_unauthorized() {
        WebApplicationException ex = mock(WebApplicationException.class);
        Response response = Response.status(Response.Status.UNAUTHORIZED).build();
        when(ex.getResponse()).thenReturn(response);
        when(ex.getMessage()).thenReturn("Unauthorized access");

        Response result = handler.toResponse(ex);
        ErrorResponse entity = (ErrorResponse) result.getEntity();

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
        assertNotNull(entity);
        assertTrue(entity.message().toLowerCase().contains("login"));
    }

    @Test
    void toResponse_forbidden() {
        WebApplicationException ex = mock(WebApplicationException.class);
        Response response = Response.status(Response.Status.FORBIDDEN).build();
        when(ex.getResponse()).thenReturn(response);
        when(ex.getMessage()).thenReturn("Forbidden");

        Response result = handler.toResponse(ex);
        ErrorResponse entity = (ErrorResponse) result.getEntity();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), result.getStatus());
        assertNotNull(entity);
        assertTrue(entity.message().toLowerCase().contains("perm"));
    }

    @Test
    void toResponse_otherStatus() {
        WebApplicationException ex = mock(WebApplicationException.class);
        Response response = Response.status(Response.Status.NOT_FOUND).build();
        when(ex.getResponse()).thenReturn(response);
        when(ex.getMessage()).thenReturn("Not Found");

        Response result = handler.toResponse(ex);
        ErrorResponse entity = (ErrorResponse) result.getEntity();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getStatus());
        assertNotNull(entity);
        assertTrue(entity.message().toLowerCase().contains("erro"));
    }

    /*
    @Test
    void isJsonParsingError_detectsJsonError() throws Exception {
        WebApplicationException ex = mock(WebApplicationException.class);
        when(ex.getMessage()).thenReturn("Unexpected end-of-input in JSON");
        when(ex.getCause()).thenReturn(null);

        assertTrue(handler.toResponse(ex).getEntity() instanceof ErrorResponse);
    }
    */
}