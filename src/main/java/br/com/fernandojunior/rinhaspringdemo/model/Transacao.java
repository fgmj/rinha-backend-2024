package br.com.fernandojunior.rinhaspringdemo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 0)
    private BigDecimal valor;

    @Column(length = 1)
    @NotNull
    private char tipo;

    @Column(length = 10)
    @Size(min=1, max=10)
    @Pattern(regexp = "^[A-Za-z0-9]+$")
    @NotNull
    private String descricao;

    @Column()
    private LocalDateTime dataLancamento;

    @ManyToOne()
    @JoinColumn(name="cliente_id", nullable=false)
    private Cliente cliente;
}
