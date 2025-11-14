package org.example.model.cliente;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

/**
 * Entidade JPA para representar uma Pessoa no sistema
 * Utilizada para clientes e administradores
 */
@Entity
@Table(name = "pessoa")
public class Pessoa extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    public Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    public String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    public String cpf;

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    public String username;

    @NotBlank(message = "Password é obrigatório")
    @Size(min = 6, message = "Password deve ter no mínimo 6 caracteres")
    @Column(name = "password", nullable = false)
    public String password;

    @NotBlank(message = "Role é obrigatório")
    @Pattern(regexp = "cliente|admin|USER|ADMIN", message = "Role deve ser 'cliente', 'admin', 'USER' ou 'ADMIN'")
    @Column(name = "role", nullable = false, length = 10)
    public String role;

    /**
     * Construtor padrão
     */
    public Pessoa() {}

    /**
     * Construtor completo
     */
    public Pessoa(String nome, String cpf, String username, String password, String role) {
        this.nome = nome;
        this.cpf = cpf;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Busca pessoa por CPF
     */
    public static Pessoa findByCpf(String cpf) {
        return find("cpf", cpf).firstResult();
    }

    /**
     * Busca pessoa por username
     */
    public static Pessoa findByUsername(String username) {
        return find("username", username).firstResult();
    }

    /**
     * Verifica se CPF já existe
     */
    public static boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }

    /**
     * Verifica se username já existe
     */
    public static boolean existsByUsername(String username) {
        return count("username", username) > 0;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
