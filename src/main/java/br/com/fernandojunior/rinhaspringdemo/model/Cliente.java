package br.com.fernandojunior.rinhaspringdemo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
public class Cliente {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name="limite")
        private long limite;

        @Column(name="saldo")
        private long saldo;

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "cliente")
        private List<Transacao> transacoes;

        public void registraTransacao(Transacao transacao) {

        }
}
