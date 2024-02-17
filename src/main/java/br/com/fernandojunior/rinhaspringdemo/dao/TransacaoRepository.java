package br.com.fernandojunior.rinhaspringdemo.dao;

import br.com.fernandojunior.rinhaspringdemo.model.Cliente;
import br.com.fernandojunior.rinhaspringdemo.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    Page<Transacao> findByCliente(Cliente cliente, PageRequest dataLancamento);

    @org.springframework.data.jpa.repository.Query(
            value = "SELECT c, t FROM  Cliente c LEFT JOIN c.transacoes t" +
                    " WHERE c.id = :id" +
                    " order by t.dataLancamento desc limit 10 "
    )
    public List<Object[]> findUltimasTransacoes(Long id);


}

