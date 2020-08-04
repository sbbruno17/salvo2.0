package com.codeoftheweb.Salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {
	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);
	}

	@Bean
	public CommandLineRunner initData(com.codeoftheweb.Salvo.PlayerRepository playerRepository, com.codeoftheweb.Salvo.GameRepository gameRepository, com.codeoftheweb.Salvo.GameplayerRepository gameplayerRepository, com.codeoftheweb.Salvo.ShipRepository shipRepository, com.codeoftheweb.Salvo.ScoreRepository scoreRepository) {
		return (args) -> {
/*
			Player player1 = new Player("j.bauer@ctu.gov",passwordEncoder.encode("24"));
			Player player2 = new Player("c.obraian@ctu.gov",passwordEncoder.encode("42"));
			Player player3 = new Player("kim_bauer@gmail.com",passwordEncoder.encode("kb"));
			Player player4 = new Player("t.almeida@ctu.gov",passwordEncoder.encode("mole"));
			Player player5 = new Player("d.palmer@whitehouse.gov",passwordEncoder.encode("EJEMPLO"));
			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);
			playerRepository.save(player5);
*/

		};
	}
}



