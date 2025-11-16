package br.gov.caixa.api.investimentos.helper.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Set;

import br.gov.caixa.api.investimentos.exception.auth.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("JwtAuthorizationHelper - Testes unitários para autorização JWT")
class JwtAuthorizationHelperTest {

    private JwtAuthorizationHelper jwtHelper;

    @Mock
    JsonWebToken mockJwt;

    private static final Long CLIENTE_ID_TESTE = 123L;
    private static final Long USER_ID_JWT = 123L;
    private static final Long USER_ID_DIFERENTE = 456L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtHelper = new JwtAuthorizationHelper();
    }

    @Test
    @DisplayName("Deve permitir acesso para ADMIN a qualquer cliente")
    void devePermitirAcessoAdminParaQualquerCliente() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("ADMIN"));

        // When & Then
        assertDoesNotThrow(() -> 
            jwtHelper.validarAcessoAoCliente(mockJwt, CLIENTE_ID_TESTE)
        );
    }

    @Test
    @DisplayName("Deve permitir acesso para USER aos seus próprios dados")
    void devePermitirAcessoUserParaSeusPropiosDados() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("USER"));
        when(mockJwt.getClaim("userId")).thenReturn(USER_ID_JWT);

        // When & Then
        assertDoesNotThrow(() -> 
            jwtHelper.validarAcessoAoCliente(mockJwt, CLIENTE_ID_TESTE)
        );
    }

    @Test
    @DisplayName("Deve negar acesso para USER tentando acessar dados de outro cliente")
    void deveNegarAcessoUserParaDadosDeOutroCliente() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("USER"));
        when(mockJwt.getClaim("userId")).thenReturn(USER_ID_DIFERENTE);

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> 
            jwtHelper.validarAcessoAoCliente(mockJwt, CLIENTE_ID_TESTE)
        );
        
        assertEquals("Acesso negado: usuário só pode acessar seus próprios dados", 
                     exception.getMessage());
    }

    @Test
    @DisplayName("Deve negar acesso quando JWT é null")
    void deveNegarAcessoQuandoJWTENull() {
        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> 
            jwtHelper.validarAcessoAoCliente(null, CLIENTE_ID_TESTE)
        );
        
        assertEquals("Token JWT não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve negar acesso quando USER não tem userId no JWT")
    void deveNegarAcessoQuandoUserNaoTemUserIdNoJWT() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("USER"));
        when(mockJwt.getClaim("userId")).thenReturn(null);

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> 
            jwtHelper.validarAcessoAoCliente(mockJwt, CLIENTE_ID_TESTE)
        );
        
        assertEquals("Acesso negado: usuário só pode acessar seus próprios dados", 
                     exception.getMessage());
    }

    @Test
    @DisplayName("Deve negar acesso para role não autorizada")
    void deveNegarAcessoParaRoleNaoAutorizada() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("GUEST", "OTHER"));

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> 
            jwtHelper.validarAcessoAoCliente(mockJwt, CLIENTE_ID_TESTE)
        );
        
        assertEquals("Acesso negado: role não autorizada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve negar acesso quando não tem roles")
    void deveNegarAcessoQuandoNaoTemRoles() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of());

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> 
            jwtHelper.validarAcessoAoCliente(mockJwt, CLIENTE_ID_TESTE)
        );
        
        assertEquals("Acesso negado: role não autorizada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve identificar corretamente se usuário é ADMIN")
    void deveIdentificarCorretamenteSeUsuarioEAdmin() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("ADMIN"));

        // When
        boolean isAdmin = jwtHelper.isAdmin(mockJwt);

        // Then
        assertTrue(isAdmin);
    }

    @Test
    @DisplayName("Deve identificar corretamente que usuário não é ADMIN")
    void deveIdentificarCorretamenteQueUsuarioNaoEAdmin() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("USER"));

        // When
        boolean isAdmin = jwtHelper.isAdmin(mockJwt);

        // Then
        assertFalse(isAdmin);
    }

    @Test
    @DisplayName("Deve retornar false para isAdmin quando JWT é null")
    void deveRetornarFalseParaIsAdminQuandoJWTENull() {
        // When
        boolean isAdmin = jwtHelper.isAdmin(null);

        // Then
        assertFalse(isAdmin);
    }

    @Test
    @DisplayName("Deve identificar corretamente se usuário é USER")
    void deveIdentificarCorretamenteSeUsuarioEUser() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("USER"));

        // When
        boolean isUser = jwtHelper.isUser(mockJwt);

        // Then
        assertTrue(isUser);
    }

    @Test
    @DisplayName("Deve identificar corretamente que usuário não é USER")
    void deveIdentificarCorretamenteQueUsuarioNaoEUser() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("ADMIN"));

        // When
        boolean isUser = jwtHelper.isUser(mockJwt);

        // Then
        assertFalse(isUser);
    }

    @Test
    @DisplayName("Deve retornar false para isUser quando JWT é null")
    void deveRetornarFalseParaIsUserQuandoJWTENull() {
        // When
        boolean isUser = jwtHelper.isUser(null);

        // Then
        assertFalse(isUser);
    }

    @Test
    @DisplayName("Deve obter userId corretamente do JWT")
    void deveObterUserIdCorretamenteDoJWT() {
        // Given
        when(mockJwt.getClaim("userId")).thenReturn(USER_ID_JWT);

        // When
        Long userId = jwtHelper.getUserId(mockJwt);

        // Then
        assertEquals(USER_ID_JWT, userId);
    }

    @Test
    @DisplayName("Deve retornar null para getUserId quando JWT é null")
    void deveRetornarNullParaGetUserIdQuandoJWTENull() {
        // When
        Long userId = jwtHelper.getUserId(null);

        // Then
        assertNull(userId);
    }

    @Test
    @DisplayName("Deve retornar null quando userId não está presente no JWT")
    void deveRetornarNullQuandoUserIdNaoEstaPresenteNoJWT() {
        // Given
        when(mockJwt.getClaim("userId")).thenReturn(null);

        // When
        Long userId = jwtHelper.getUserId(mockJwt);

        // Then
        assertNull(userId);
    }

    @Test
    @DisplayName("Deve permitir acesso para usuário com múltiplas roles incluindo ADMIN")
    void devePermitirAcessoParaUsuarioComMultiplasRolesIncluindoAdmin() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("USER", "ADMIN", "MANAGER"));

        // When & Then
        assertDoesNotThrow(() -> 
            jwtHelper.validarAcessoAoCliente(mockJwt, CLIENTE_ID_TESTE)
        );
    }

    @Test
    @DisplayName("Deve permitir acesso para usuário com múltiplas roles incluindo USER")
    void devePermitirAcessoParaUsuarioComMultiplasRolesIncluindoUser() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("USER", "GUEST"));
        when(mockJwt.getClaim("userId")).thenReturn(USER_ID_JWT);

        // When & Then
        assertDoesNotThrow(() -> 
            jwtHelper.validarAcessoAoCliente(mockJwt, CLIENTE_ID_TESTE)
        );
    }

    @Test
    @DisplayName("Deve identificar ADMIN corretamente quando tem múltiplas roles")
    void deveIdentificarAdminCorretamenteQuandoTemMultiplasRoles() {
        // Given
        when(mockJwt.getGroups()).thenReturn(Set.of("USER", "ADMIN", "MANAGER"));

        // When
        boolean isAdmin = jwtHelper.isAdmin(mockJwt);
        boolean isUser = jwtHelper.isUser(mockJwt);

        // Then
        assertTrue(isAdmin);
        assertTrue(isUser); // Deve ser true também pois tem role USER
    }
}