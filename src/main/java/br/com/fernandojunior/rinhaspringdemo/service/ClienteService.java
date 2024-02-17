package br.com.fernandojunior.rinhaspringdemo.service;

import br.com.fernandojunior.rinhaspringdemo.dao.ClienteRepository;
import br.com.fernandojunior.rinhaspringdemo.dao.TransacaoRepository;
import br.com.fernandojunior.rinhaspringdemo.exception.ClienteNaoEncontradoException;
import br.com.fernandojunior.rinhaspringdemo.exception.SaldoInsuficienteException;
import br.com.fernandojunior.rinhaspringdemo.model.Cliente;
import br.com.fernandojunior.rinhaspringdemo.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClienteService {


    private ClienteRepository repository;

    private TransacaoRepository transacaoRepository;

    ClienteService(ClienteRepository repository, TransacaoRepository transacaoRepository){
        this.repository = repository;
        this.transacaoRepository = transacaoRepository;
    }




    @Transactional()
    public void registraTransacao(Transacao transacao, long idCliente) throws SaldoInsuficienteException, ClienteNaoEncontradoException {

        Cliente cliente = repository.findByIdLocked(idCliente).orElseThrow(ClienteNaoEncontradoException::new);

        transacao.setCliente(cliente);

        if (transacao.getTipo() == 'c'){
            cliente.setSaldo((cliente.getSaldo() + transacao.getValor().longValue()));
        } else {
            long newBalance = cliente.getSaldo() - transacao.getValor().longValue();
            if (Math.abs(newBalance) > cliente.getLimite()) {
                throw new SaldoInsuficienteException();
            }
            cliente.setSaldo(newBalance);
        }
        transacao.setDataLancamento(LocalDateTime.now());

        repository.save(cliente);
        transacaoRepository.save(transacao);
    }

    @Transactional
    public List<?> findUltimasTrasacoes(Cliente c) {

        Page<Transacao> pageLancamentos =
                transacaoRepository.
                findByCliente(c,
                        PageRequest.of(0, 10, Sort.by(Sort.Order.desc("dataLancamento"))));

        List<Transacao>  lancamentos = pageLancamentos.hasContent() ? pageLancamentos.getContent() : Collections.emptyList();

        return lancamentos.stream()
                .map(transacao ->  Map.of(
                        "valor", transacao.getValor(),
                        "tipo", transacao.getTipo(),
                        "descricao", transacao.getDescricao(),
                        "realizada_em", transacao.getDataLancamento()))
                .collect(Collectors.toList());


    }

    public Optional<HashMap<String, Object>> findClienteEUltimasTransacoes(Long id){
        List<Object[]> ultimasTransacoes = transacaoRepository.findUltimasTransacoes(id);
        if (ultimasTransacoes.isEmpty()) {
            return Optional.empty();
        }else {
            Cliente cliente = (Cliente) ultimasTransacoes.get(0)[0];
            HashMap<String,Object> retorno = new HashMap<>();

            Map<String, Object> saldo = new HashMap<String, Object>();
            saldo.put("total", cliente.getSaldo());
            saldo.put("limite", cliente.getLimite());
            saldo.put("data_extrato", LocalDateTime.now());

            retorno.put("saldo", saldo);

            List transacoes = new ArrayList();
            for (Object[] tupla : ultimasTransacoes) {
                Transacao transacao = (Transacao) tupla[1];
                if (transacao != null){
                    transacoes.add(Map.of(
                            "valor", transacao.getValor(),
                            "tipo", transacao.getTipo(),
                            "descricao", transacao.getDescricao(),
                            "realizada_em", transacao.getDataLancamento()));
                }
            }
            retorno.put("ultimas_transacoes", transacoes);

            return Optional.of(retorno);
        }
    }
}
