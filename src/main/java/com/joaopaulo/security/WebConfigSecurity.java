package com.joaopaulo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // classe de configuração
@EnableWebSecurity // classe de segurança
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@Override //solicita as solicitações de acesso por HTTP
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
			.disable() // desablita as configurções padrões de memória
			.authorizeRequests() // permiti restringir acessos
			.antMatchers(HttpMethod.GET, "/").permitAll() // permite inicialmente qq usuário
			.antMatchers(HttpMethod.GET, "/cadastropessoa").hasAnyRole("ADMIN")
			.anyRequest().authenticated()
			.and().formLogin().permitAll() // permite todos usuários a tela de login
			.loginPage("/login")
			.defaultSuccessUrl("/cadastropessoa")
			.failureUrl("/login?error=true")
			.and().logout().logoutSuccessUrl("/login") // mapeia a URL de logout e invalida usuário autenticado
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout")); // invalida sessão
	}
	
	@Override // cria autenticação do usuário com banco de dados ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(implementacaoUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
		
//		auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder()) //NoOpPasswordEncoder.getInstance()
//			.withUser("joao")
//			.password("$2a$10$fS0cIL2FxiYhas.BC5O0yeyLurk71vVlhuAHfmaleZlgYfN5ajUu6")
//			.roles("ADMIN");
	}
	
	@Override // iguinora url específicas (CSS, JavaScript...)
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/materialize/**");
	}
}
