package com.joaopaulo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.joaopaulo.model.Usuario;

@Repository // acessa o banco de dados
@Transactional
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

	@Query("select u from Usuario u where u.login = ?1") //consulta no banco de dados
	Usuario findUserByLogin(String login);
}
