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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    //private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    private ClienteRepository repository;

    private TransacaoRepository transacaoRepository;

    ClienteService(ClienteRepository repository, TransacaoRepository transacaoRepository){
        this.repository = repository;
        this.transacaoRepository = transacaoRepository;
    }



    @Transactional()
    public void registraTransacao(Transacao transacao, long idCliente) throws SaldoInsuficienteException, ClienteNaoEncontradoException {

        //Cliente cliente = repository.findByIdLocked(idCliente).orElseThrow(ClienteNaoEncontradoException::new);
        Cliente cliente = repository.findById(idCliente).orElseThrow(ClienteNaoEncontradoException::new);
        transacao.setCliente(cliente);
        transacao.setDataLancamento(LocalDateTime.now(ZoneOffset.UTC));



        long valorTransacao = transacao.getValor().longValue();
        if (transacao.getTipo() == 'd'){
            valorTransacao = valorTransacao * -1;
        }

        if (Math.abs(cliente.getSaldo()+valorTransacao) < cliente.getLimite()*-1) {
                throw new SaldoInsuficienteException();
        }

        repository.update(idCliente, valorTransacao);
        transacaoRepository.save(transacao);
    }

    @Transactional
    public void registraTransacaoWithPessimisticLocking(Transacao transacao, long idCliente) throws SaldoInsuficienteException, ClienteNaoEncontradoException {

        Cliente cliente = repository.findByIdWithPessimisticLocking(idCliente).orElseThrow(() -> {
            throw new ClienteNaoEncontradoException();
        });


        long valorTransacao = transacao.getValor().longValue();
        if (transacao.getTipo() == 'd'){
            valorTransacao = valorTransacao * -1;
        }

        long newBalance = (cliente.getSaldo() + valorTransacao);
        if (newBalance < cliente.getLimite() * -1) {
            throw new SaldoInsuficienteException();
        }

        transacao.setCliente(cliente);
        transacao.setDataLancamento(LocalDateTime.now(ZoneOffset.UTC));

        cliente.setSaldo(newBalance);
        repository.save(cliente);

        transacaoRepository
                .save(transacao);
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
                        "realizada_em", transacao.getDataLancamento().format(formatter)))
                .collect(Collectors.toList());


    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now(ZoneOffset.UTC).atOffset(ZoneOffset.UTC).format(formatter));
        System.out.println(LocalDateTime.now().format(formatter));
        System.out.println(LocalDateTime.now().format(formatter));


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

            saldo.put("data_extrato", LocalDateTime.now(ZoneOffset.UTC).atOffset(ZoneOffset.UTC).format(formatter)  );

            retorno.put("saldo", saldo);

            List transacoes = new ArrayList();
            for (Object[] tupla : ultimasTransacoes) {
                Transacao transacao = (Transacao) tupla[1];
                if (transacao != null){
                    transacoes.add(Map.of(
                            "valor", transacao.getValor(),
                            "tipo", transacao.getTipo(),
                            "descricao", transacao.getDescricao(),
                            "realizada_em", transacao.getDataLancamento().atOffset(ZoneOffset.UTC).format(formatter)));
                }
            }
            retorno.put("ultimas_transacoes", transacoes);

            return Optional.of(retorno);
        }
    }
}
