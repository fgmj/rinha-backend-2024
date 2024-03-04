package br.com.fernandojunior.rinhaspringdemo.controller;

import br.com.fernandojunior.rinhaspringdemo.dao.ClienteRepository;
import br.com.fernandojunior.rinhaspringdemo.exception.ClienteNaoEncontradoException;
import br.com.fernandojunior.rinhaspringdemo.exception.SaldoInsuficienteException;
import br.com.fernandojunior.rinhaspringdemo.model.Transacao;
import br.com.fernandojunior.rinhaspringdemo.service.ClienteService;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;
    private final ClienteRepository repository;

    Logger logger = Logger.getLogger(ClienteController.class.getName());

    ClienteController(ClienteRepository repository, ClienteService service) {
        this.repository = repository;
        this.service = service;
    }


    @GetMapping
    public List findAll() {
        return repository.findAll();
    }

    @GetMapping(path = {"/{id}/extrato"})
    public ResponseEntity findById(@PathVariable long id) {


        Optional<HashMap<String, Object>> clienteEUltimasTransacoes = service.findClienteEUltimasTransacoes(id);

        if (clienteEUltimasTransacoes.isPresent()) {
            return ResponseEntity.ok().body(clienteEUltimasTransacoes.get());
        } else {
            return ResponseEntity.notFound().build();
        }



        /*
        Map<String, Object> retorno = new HashMap<String, Object>();

        Optional<Cliente> c = repository.findById(id);

        if (c.isPresent()) {
            Cliente cliente = c.get();
            //TODO Colocar numa só consulta
            retorno.put("ultimas_transacoes", service.findUltimasTrasacoes(cliente));

            Map<String, Object> saldo = new HashMap<String, Object>();
            saldo.put("total", cliente.getSaldo());
            saldo.put("limite", cliente.getLimite());
            saldo.put("data_extrato", LocalDateTime.now());

            retorno.put("saldo", saldo);


            return ResponseEntity.ok().body(retorno);

        } else {
            return ResponseEntity.notFound().build();
        }
        */

    }

    //POST /clientes/[id]/transacoes

    @PostMapping(path = {"/{id}/transacoes"})
    public ResponseEntity create(@PathVariable long id, @Validated @RequestBody Transacao transacao) {

        /*
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Transacao>> validate = validator.validate(transacao);
        if (!validate.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

         */

        try {
            if (transacao.getTipo() != 'c' && transacao.getTipo() != 'd') {
                return ResponseEntity.unprocessableEntity().build();
            }

            service.registraTransacaoWithPessimisticLocking(transacao, id);

        } catch (SaldoInsuficienteException e) {
            logger.info("saldo insuficiente.");
            return ResponseEntity.unprocessableEntity().build();
        } catch (ClienteNaoEncontradoException e) {
            logger.info("cliente nao encontrado.");
            return ResponseEntity.notFound().build();
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("limite", transacao.getCliente().getLimite());
        map.put("saldo", transacao.getCliente().getSaldo());
        return ResponseEntity.ok().body(map);

    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(MethodArgumentNotValidException exception) {
        String errorMsg = exception.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(exception.getMessage());
        return ErrorResponse.builder(exception, HttpStatus.UNPROCESSABLE_ENTITY, errorMsg).build();

    }

}
