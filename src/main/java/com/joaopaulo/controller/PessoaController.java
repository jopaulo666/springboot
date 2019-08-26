package com.joaopaulo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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

import net.sf.jasperreports.engine.JRException;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil reportUtil; 
	
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
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {
		//carrega os telefones
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		//faz a validaçao
		if (bindingResult.hasErrors()) {
			ModelAndView andView = new ModelAndView("cadastro/cadastropessoa"); // retorna pre mesma tela
			Iterable<Pessoa> pessoasIt = pessoaRepository.findAll(); // consulta todos
			andView.addObject("pessoas", pessoasIt); // passa a lista de pessoas
			andView.addObject("pessoaobj", pessoa); //retorna a mesma pessoa
			
			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) { // pega todos os erros
				msg.add(objectError.getDefaultMessage()); //pega a msg das anotações do model
			}
			andView.addObject("msg", msg);
			return andView;
		}
		
		pessoaRepository.save(pessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa"); // retorna pre mesma tela
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll(); // consulta todos
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoaobj", new Pessoa()); //nova pessoa
		
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
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo) {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		if (pesqsexo != null && !pesqsexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
		} else {
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		}
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoas);
		andView.addObject("pessoaobj", new Pessoa());
		return andView;		
	}
	
	@GetMapping("**/pesquisarpessoa") // não tem retorno
	public void ImprimePDF(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		if (pesqsexo != null && !pesqsexo.isEmpty() && nomepesquisa != null && !nomepesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNameSexo(pesqsexo, nomepesquisa);
		} else if (nomepesquisa != null && !nomepesquisa.isEmpty()){
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		} else if (pesqsexo != null && !pesqsexo.isEmpty()){
			pessoas = pessoaRepository.findPessoaBySexo(pesqsexo);
		}
		else {
			Iterable<Pessoa> iterator = pessoaRepository.findAll();
			for (Pessoa pessoa : iterator) {
				pessoas.add(pessoa);
			}
		}
		//chama o serviço que faz a geração do relarório
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		//tamanho da resposta
		response.setContentLength(pdf.length);
		//definir na resposta o tipo de caminho
		response.setContentType("application/octet-stream");
		//definir cabeçalho da resposta
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		//finaliza a resposta pro navegador
		response.getOutputStream().write(pdf);
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
		
		if (telefone != null && telefone.getCelular().isEmpty() || telefone.getEmail().isEmpty()) {
			ModelAndView andView = new ModelAndView("cadastro/telefones");
			andView.addObject("pessoaobj", pessoa); // objeto pai
			andView.addObject("telefones", telefoneRepository.getTelefones(pessoaid)); //objeto filho
			
			List<String> msg = new ArrayList<String>();
			if (telefone.getCelular().isEmpty()) {
				msg.add("Informe um número de celular");				
			}
			if (telefone.getEmail().isEmpty()) {
				msg.add("Informe um endereço de e-mail");				
			}
			andView.addObject("msg", msg);
			
			return andView; // para a execução do código
		}
		
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		
		telefone.setPessoa(pessoa); //pega o telefone e coloca na pessoa
		
		telefoneRepository.save(telefone); // salva
		
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
