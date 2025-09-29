package br.gov.es.siscap.repository;

import br.gov.es.siscap.models.TokenAc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenAcRepository extends JpaRepository<TokenAc, String> {

}