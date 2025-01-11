package com.Transactions.Transactions.Repository;

import com.Transactions.Transactions.Model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByAccountIdOrderByDateDesc(Long accountId);
    List<Activity> findByAccountIdAndTypeOrderByDateDesc(Long accountId, String type);

    // Buscar la última actividad registrada asociada a un CVU destino
    Activity findFirstByDestinationCbuOrderByDateDesc(String destinationCbu);
}
