package org.example.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.example.dto.InvestimentoRequest;
import org.example.enums.Indice;
import org.example.enums.PeriodoRentabilidade;
import org.example.enums.TipoProduto;
import org.example.enums.TipoRentabilidade;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa um investimento efetivado
 */
@Entity
@Table(name = "investimento")
public class Investimento extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INTEGER")
    public Long id;

    @NotNull
    @Column(name = "cliente_id", nullable = false)
    public Long clienteId;

    @NotNull
    @Column(name = "produto_id", nullable = false)
    public Long produtoId;

    @NotNull
    @DecimalMin(value = "1.00")
    @DecimalMax(value = "999999999.99")
    @Column(name = "valor", precision = 19, scale = 2, nullable = false)
    public BigDecimal valor;

    @Column(name = "prazo_meses")
    public Integer prazoMeses;

    @Column(name = "prazo_dias")
    public Integer prazoDias;

    @Column(name = "prazo_anos")
    public Integer prazoAnos;

    @NotNull
    @Column(name = "data", nullable = false)
    public LocalDate data;

    // Campos copiados do Produto para snapshot
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    public TipoProduto tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_rentabilidade", nullable = false)
    public TipoRentabilidade tipoRentabilidade;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "rentabilidade", precision = 10, scale = 4, nullable = false)
    public BigDecimal rentabilidade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_rentabilidade", nullable = false)
    public PeriodoRentabilidade periodoRentabilidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "indice")
    public Indice indice;

    @NotNull
    @Min(-1)
    @Column(name = "liquidez", nullable = false)
    public Integer liquidez;

    @NotNull
    @Min(0)
    @Column(name = "minimo_dias_investimento", nullable = false)
    public Integer minimoDiasInvestimento;

    @NotNull
    @Column(name = "fgc", nullable = false)
    public Boolean fgc;

    public Investimento() {}

    public static Investimento from(InvestimentoRequest request, Produto produto) {
        Investimento inv = new Investimento();
        inv.clienteId = request.clienteId();
        inv.produtoId = request.produtoId();
        inv.valor = request.valor();
        inv.prazoMeses = request.prazoMeses();
        inv.prazoDias = request.prazoDias();
        inv.prazoAnos = request.prazoAnos();
        inv.data = request.data();
        inv.tipo = produto.getTipo();
        inv.tipoRentabilidade = produto.getTipoRentabilidade();
        inv.rentabilidade = produto.getRentabilidade();
        inv.periodoRentabilidade = produto.getPeriodoRentabilidade();
        inv.indice = produto.getIndice();
        inv.liquidez = produto.getLiquidez();
        inv.minimoDiasInvestimento = produto.getMinimoDiasInvestimento();
        inv.fgc = produto.getFgc();
        return inv;
    }
}
