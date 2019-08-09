package com.joaopaulo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.joaopaulo.model.Pessoa;
import com.joaopaulo.model.Telefone;
import com.joaopaulo.repository.PessoaRepository;
import com.joaopaulo.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoaobj", new Pessoa());
		//carerga pessoas
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoasIt);
		
		return andView;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(Pessoa pessoa) {
		pessoaRepository.save(pessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoaobj", new Pessoa());
		
		return andView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		//carerga pessoas
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}
	
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoaobj", pessoa.get());
		return andView;
	}
	
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		
		pessoaRepository.deleteById(idpessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll());
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa) {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findPessoaByName(nomepesquisa));
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
		
	}
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		andView.addObject("pessoaobj", pessoa.get()); // lista pessoas
		andView.addObject("telefones", telefoneRepository.getTelefones(idpessoa)); // lista contatos
		return andView;
	}
	
	@PostMapping("**/addfonepessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get(); //consulta pessoa
		telefone.setPessoa(pessoa); //pega o telefone e coloca na pessoa
		
		telefoneRepository.save(telefone); // salva
		
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		andView.addObject("pessoaobj", pessoa);
		andView.addObject("telefones", telefoneRepository.getTelefones(pessoaid)); //carrega a lista de contatos
		return andView;
	}
	
	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView removerTelefone(@PathVariable("idtelefone") Long idtelefone) {
		
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		
		telefoneRepository.deleteById(idtelefone);
		
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		andView.addObject("pessoaobj", pessoa);
		andView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		return andView;
	}

}
