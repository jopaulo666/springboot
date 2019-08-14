package com.joaopaulo.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // classe de configuração
@EnableWebSecurity // classe de segurança
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

	@Override //solicita as solicitações de acesso por HTTP
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
			.disable() // desablita as configurções padrões de memória
			.authorizeRequests() // permiti restringir acessos
			.antMatchers(HttpMethod.GET, "/").permitAll() // permite inicialmente qq usuário
			.anyRequest().authenticated()
			.and().formLogin().permitAll() // permite todos usuários a tela de login
			.and().logout() // mapeia a URL de logout e invalida usuário autenticado
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout")); // invalida sessão
	}
	
	@Override // cria autenticação do usuário com banco de dados ou em meméria
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
			.withUser("joao")
			.password("123")
			.roles("admin");
	}
	
	@Override // iguinora url específicas (CSS, JavaScript...)
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/materialize/**");
	}
}
