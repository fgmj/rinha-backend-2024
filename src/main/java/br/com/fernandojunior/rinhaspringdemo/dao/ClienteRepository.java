package br.com.fernandojunior.rinhaspringdemo.dao;

import br.com.fernandojunior.rinhaspringdemo.model.Cliente;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {


    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cliente c WHERE c.id = :id")
    Optional<Cliente> findByIdWithPessimisticLocking(Long id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = """
                   update cliente
                      set saldo = (saldo + :valorTransacao)
                    where id = :accountId
                   """
    )

    public int update(Long accountId, Long valorTransacao);

}

